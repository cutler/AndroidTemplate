package com.cutler.template.common;

/**
 * 本类用来定义全局的常量
 * @author cutler
 *
 */
public class Config {
	// 配置文件的名称。
 	public static final String KEY_SHARE_PRE_FILE_NAME = "share_prefile";
 	// 本地时间与服务器时间差。
 	public static final String KEY_TIME_OFFSET = "timeOffset";
	// 本机的经纬度。
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_LATITUDE = "latitude";
	// 当获取到无效的经纬度时，将经纬度的值改为此值。
	public static final String INVALID_LOCATION = "361";
	// 媒体下载相关 
	public static final int DISK_CACHE_INDEX = 0;
	public static final int DISK_CACHE_SIZE = 50 * 1024 * 1024;
	public static final int IO_BUFFER_SIZE = 8 * 1024;
	public static final int HTTP_DOWNLOAD_THREAD_POOL_SIZE = 3;
	// 本地的缓存目录
	public static final String CACHE_MEDIA = "media";
	public static final String CACHE_APK = "apk";
	
}
