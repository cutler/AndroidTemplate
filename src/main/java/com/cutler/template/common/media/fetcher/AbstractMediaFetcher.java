package com.cutler.template.common.media.fetcher;

import com.cutler.template.common.media.MediaManager;

import android.content.Context;

/**
 * 媒体文件的加载器。 抽象类。
 * @author cutler
 */
public abstract class AbstractMediaFetcher {

	private Context ctx;
	private String desc;

	public AbstractMediaFetcher(Context ctx, String desc) {
		this.ctx = ctx;
		this.desc = desc;
	}
	
	/**
	 * 执行加载数据的操作。
	 */
	public abstract void fetch();

	/**
	 * 通知MediaManager类数据已经加载完毕。
	 */
	public void notifyFetched(boolean success, Object media) {
		MediaManager.getInstance().notifyFetched(desc, success, media);
	}

	/**
	 * 通知MediaManager类数据加载的进度已经改变。
	 */
	public void notifyFetchingProgress(int currSize, int totalSize) {
		MediaManager.getInstance().notifyFetchingProgress(desc, currSize, totalSize);
	}
	
	/**
	 * 估计即将加载的资源所需要占据的内存大小。 默认返回1M。
	 */
	public int getEstimatedMemorySize() {
		return 1024 * 1024;
	}
	
	public boolean checkToCancel(){
		return MediaManager.getInstance().checkToCancel(desc);
	}

	public Context getContext() {
		return ctx;
	}

	public String getDesc() {
		return desc;
	}
	
	/**
	 * 去掉desc开头的约束。 如 gif:aaa 返回值为 aaa
	 */
	public String trimDescSchema(){
		String retVal = null;
		if(desc != null){
			int index = desc.indexOf(":");
			if(index != -1){
				retVal = desc.substring(index+1);
			}
		}
		return retVal;
	}
}
