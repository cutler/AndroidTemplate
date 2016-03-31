package com.cutler.template.transload.common;

import java.util.Map;

/**
 * 前端代码传递给上传/下载模块的回调接口。
 *
 * @author cutler
 */
public interface TransferObserver {

    /**
     * 文件上传/下载过程中，产生的各种事件。
     */
    enum TransferState {
        /**
         * 文件被加入到队列中
         */
        ADDED,

        /**
         * 文件被暂停
         */
        PAUSED,

        /**
         * 文件继续传输
         */
        CONTINUED,

        /**
         * 文件被从队列中被删除
         */
        DELETED,

        /**
         * 文件传输完成
         */
        FINISHED,

        /**
         * 传输进度改变
         */
        PROGRESS,

        /**
         * 上传/下载出错
         */
        ERROR, FileState;
    }

    /**
     * 传输速率
     */
    String KEY_SPEED = "speed";

    /**
     * 总大小
     */
    String KEY_TOTAL_SIZE = "totalSize";

    /**
     * 当前进度
     */
    String KEY_PROGRESS = "progress";

    /**
     * 上传/下载的文件
     */
    String KEY_FILE = "file";

    /**
     * 本地路径
     */
    String KEY_LOCAL_PATH = "localPath";

    /**
     * 异常对象
     */
    String KEY_ERROR = "error";

    /**
     * 当前状态发生变化的TransferableTask对象。
     */
    String KEY_TASK = "task";

    /**
     * 服务端返回的结果。
     */
    String KEY_RESULT = "result";

    /**
     * 当下载数据改变时下载器会在主线程中回调此方法，通知前端代码。
     *
     * @param state
     * @param params
     */
    void onTransferDataChanged(TransferState state, Map<String, Object> params);
}
