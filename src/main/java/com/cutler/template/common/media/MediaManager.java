package com.cutler.template.common.media;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.cutler.template.common.media.cache.MediaCache;
import com.cutler.template.common.media.cache.MediaSizeEstimator;
import com.cutler.template.common.media.fetcher.AbstractMediaFetcher;
import com.cutler.template.common.media.fetcher.HttpMediaFetcher;
import com.cutler.template.common.media.fetcher.MediaFetcherFactory;
import com.cutler.template.common.media.goal.AbstractMediaFetchingGoal;
import com.cutler.template.common.media.goal.ImageViewGoal;
import com.cutler.template.common.media.goal.SyncGoal;
import com.cutler.template.common.media.model.MediaFetchingJob;
import com.jakewharton.disklrucache.DiskLruCache;

/**
 * 媒体加载类，目前可以从res、http中加载图片。
 * @author cutler
 */
public class MediaManager {
	// 单例对象。
	private static MediaManager instance;
	// 资源文件的schema
	public static final String RES_SCHEMA = "res:";
	// http文件的schema
	public static final String HTTP_SCHEMA = "http:";
	// 保存当前正在加载的所有工作。
	private Map<String,MediaFetchingJob> workers = new HashMap<String,MediaFetchingJob>();
	// 具有单个线程的线程池，不用Thread是因为这样可以减少线程创建时的消耗。
	private ExecutorService singleThreadPool;
	// 内存缓存。
	private MediaCache memoryCache;
	/**
	 * @return 单例对象。
	 */
	public static MediaManager getInstance(){
		if(instance == null){
			synchronized (MediaManager.class) {
				if(instance == null){
					instance = new MediaManager();
				}
			}
		}
		return instance;
	}
	
	private MediaManager() {
		memoryCache = new MediaCache();
		singleThreadPool = Executors.newSingleThreadExecutor();
	}
	
	/**
	 * 异步加载媒体文件。
	 * @param desc 要加载的媒体的唯一标识。
	 * @param goal 回调接口。
	 * @param bypass 是否在加载完毕后将其缓存在内存中。
	 */
	public void loadMedia(Context ctx, final String desc, AbstractMediaFetchingGoal goal, boolean bypass){
		// 尝试从内存缓存中读取。
		Object media = memoryCache.get(desc);
		if(media == null) {
			synchronized (this) {
				media = memoryCache.get(desc);
				// 若内存缓存中不存在，则执行加载。
				if (media == null) {
					MediaFetchingJob job = workers.get(desc);
					// 若已经有任务在进行了，则将goal添加到该任务的回调列表里，然后直接返回。
					if (job != null) {	
						job.getGoals().add(goal);
					} else {
						job = new MediaFetchingJob(desc,goal,bypass);
						workers.put(desc, job);
						// 依据要加载的媒体的类型，来创建不同的媒体加载器。
						final AbstractMediaFetcher fetcher = MediaFetcherFactory.createFetcher(ctx, desc) ;
						if(fetcher != null){	
							// 在内存缓存中腾出一定的空间，以便加载新任务。
							reserveMemory(fetcher.getEstimatedMemorySize());
							// 开启一个Job，执行加载操作。
							singleThreadPool.execute(new Runnable() {
								public void run() {
									// 若任务在开始之前，没有被cancel。
									if (!checkToCancel(desc)) {
										fetcher.fetch();
									}
								}
							});
						}
					}
					return;
				}
			}
		}
		// 若内存中已经存在了该资源，则直接通知数据加载完成。
		if (goal != null) {	
			goal.onFetched(true, media);
		}
	}
	
	/**
	 * 同步加载（即阻塞当前线程，直到媒体加载完毕）媒体文件。
	 */
	public Object getMedia(Context ctx, String desc, boolean bypass) {
		Object media = memoryCache.get(desc);
		if (media == null) {
			SyncGoal goal = new SyncGoal();
			loadMedia(ctx, desc, goal, bypass);
			media = goal.getMedia();
		}
		return media;
	}
	
	/**
	 * 异步加载图片文件。
	 * 图片下载完毕后，会自动为ImageView设置进去。
	 * 图片下载的中途，也会不断的调用ImageLoaderCallback的方法，通知外界加载的进度改变。
	 */
	public void loadImage(Context ctx, String desc, ImageView imageView, ImageLoaderCallback callback, boolean bypass, int defImgId) {
		Object media = memoryCache.get(desc);
		if (media == null) {
			// 若内存缓存中不存在该图片，则执行加载。
			loadMedia(ctx, desc, new ImageViewGoal(desc, imageView, callback, defImgId), bypass);
		} else {
			// 若内存中存在，则将该图片设置到ImageView中。
			ImageViewGoal.loadImageDirectly(desc, imageView, (Drawable) media);
			if (callback != null) {
				// 通知前端，加载完成。
				callback.onImageLoadFinish(true);
			}
		}
	}
	
	/**
	 * 异步加载res目录下的图片文件。
	 * 图片加载完毕后，会自动为ImageView设置进去。
	 */
	public void loadImage(Context ctx, int resId, ImageView imageView, boolean bypass) {
		loadImage(ctx, RES_SCHEMA + resId, imageView, null, bypass, -1);
	}
	
	/**
	 * 当某个媒体文件加载完毕后，调用此方法。
	 */
	public synchronized void notifyFetched(String desc, boolean success, Object media) {
		MediaFetchingJob job;
		job = workers.remove(desc);
		if (success && !job.isBypass()) { // 如果需要在内存中缓存这个媒体对象。
			memoryCache.add(desc, media);
		} else {
			memoryCache.reserveMemory(MediaSizeEstimator.estimateMediaSize(media));
		}
		if (job != null && job.getGoals() != null) {
			for (AbstractMediaFetchingGoal goal : job.getGoals()) {
				if (goal != null) {
					goal.onFetched(success, media);
				}
			}
		}
	}
	
	/**
	 * 当某个媒体文件加载进度改变时，调用此方法。
	 */
	public synchronized void notifyFetchingProgress(String desc, int curSize, int totalSize) {
		MediaFetchingJob job = workers.get(desc);
		if (job != null) {
			for (AbstractMediaFetchingGoal goal : job.getGoals()) {
				if (goal != null) {
					goal.onFetchingProgress(curSize, totalSize);
				}
			}
		}
	}
	
	/**
	 * 若指定的job已经被cancel了，则返回true。
	 * 级检查指定的job中是否存在active的goal，若是，则意味着job未被cancel过。
	 */
	public synchronized boolean checkToCancel(String desc){
		MediaFetchingJob job = workers.get(desc);
		if(job != null){
			for(AbstractMediaFetchingGoal goal : job.getGoals()) {
				if(goal == null || goal.isActive()) {
					return false;
				}
			}
		}
		// 若任务在开始之前，已经被cancel了，则将job从workds中删除。
		removeJob(job);
		return true;
	}

	private synchronized void removeJob(MediaFetchingJob job) {
		workers.remove(job.getDesc());
		job.getGoals().clear();
	}
	
	public void evictAll() {
		memoryCache.evictAll();
	}
	
	/**
	 * 检测本地是否已经存在了指定的文件。
	 */
	public boolean contains(String desc) {
		boolean exist = false;
		Object media = memoryCache.get(desc);
		if(media == null) {
			DiskLruCache.Snapshot snapshot = HttpMediaFetcher.checkLocalCache(desc);
			if (snapshot != null) { // 若本地有缓存。
				exist = true;
			}
		}
		return exist;
	}
	
	/**
	 * 从L1Cache中清除多余的数据，使其保持在指定大小。
	 */
	public void reserveMemory(int size) {
		memoryCache.reserveMemory(size);
	}
	
	/**
	 * 当图片加载的状态，会调用其内的方法。
	 */
	public static abstract class ImageLoaderCallback {
		/**
		 * 当图片加载的进度改变时，回调此方法。
		 */
		public void onProgressChange(int curSize, int maxSize) { };
		
		/**
		 * 当图片加载完毕后，回调此方法。
		 */
		public void onImageLoadFinish(boolean success) { };
	}

}
