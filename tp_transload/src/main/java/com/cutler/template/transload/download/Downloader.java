package com.cutler.template.transload.download;

import android.widget.Toast;

import com.cutler.template.base.Template;
import com.cutler.template.transload.common.AbstractLoader;
import com.cutler.template.transload.common.TransferObserver;
import com.cutler.template.transload.common.TransferableFile;
import com.cutler.template.transload.common.TransferableTask;
import com.cutler.template.transload.download.model.DownloadFile;
import com.cutler.template.transload.download.model.DownloadListDAO;
import com.cutler.template.transload.download.model.DownloadTask;
import com.cutler.template.base.util.io.StorageUtils;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Downloader extends AbstractLoader {

    /*
     * 接收下载模块传递过来的回调
     */
    private TransferObserver observer = new TransferObserver() {
        // DownloadTask会在主线程中调用本方法。
        public void onTransferDataChanged(TransferState state,
                                          Map<String, Object> params) {
            switch (state) {
                case PROGRESS:
                    handleProgressChanged(params);
                    break;
                case FINISHED:
                    handleFinished(params);
                    break;
                case ERROR:
                    handleError(params);
                    break;
                case DELETED:
                    onDeletedTransferableTask((TransferableTask) params.get(TransferObserver.KEY_TASK));
                    break;
                case PAUSED:
                    handlePaused(params);
                    break;
            }
        }
    };

    /**
     * 当下载器启动之前，先从本地数据库中加载下载列表。
     */
    protected void onPreStartLoader() {
        try {
            List<DownloadFile> list = DownloadListDAO.getInstance().findAllDownloadFile();
            for (DownloadFile file : list) {
                switch (file.getState()) {
                    case STARTED:
                    case WAITING:
                        file.setState(TransferableFile.FileState.WAITING);
                        getWaitingTaskQueue().offer(createTransferableTask(file));
                        break;
                    case PAUSED:
                        getPausingTaskList().add(createTransferableTask(file));
                        break;
                    case FINISHED:
                    case ERROR:
                    case DELETED:
                        getOtherTaskList().add(createTransferableTask(file));
                        break;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    ;

    @Override
    protected boolean verifyTransferableFileForAdd(TransferableFile file) {
        boolean retVal = true;
        if (!StorageUtils.isSdCardWriteAble()) {
            Toast.makeText(Template.getApplication(), "未发现SD卡", Toast.LENGTH_LONG).show();
            retVal = false;
        }
        return retVal;
    }

    @Override
    protected TransferableTask createTransferableTask(TransferableFile downloadFile) throws MalformedURLException {
        String localSavePath = DownloadManager.getInstance().getLocalDownloadFolder().getAbsolutePath();
        return new DownloadTask((DownloadFile) downloadFile, localSavePath, observer);
    }

    /*
     * 下载进度改变。
     */
    private void handleProgressChanged(Map<String, Object> params) {
        DownloadTask task = (DownloadTask) params.get(TransferObserver.KEY_TASK);
        // 通知所有观察者，数据已经改变。
        // params.put(DownloadObserver.KEY_SPEED, task.getDownloadSpeed() + "kbps | "
        //        + task.getDownloadSize() + " / " + task.getTotalSize());
        params.put(TransferObserver.KEY_TOTAL_SIZE, task.getTotalSize());
        params.put(TransferObserver.KEY_PROGRESS, task.getDownloadPercent());
        params.put(TransferObserver.KEY_FILE, task.getDownloadFile());
        notifyObservers(TransferObserver.TransferState.PROGRESS, params);
    }

    /*
     * 下载完成
     */
    protected void handleFinished(Map<String, Object> params) {
        DownloadTask task = (DownloadTask) params.get(TransferObserver.KEY_TASK);
        if (getDoingTaskList().contains(task)) {
            getDoingTaskList().remove(task);
            getOtherTaskList().add(task);
            params.put(TransferObserver.KEY_FILE, task.getDownloadFile());
            params.put(TransferObserver.KEY_LOCAL_PATH, task.getDownloadFileLocalPath());
            notifyObservers(TransferObserver.TransferState.FINISHED, params);
        }
    }

    /*
     * 下载出错
     */
    protected void handleError(Map<String, Object> params) {
        Throwable error = (Throwable) params.get(TransferObserver.KEY_ERROR);
        if (error == null) {
            error = new RuntimeException("unkown error");
        }
        final DownloadTask task = (DownloadTask) params.get(TransferObserver.KEY_TASK);
        final DownloadFile file = task.getDownloadFile();
        // 若当下载出错的时候不需要重试
        if (!file.canRetryDownload()) {
            getDoingTaskList().remove(task);
            getOtherTaskList().add(task);
            params.put(TransferObserver.KEY_FILE, file);
            notifyObservers(TransferObserver.TransferState.ERROR, params);
            // 同时弹出提示
            Toast.makeText(Template.getApplication(), "Error: \n" + error.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            file.iterateRetryTimes();
            Template.getMainHandler().postDelayed(new Runnable() {
                public void run() {
                    try {
                        getWaitingTaskQueue().offerFirst(createTransferableTask(file));
                        getDoingTaskList().remove(task);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }, DownloadFile.RETRY_TIME_GAP);
        }
    }

    /*
     * 下载暂停
     */
    protected void handlePaused(Map<String, Object> params) {
        DownloadTask task = (DownloadTask) params.get(TransferObserver.KEY_TASK);
        params.put(TransferObserver.KEY_FILE, task.getDownloadFile());
        params.put(TransferObserver.KEY_LOCAL_PATH, task.getDownloadFileLocalPath());
        notifyObservers(TransferObserver.TransferState.PAUSED, params);
    }

    @Override
    protected void onTransferableFileAddedTask(TransferableFile file) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(TransferObserver.KEY_FILE, file);
        notifyObservers(TransferObserver.TransferState.ADDED, params);
        // 加入数据库
        DownloadListDAO.getInstance().doCreateOrUpdate((DownloadFile) file);
    }

    /*
     * 下载删除
     */
    @Override
    protected void onDeletedTransferableTask(TransferableTask pTask) {
        getDoingTaskList().remove(pTask);
        getOtherTaskList().add(pTask);
        DownloadTask task = (DownloadTask) pTask;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(TransferObserver.KEY_FILE, task.getDownloadFile());
        params.put(TransferObserver.KEY_LOCAL_PATH, task.getDownloadFileLocalPath());
        notifyObservers(TransferObserver.TransferState.DELETED, params);
    }

}
