package com.cutler.template.transload.common;

public interface TransferableTask {

    // 更新进度的时间间隔（毫秒），每载几kb的数据就通知一次太过于频繁。
    int NOTIFY_PROGRESS_INTERVAL = 1000;

    /**
     * 开始执行任务。
     */
    void startTask();

    /**
     * 暂停任务。
     */
    void pauseTask();

    /**
     * 停止任务。
     */
    void stopTask();

    /**
     * 获取当前任务对应的文件。
     *
     * @return
     */
    TransferableFile getTransferableFile();
}
