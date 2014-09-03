package com.cutler.template.common.media;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * 本类用来管理内存中缓存的媒体对象。
 */
public class MediaCache {
	/*
	 * 对于一个MediaInf对象来说，他在外界可能被两个地方引用。  一级缓存和二级缓存。
	 * 一级缓存使用lru算法来保存MediaInf对象，由于lru的特性，很久未被使用的元素将会被从内部踢出。
	 * 二级缓存使用HashMap来保存，当用户访问了一个从一级缓存中踢出的元素时，会再从二级缓存中查找。 由于二级缓存的value是SoftReference类型的，因此不会影响MediaInfo对象的回收。
	 */
	// 一级缓存。
	private MediaLruCache mL1Cache = new MediaLruCache();
	// 二级缓存。
	private HashMap<String,SoftReference<MediaInfo>> mL2cache = new HashMap<String, SoftReference<MediaInfo>>();
	
	/**
	 * 尝试从内存中读取指定的媒体对象。
	 * @param desc
	 * @return
	 */
	public Object get(String desc){
		MediaInfo mediaInfo = mL1Cache.get(desc);
		if (mediaInfo == null) {
			synchronized (this) {
				mediaInfo = mL1Cache.get(desc);
				if (mediaInfo == null) {
					SoftReference<MediaInfo> sref = mL2cache.get(desc);
					if (sref != null) {
						mediaInfo = sref.get();
                        if (mediaInfo != null) {
                            // 重新将元素加入到一级缓存中。
                            mL1Cache.set(desc, mediaInfo);
                        }
					}
				}
			}
		}
		return mediaInfo == null ? null : mediaInfo.getMedia();
	}
	
	/**
	 * 从L1Cache中清除多余的数据，使其保持在指定大小。
	 * @param size
	 */
	public void reserveMemory(int size) {
		mL1Cache.trimToSize(MediaLruCache.getCacheSize()- size);
	}
	
	/**
	 * 将参数媒体对象，加入到内存缓存中。
	 * @param desc
	 * @param media
	 * @param size
	 */
	public void add(String desc,Object media){
		MediaInfo mediaInfo = new MediaInfo(media, MediaSizeEstimator.estimateMediaSize(media));
		SoftReference<MediaInfo> mediaInfoRef = new SoftReference<MediaInfo>(mediaInfo);
		synchronized (this) {
			mL1Cache.put(desc, mediaInfo);
			mL2cache.put(desc, mediaInfoRef);
		}
	}
	
	/**
	 * 清空内存缓存中的所有数据。
	 */
	public void evictAll() {
		synchronized (this) {
			mL1Cache.evictAll();
			mL2cache.clear();
		}
	}
}
