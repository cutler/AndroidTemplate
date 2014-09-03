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
}
