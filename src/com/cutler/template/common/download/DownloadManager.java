package com.cutler.template.common.download;

import android.text.TextUtils;

import com.cutler.template.common.Config;
import com.cutler.template.common.download.goal.DownloadObserver;
import com.cutler.template.common.download.model.DownloadFile;
import com.cutler.template.common.media.MediaManager;

/**
 * 本类负责接收外界的下载请求。
 */
public class DownloadManager {
	private static DownloadManager instance;
	// 下载器对象。
	private Downloader mDownloader;

	private DownloadManager() {
		mDownloader = new Downloader();
	}

	/**
	 * 添加一个观察者。
	 * @param observer
	 */
	public void addObserver(DownloadObserver observer) {
		mDownloader.addObserver(observer);
	}
    
	/**
	 * 删除一个观察者。
	 * @param observer
	 */
	public void removeObserver(DownloadObserver observer) {
		mDownloader.removeObserver(observer);
	}
	
	/**
	 * 更新文件的状态。
	 * @param type	操作的类型
	 * @param file	操作的文件
	 */
	public void service(int type, DownloadFile...file) {
		switch (type) {
		case Config.DownloadTypes.INIT_DOWNLOADER:
			if (!mDownloader.isRunning()) {
				mDownloader.startManage();
			}
			break;
		case Config.DownloadTypes.ADD:
			if (!TextUtils.isEmpty(file[0].getUrl()) && !mDownloader.hasTask(file[0])) {
				mDownloader.addTask(file[0]);
			}
			break;
//		case Config.DownloadTypes.CONTINUE:
//			url = intent.getStringExtra(MyIntents.URL);
//			if (!TextUtils.isEmpty(url)) {
//				mDownloader.continueTask(url);
//			}
//			break;
//		case Config.DownloadTypes.DELETE:
//			url = intent.getStringExtra(MyIntents.URL);
//			if (!TextUtils.isEmpty(url)) {
//				mDownloader.deleteTask(url);
//			}
//			break;
//		case Config.DownloadTypes.PAUSE:
//			url = intent.getStringExtra(MyIntents.URL);
//			if (!TextUtils.isEmpty(url)) {
//				mDownloader.pauseTask(url);
//			}
//			break;
//		case Config.DownloadTypes.STOP:
//			mDownloader.close();
//			// mDownloadManager = null;
//			break;
//
//		default:
//			break;
		}
	}

	public static DownloadManager getInstance() {
		if (instance == null) {
			synchronized (MediaManager.class) {
				if (instance == null) {
					instance = new DownloadManager();
				}
			}
		}
		return instance;
	}
}
