package com.cutler.template.common.download.goal;

import java.util.Map;

public interface DownloadObserver {

	public static String KEY_DOWNLOADING_LIST = "downloadingList";
	
	/**
	 * 下载速率
	 */
	public static String KEY_SPEED = "speed";
	
	/**
	 * 总大小
	 */
	public static String KEY_TOTAL_SIZE = "totalSize";
	
	/**
	 * 下载进度
	 */
	public static String KEY_PROGRESS = "progress";
	
	/**
	 * 下载的文件
	 */
	public static String KEY_FILE = "file";
	
	/**
	 * 本地路径
	 */
	public static String KEY_LOCAL_PATH = "localPath";
	
	/**
	 * 操作的类型。
	 */
	public static String KEY_TYPE = "type";
	
	/**
	 * 是否是从暂停状态转为开始状态。
	 */
	public static String KEY_IS_PAUSED = "isPaused";
	
	/**
	 *  当下载数据改变时下载器会在主线程中回调此方法。
	 *  @param downloadList 下载列表
	 *  @param curFile 当前数据改变的文件对象。
	 */
	public void onDownloadDataChanged(Map<String, Object> params);
}
