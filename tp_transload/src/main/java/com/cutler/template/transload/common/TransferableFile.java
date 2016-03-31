package com.cutler.template.transload.common;

public abstract class TransferableFile {
    /**
     * 返回当前文件的唯一标识。
     */
    public abstract String getUniqueNumber();

    /**
     * 文件当前的传输状态
     *
     * @author cutler
     */
    public enum FileState {
        /**
         * 等待传输
         */
        WAITING,

        /**
         * 正在传输
         */
        STARTED,

        /**
         * 暂停传输
         */
        PAUSED,

        /**
         * 停止传输
         */
        DELETED,

        /**
         * 传输完成
         */
        FINISHED,

        /**
         * 传输失败
         */
        ERROR;
    }

}
