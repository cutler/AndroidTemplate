package com.cutler.template.common.media.goal;


/**
 * 同步加载时的回调。
 * @author cutler
 */
public class SyncGoal extends AbstractMediaFetchingGoal {

	private volatile boolean loadFinish;
	private volatile Object media;

	@Override
	public synchronized void onFetched(boolean success, Object media) {
		this.media = media;
		this.loadFinish = true;
		// 唤醒等待的线程。
		notifyAll();
	}

	@Override
	public void onFetchingProgress(int curSize, int totalSize) {

	}

	/**
	 * 媒体尚未完成载入时一直阻塞 (wait)，直到完成载入以后将其返回.
	 */
	public synchronized Object getMedia() {
		// 如果数据没有加载完毕，则阻塞当前线程。
		while (!loadFinish) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// 若数据加载失败，则media属性为null。
		return media;
	}

}
