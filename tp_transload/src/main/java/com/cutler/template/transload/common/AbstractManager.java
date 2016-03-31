package com.cutler.template.transload.common;

import android.text.TextUtils;

import com.cutler.template.base.Template;


public abstract class AbstractManager<T extends AbstractLoader> {

    protected T mLoader;

    public AbstractManager(T mLoader) {
        this.mLoader = mLoader;
        service(TransloadAction.INITIALIZE);
    }

    /**
     * 添加一个观察者。
     *
     * @param observer
     */
    public void addObserver(TransferObserver observer) {
        mLoader.addObserver(observer);
    }

    /**
     * 删除一个观察者。
     *
     * @param observer
     */
    public void removeObserver(TransferObserver observer) {
        mLoader.removeObserver(observer);
    }

    /**
     * 业务方法，完成上传或下载操作。
     *
     * @param type
     * @param files
     */
    public void service(final int type, final TransferableFile... files) {
        // 保证子类的doService方法在主线程中被调用。
        Template.getMainHandler().post(new Runnable() {
            public void run() {
                switch (type) {
                    case TransloadAction.INITIALIZE:        // 开启Loader
                        if (mLoader != null && !mLoader.isRunning()) {
                            mLoader.startLoader();
                        }
                        break;
                    case TransloadAction.TERMINATE:            // 终止Loader
                        if (mLoader != null) {
                            mLoader.stopLoader();
                            mLoader = null;
                        }
                        break;
                    case TransloadAction.ADD:
                        handleAddAction(files);
                        break;
                    case TransloadAction.PAUSE:
                        handlePauseAction(files);
                        break;
                    case TransloadAction.PAUSE_ALL:
                        handlePauseAllAction();
                        break;
                    case TransloadAction.CONTINUE:
                        handleContinueAction(files);
                        break;
                    case TransloadAction.DELETE:
                        handleDeleteAction(files);
                        break;
                    default:
                        // 其他操作则交给子类处理
                        doService(type, files);
                        break;
                }
            }
        });
    }

    /**
     * 在主线程中执行操作。
     *
     * @param type
     * @param file
     */
    protected abstract void doService(int type, TransferableFile... file);

    /*
     * 添加任务到队列中
     */
    protected void handleAddAction(TransferableFile... files) {
        if (files == null) {
            return;
        }
        for (TransferableFile file : files) {
            if (!TextUtils.isEmpty(file.getUniqueNumber()) && !mLoader.hasTransferableFile(file)) {
                mLoader.addTransferableFile(file);
            }
        }
    }

    /*
     * 暂停任务
     */
    protected void handlePauseAction(TransferableFile[] files) {
        if (files == null) {
            return;
        }
        for (TransferableFile file : files) {
            if (!TextUtils.isEmpty(file.getUniqueNumber())) {
                mLoader.pauseTransferableFile(file);
            }
        }
    }

    /*
     * 继续任务
     */
    protected void handleContinueAction(TransferableFile[] files) {
        if (files == null) {
            return;
        }
        for (TransferableFile file : files) {
            if (!TextUtils.isEmpty(file.getUniqueNumber())) {
                mLoader.continueTransferableFile(file);
            }
        }
    }

    /*
     * 暂停所有任务
     */
    protected void handlePauseAllAction() {
        mLoader.pauseAllTransferableTask();
    }

    /*
     * 立刻停止删除任务
     */
    protected void handleDeleteAction(TransferableFile[] files) {
        if (files == null) {
            return;
        }
        for (TransferableFile file : files) {
            if (!TextUtils.isEmpty(file.getUniqueNumber())) {
                mLoader.deleteTransferableFile(file);
            }
        }
    }

    /**
     * 上传（或下载）模块所支持的动作。
     */
    public class TransloadAction {
        /**
         * 初始化上传（或下载）模块
         */
        public static final int INITIALIZE = 0;

        /**
         * 添加一个新的上传（或下载）任务
         */
        public static final int ADD = 1;

        /**
         * 暂停一个上传（或下载）任务
         */
        public static final int PAUSE = 2;

        /**
         * 暂停所有上传（或下载）任务（包括正在进行的和等待进行的）
         */
        public static final int PAUSE_ALL = 3;

        /**
         * 继续一个上传（或下载）任务
         */
        public static final int CONTINUE = 4;

        /**
         * 删除一个上传（或下载）任务
         */
        public static final int DELETE = 6;

        /**
         * 停止上传（或下载）模块
         */
        public static final int TERMINATE = 7;

    }

}
