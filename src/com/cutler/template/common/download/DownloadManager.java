package com.cutler.template.common.download;

import java.io.File;
import java.net.URL;

import android.text.TextUtils;

import com.cutler.template.MainApplication;
import com.cutler.template.common.Config;
import com.cutler.template.common.download.goal.DownloadObserver;
import com.cutler.template.common.download.model.DownloadFile;
import com.cutler.template.common.download.model.Downloader;
import com.cutler.template.util.StorageUtils;

/**
 * 本类负责接收外界的下载请求。
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
	 * 删除本地的缓存文件。
	 * @param url
	 */
	public boolean deleteLocalFileByUrl(String url){
		try {
			URL localUrl = new URL(url);
			String fileName = new File(localUrl.getFile()).getName();
			File file = new File(StorageUtils.getDiskCacheDir(MainApplication.getInstance(), Config.CACHE_APK), fileName);
			return file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 更新文件的状态。
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
			if (!TextUtils.isEmpty(file[0].getUrl()) && !mDownloader.hasTask(file[0])) {
				mDownloader.addTask(file[0]);
			}
			break;
		case DownloadTypes.PAUSE:
			if (!TextUtils.isEmpty(file[0].getUrl())) {
				mDownloader.pauseTask(file[0]);
			}
			break;
		case DownloadTypes.CONTINUE:
			if (!TextUtils.isEmpty(file[0].getUrl())) {
				mDownloader.continueTask(file[0]);
			}
			break;
		case DownloadTypes.DELETE:
			if (!TextUtils.isEmpty(file[0].getUrl())) {
				mDownloader.deleteTask(file[0], file[0].isDeleteCache());
			}
			break;
//		case Config.DownloadTypes.STOP:
//			mDownloader.close();
//			// mDownloadManager = null;
//			break;
		}
	}
	
	/**
	 * 下载模块相关的常量
	 */
	public class DownloadTypes{
		/**
		 *  初始化下载器
		 */
		public static final int INIT_DOWNLOADER = 0;
		
		/**
		 * 下载进度改变
		 */
		public static final int PROGRESS = 1;
		
		/**
		 * 添加一个新的下载任务
		 */
		public static final int ADD = 6;
		
		/**
		 * 暂停下载
		 */
		public static final int PAUSE = 3;
		
		/**
		 * 继续下载
		 */
		public static final int CONTINUE = 5;
		
		/**
		 * 下载完成
		 */
		public static final int COMPLETE = 2;
		
		/**
		 * 删除下载
		 */
		public static final int DELETE = 4;
		
		public static final int STOP = 7; 
		
		/**
		 * 下载出错
		 */
		public static final int ERROR = 9;
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
