package com.cutler.template.common.download.goal;

import com.cutler.template.common.download.model.DownloadTask;

/**
 * 下载相关的回调方法。
 */
public class DownloadTaskListener {

	/**
	 * 下载进度改变时回调此方法。
	 * @param task
	 */
	public void onProgressChanged(DownloadTask task) {

	}

	/**
	 * 下载完成回调此方法。
	 * @param task
	 */
	public void onFinished(DownloadTask task) {

	}

	/**
	 * 下载之前回调此方法。
	 * @param task
	 */
	public void onPreExecute(DownloadTask task) {

	}

	/**
	 * 下载出错时回调此方法。
	 * @param task
	 * @param error
	 */
	public void onError(DownloadTask task, Throwable error) {

	}

	/**
	 * 取消下载时回调此方法。
	 * @param task
	 */
	public void onCanceled(DownloadTask task) {

	}
	
	/**
	 * 下载暂停时回调此方法。
	 * @param task
	 */
	public void onPaused(DownloadTask task) {

	}
}
