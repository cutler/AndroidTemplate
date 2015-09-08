package com.cutler.template.common.transloader.upload;

import android.widget.Toast;

import com.cutler.template.Template;
import com.cutler.template.common.transloader.common.AbstractLoader;
import com.cutler.template.common.transloader.common.TransferObserver;
import com.cutler.template.common.transloader.common.TransferObserver.TransferState;
import com.cutler.template.common.transloader.common.TransferableFile;
import com.cutler.template.common.transloader.common.TransferableTask;
import com.cutler.template.common.transloader.upload.model.HttpURLConnectionUploadTask;
import com.cutler.template.common.transloader.upload.model.UploadFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class Uploader extends AbstractLoader {

    /*
     * 接收上传模块传递过来的回调
     */
    private TransferObserver observer = new TransferObserver() {
        // TransferableTask会在主线程中调用本方法。
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
     * 当上传器启动之前，先从本地数据库中加载上传列表。
     */
    protected void onPreStartLoader() {
    }

    ;

    @Override
    protected boolean verifyTransferableFileForAdd(TransferableFile file) {
        UploadFile localFile = (UploadFile) file;
        return new File(localFile.getLocalPath()).isFile();
    }

    @Override
    protected TransferableTask createTransferableTask(TransferableFile file) throws Exception {
        UploadFile localFile = (UploadFile) file;
        TransferableTask task = null;
        // 依据uploadType来决定使用什么方式上传文件。
        switch (localFile.getUploadType()) {
            case NORMARL:
                task = new HttpURLConnectionUploadTask(localFile, observer);
                break;
        }
        return task;
    }

    protected void handleProgressChanged(Map<String, Object> params) {
        HttpURLConnectionUploadTask task = (HttpURLConnectionUploadTask) params.get(TransferObserver.KEY_TASK);
        params.put(TransferObserver.KEY_TOTAL_SIZE, task.getTotalSize());
        params.put(TransferObserver.KEY_PROGRESS, task.getUploadPercent());
        params.put(TransferObserver.KEY_FILE, task.getUploadFile());
        notifyObservers(TransferState.PROGRESS, params);
    }

    protected void handleFinished(Map<String, Object> params) {
        HttpURLConnectionUploadTask task = (HttpURLConnectionUploadTask) params.get(TransferObserver.KEY_TASK);
        if (getDoingTaskList().contains(task)) {
            getDoingTaskList().remove(task);
            params.put(TransferObserver.KEY_FILE, task.getUploadFile());
            params.put(TransferObserver.KEY_RESULT, task.getResult());
            notifyObservers(TransferState.FINISHED, params);
        }
    }

    protected void handleError(Map<String, Object> params) {
        Throwable error = (Throwable) params.get(TransferObserver.KEY_ERROR);
        HttpURLConnectionUploadTask task = (HttpURLConnectionUploadTask) params.get(TransferObserver.KEY_TASK);
        if (error == null) {
            error = new RuntimeException("unkown error");
        }
        params.put(TransferObserver.KEY_FILE, task.getUploadFile());
        notifyObservers(TransferState.ERROR, params);
        error.printStackTrace();
        // 同时弹出提示
        Toast.makeText(Template.getApplication(), "Error: \n" + error.getMessage(), Toast.LENGTH_LONG).show();
    }

    protected void handlePaused(Map<String, Object> params) {
        HttpURLConnectionUploadTask task = (HttpURLConnectionUploadTask) params.get(TransferObserver.KEY_TASK);
        params.put(TransferObserver.KEY_FILE, task.getUploadFile());
        notifyObservers(TransferState.PAUSED, params);
    }

    @Override
    protected void onTransferableFileAddedTask(TransferableFile file) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(TransferObserver.KEY_FILE, file);
        notifyObservers(TransferState.ADDED, params);
        // TODO
    }

    @Override
    protected void onDeleteCacheFile(TransferableTask task) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onDeletedTransferableTask(TransferableTask ptask) {
        getOtherTaskList().add(ptask);
        UploadFile localFile = (UploadFile) ptask.getTransferableFile();
        // 依据uploadType来决定使用什么方式上传文件。
        switch (localFile.getUploadType()) {
            case NORMARL:
                HttpURLConnectionUploadTask task = (HttpURLConnectionUploadTask) ptask;
                Map<String, Object> params = new HashMap<String, Object>();
                params.put(TransferObserver.KEY_FILE, task.getUploadFile());
                notifyObservers(TransferState.DELETED, params);
                break;
        }
    }

}
