package com.cutler.template.common.download;

import android.text.TextUtils;

import com.cutler.template.common.download.goal.DownloadObserver;
import com.cutler.template.common.download.model.DownloadFile;
import com.cutler.template.common.download.model.Downloader;

/**
 * 本类负责接收外界的下载请求。
 * @author cutler
 */
public class DownloadManager {
	private static DownloadManager instance;
	// 下载器对象。
	private Downloader mDownloader;
	private DownloadManager() {
		mDownloader = new Downloader();
		service(DownloadTypes.INIT_DOWNLOADER);
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
	 * 下载模块提供的相关操作。
	 * @param type	操作的类型
	 * @param file	操作的文件
	 */
	public void service(int type, DownloadFile...file) {
		switch (type) {
		case DownloadTypes.INIT_DOWNLOADER:
			if (!mDownloader.isRunning()) {
				mDownloader.startManage();
			}
			break;
		case DownloadTypes.ADD:
			if (file[0] != null && !TextUtils.isEmpty(file[0].getUrl()) && !mDownloader.hasTask(file[0])) {
				mDownloader.addTask(file[0]);
			}
			break;
		case DownloadTypes.PAUSE:
			if (file[0] != null && !TextUtils.isEmpty(file[0].getUrl())) {
				mDownloader.pauseTask(file[0]);
			}
			break;
		case DownloadTypes.PAUSE_ALL:
			mDownloader.pauseAllTask();
			break;
		case DownloadTypes.CONTINUE:
			if (file[0] != null && !TextUtils.isEmpty(file[0].getUrl())) {
				mDownloader.continueTask(file[0]);
			}
			break;
		case DownloadTypes.DELETE:
			if (file[0] != null && !TextUtils.isEmpty(file[0].getUrl())) {
				mDownloader.deleteTask(file[0], file[0].isDeleteCache());
			}
			break;
		case DownloadTypes.STOP:
			mDownloader.close();
			instance = null;
			break;
		}
	}
	
	/**
	 * 下载模块的参数。
	 */
	public class DownloadTypes{
		/**
		 *  初始化下载器
		 */
		public static final int INIT_DOWNLOADER = 0;
		
		/**
		 * 添加一个新的下载任务
		 */
		public static final int ADD = 1;
		
		/**
		 * 暂停下载
		 */
		public static final int PAUSE = 2;
		
		/**
		 * 暂停所有下载（包括正在下载的和等待下载的）
		 */
		public static final int PAUSE_ALL = 3;
		
		/**
		 * 继续下载
		 */
		public static final int CONTINUE = 4;
		
		/**
		 * 下载完成
		 */
		public static final int COMPLETE = 5;
		
		/**
		 * 删除下载
		 */
		public static final int DELETE = 6;
		
		/**
		 * 停止下载器。
		 */
		public static final int STOP = 7; 
		
	}
	
	/**
	 * 下载的状态。
	 */
	public class DownloadStates {
		
		/**
		 * 下载进度改变
		 */
		public static final int PROGRESS = 1001;
		
		/**
		 * 下载出错
		 */
		public static final int ERROR = 1002;
		
	}
	
	public static DownloadManager getInstance() {
		if (instance == null) {
			synchronized (DownloadManager.class) {
				if (instance == null) {
					instance = new DownloadManager();
				}
			}
		}
		return instance;
	}

}
