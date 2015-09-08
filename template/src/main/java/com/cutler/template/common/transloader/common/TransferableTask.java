package com.cutler.template.common.transloader.common;

public interface TransferableTask {

    // 更新进度的时间间隔（毫秒），每载几kb的数据就通知一次太过于频繁。
    public final static int NOTIFY_PROGRESS_INTERVAL = 1000;

    /**
     * 开始执行任务。
     */
    public void startTask();

    /**
     * 暂停任务。
     */
    public void pauseTask();

    /**
     * 停止任务。
     */
    public void stopTask();

    /**
     * 获取当前任务对应的文件。
     *
     * @return
     */
    public TransferableFile getTransferableFile();
}
