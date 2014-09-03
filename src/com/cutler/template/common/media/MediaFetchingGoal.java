package com.cutler.template.common.media;

/**
 * 当媒体的下载状态改变时，调用此回调接口中的方法。
 */
public abstract class MediaFetchingGoal {

	/**
	 * 当媒体加载完成时，回调此方法。
	 * 
	 * @param success
	 * @param media
	 */
	public abstract void onFetched(boolean success, Object media);

	/**
	 * 当媒体加载的进度改变时，回调此方法。
	 * 
	 * @param curSize
	 * @param totalSize
	 */
	public abstract void onFetchingProgress(int curSize, int totalSize);
	
	/**
	 * 标志当前Goal是否处于活动状态(未被cancel)。
	 * @return
	 */
	public boolean isActive(){
		return true;
	}
}
