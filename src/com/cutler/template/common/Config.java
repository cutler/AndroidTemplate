package com.cutler.template.common;

/**
 * 本类用来定义全局的常量
 * @author cutler
 *
 */
public class Config {
	// 配置文件的名称。
 	public static final String KEY_SHARE_PRE_FILE_NAME = "share_prefile";
 	// 服务端返回的“推荐列表”，在本地保存的名字。
 	public static final String KEY_APPINFO_JSON = "appInfo.json";
 	// 本地时间与服务器时间差。
 	public static final String KEY_TIME_OFFSET = "timeOffset";
	// 媒体下载相关 
	public static final int DISK_CACHE_INDEX = 0;
	public static final int DISK_CACHE_SIZE = 50 * 1024 * 1024;
	public static final int IO_BUFFER_SIZE = 8 * 1024;
	public static final int HTTP_DOWNLOAD_THREAD_POOL_SIZE = 3;
	// 本地的缓存目录
	public static final String CACHE_MEDIA = "media";
	public static final String CACHE_APK = "apk";
	
	/**
	 * 下载模块相关的常量
	 */
	public class DownloadTypes{
		/**
		 *  初始化下载器。
		 */
		public static final int INIT_DOWNLOADER = 0;
		
		/**
		 * 下载进度改变。
		 */
		public static final int PROGRESS = 1;
		
		public static final int COMPLETE = 2;
		public static final int PAUSE = 3;
		public static final int DELETE = 4;
		public static final int CONTINUE = 5;
		public static final int ADD = 6;
		public static final int STOP = 7; 
		public static final int ERROR = 9;
	}
}
