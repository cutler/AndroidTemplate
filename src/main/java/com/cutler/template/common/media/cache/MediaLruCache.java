package com.cutler.template.common.media.cache;

import com.cutler.template.MainApplication;
import com.cutler.template.common.media.model.MediaInfo;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.util.LruCache;

/**
 * 自定义了LruCache类。
 * @author cutler
 */
public class MediaLruCache extends LruCache<String, MediaInfo> {
	// 内存缓存的最大容量。
	private static int cacheSize;	
	private static final int M = 1024 * 1024;
	private static final int CACHE_SIZE_LOW = 10 * M;
	private static final int CACHE_SIZE_HIGH = 42 * M;
	
	static {
		long memory = ((ActivityManager) MainApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() * 1024 * 1024;
		if (memory < 64 * M) {
			cacheSize = (int) (CACHE_SIZE_LOW * memory / (64 * M));
		} else {
			cacheSize = (int) (CACHE_SIZE_LOW + (CACHE_SIZE_HIGH - CACHE_SIZE_LOW) * (memory - 64*M) / (64 * M));
		}
	}
	public MediaLruCache() {
		super(cacheSize);
	}
	
	public static int getCacheSize() {
		return cacheSize;
	}

	/**
	 * 更新缓存中的媒体对象。
	 */
	public void set(String desc,MediaInfo mediaInfo){
		put(desc, mediaInfo);
	}
	
	@Override
	protected int sizeOf(String key, MediaInfo value) {
		int size = 1;
		if(value != null){
			size =  value.getSize();
		}
		return size;
	}
}
