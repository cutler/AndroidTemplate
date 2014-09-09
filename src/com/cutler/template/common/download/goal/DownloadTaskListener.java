
package com.cutler.template.common.download.goal;

import com.cutler.template.common.download.DownloadTask;

/**
 * 下载相关的回调方法。
 */
public interface DownloadTaskListener {

    public void updateProgress(DownloadTask task);

    public void finishDownload(DownloadTask task);

    public void preDownload(DownloadTask task);

    public void errorDownload(DownloadTask task, Throwable error);
}
