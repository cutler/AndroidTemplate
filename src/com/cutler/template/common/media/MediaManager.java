package com.cutler.template.common.media;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;

public class MediaManager {
	// 单例对象。
	private static MediaManager instance;
	// 资源文件的schema
	public static final String RES_SCHEMA = "res:";
	// gif文件的schema
	public static final String GIF_SCHEMA = "gif:";
	// http文件的schema
	public static final String HTTP_SCHEMA = "http:";
	// 同步图片文件的schema
	public static final String SYNC_IMG_SCHEMA = "syncImg:";
	// 同步gif文件的schema
	public static final String SYNC_GIF_SCHEMA = "syncGif:";
	// 保存当前正在加载的所有工作。
	private Map<String,MediaFetchingJob> workers = new HashMap<String,MediaFetchingJob>();
	// 具有单个线程的线程池，不用Thread是因为这样可以减少线程创建时的消耗。
	private ExecutorService singleThreadPool;
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
	 * 指定desc,异步加载其对应的媒体文件。
	 * @param ctx
	 * @param desc
	 * @param goal
	 * @param bypass
	 */
	public void loadMedia(Context ctx,final String desc,MediaFetchingGoal goal,boolean bypass){
		Object media = memoryCache.get(desc);
		if(media == null) {
			synchronized (this) {
				media = memoryCache.get(desc);
				if (media == null) {
					MediaFetchingJob job = workers.get(desc);
					if(job != null){	// 若已经有任务在进行了，则将goal添加到该任务的回调列表里，然后直接返回。
						job.getGoals().add(goal);
					} else {
						job = new MediaFetchingJob(desc,goal,bypass);
						workers.put(desc, job);
						final MediaFetcher fetcher = MediaFetcherFactory.createFetcher(ctx,desc) ;
						if(fetcher != null){	
							// 在内存缓存中腾出一定的空间，以便加载新任务。
							reserveMemory(fetcher.getEstimatedMemorySize());
							// 开启一个Job，执行加载操作。
							singleThreadPool.execute(new Runnable() {
								public void run() {
									// 若任务在开始之前，没有被cancel。
									if(!checkToCancel(desc)){
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
		if(goal != null){	// 通知数据加载完成。
			goal.onFetched(true, media);
		}
	}
	
	/**
	 * 指定resId,异步加载其对应的媒体文件。
	 * @param ctx
	 * @param resId
	 * @param goal
	 * @param bypass
	 */
	public void loadMedia(Context ctx,int resId,MediaFetchingGoal goal,boolean bypass){
		loadMedia(ctx, RES_SCHEMA+resId, goal, bypass);
	}
	
	/**
	 * 指定desc,同步加载其对应的媒体文件。
	 * @param ctext
	 * @param desc
	 * @param bypass
	 * @return
	 */
	public Object getMedia(Context ctx,String desc,boolean bypass){
		Object media = memoryCache.get(desc);
		if(media == null){
			SyncGoal goal = new SyncGoal();
			loadMedia(ctx, desc, goal, bypass);
			media = goal.getMedia();
		}
		return media;
	}
	
	/**
	 * 指定resId,同步加载其对应的媒体文件。
	 * @param ctext
	 * @param resId
	 * @param bypass
	 * @return
	 */
	public Object getMedia(Context ctext,int resId,boolean bypass){
//		String desc = RES_SCHEMA+resId;
//		Object media = memoryCache.get(desc);
//		if(media == null) {
//			synchronized (this) {
//				media = memoryCache.get(desc);
//				if (media == null) {
//					media = ctext.getResources().getDrawable(resId);
//					if(!bypass){
//						memoryCache.add(desc, media);
//					}
//				}
//			}
//		}
		return getMedia(ctext, RES_SCHEMA+resId, bypass);
	}
	
	/**
	 * 指定resId,异步加载其对应的图片文件。
	 * @param ctx
	 * @param resId
	 * @param goal
	 * @param bypass
	 */
	public void loadImage(Context ctx,int resId,ImageView imageView,boolean bypass){
		loadImage(ctx, RES_SCHEMA+resId, imageView, bypass);
	}
	
	/**
	 * 指定desc,异步加载其对应的图片文件。 图片下载完毕后，会自动为ImageView设置进去。
	 * @param ctx
	 * @param desc
	 * @param goal
	 * @param bypass 是否不将其缓存在内存中。
	 */
	public void loadImage(Context ctx,String desc,ImageView imageView,boolean bypass){
		loadImage(ctx, desc, imageView, bypass, -1);
	}
	
	/**
	 * 指定desc,异步加载其对应的图片文件。 图片下载完毕后，会自动为ImageView设置进去。
	 * @param ctx
	 * @param desc
	 * @param goal
	 * @param bypass 是否不将其缓存在内存中。
	 */
	public void loadImage(Context ctx,String desc,ImageView imageView,boolean bypass,int defImgId){
		loadImage(ctx, desc, imageView, null, bypass, defImgId);
	}
	
	/**
	 * 指定desc,异步加载其对应的图片文件。 图片下载完毕后，会自动为ImageView设置进去。
	 * 图片下载的中途，也会不断的调用ImageLoaderCallback的方法，通知外界加载的进度改变。
	 * @param ctx
	 * @param desc
	 * @param imageView
	 * @param callback
	 * @param bypass
	 */
	public void loadImage(Context ctx,String desc,ImageView imageView,ImageLoaderCallback callback,boolean bypass){
		loadImage(ctx, desc, imageView, callback, bypass, -1);
	}
	
	public void loadImage(Context ctx,int resId,ImageView imageView,ImageLoaderCallback callback,boolean bypass){
		loadImage(ctx, RES_SCHEMA+resId, imageView, callback, bypass);
	}
	
	/**
	 * 指定desc,异步加载其对应的图片文件。 图片下载完毕后，会自动为ImageView设置进去。
	 * 图片下载的中途，也会不断的调用ImageLoaderCallback的方法，通知外界加载的进度改变。
	 * @param ctx
	 * @param desc
	 * @param imageView
	 * @param callback
	 * @param bypass
	 */
	public void loadImage(Context ctx,String desc,ImageView imageView,ImageLoaderCallback callback,boolean bypass,int defImgId){
		Object media = memoryCache.get(desc);
		if(media == null){
			loadMedia(ctx, desc, new ImageViewGoal(desc,imageView,callback,defImgId), bypass);
		} else {
            ImageViewGoal.loadImageDirectly(desc, imageView, (Drawable)media);
			if(callback != null){
				callback.onImageLoadFinish(true);
			}
		}
	}
	
	/**
	 * 从本地同步目录中，加载一个同步资源图片。
	 * @param ctx
	 * @param desc
	 * @param imageView
	 * @param bypass
	 */
	public Drawable getSyncImage(Context ctx,String desc,boolean bypass){
		return (Drawable) getMedia(ctx, SYNC_IMG_SCHEMA+desc, bypass);
	}
	
	/**
	 * 当某个媒体文件加载完毕后，调用此方法。
	 * @param desc
	 * @param success
	 * @param media
	 */
	public synchronized void notifyFetched(String desc,boolean success,Object media) {
		MediaFetchingJob job;
		job = workers.remove(desc);
		if(success && !job.isBypass()){	// 如果需要在内存中缓存这个媒体对象。
			memoryCache.add(desc, media);
		} else {
			memoryCache.reserveMemory(MediaSizeEstimator.estimateMediaSize(media));
		}
		if (job != null && job.getGoals() != null) {
			for(MediaFetchingGoal goal : job.getGoals()){
				if (goal != null) {
					goal.onFetched(success, media);
				}
			}
		}
	}
	
	/**
	 * 当某个媒体文件加载进度改变时，调用此方法。
	 * @param desc
	 * @param currSize
	 * @param totalSize
	 */
	public synchronized void notifyFetchingProgress(String desc,int curSize,int totalSize){
		MediaFetchingJob job = workers.get(desc);
		if(job != null) {
			for(MediaFetchingGoal goal : job.getGoals()) {
				if( goal != null){
					goal.onFetchingProgress(curSize, totalSize);
				}
			}
		}
	}
	
	public void prefetch(Context ctx,String desc){
		loadMedia(ctx, desc, null, true);
	}
	
	public void prefetch(Context ctx,int resId){
		loadMedia(ctx, resId, null, true);
	}
	
	/**
	 * 若指定的job已经被cancel了，则返回true。
	 * 级检查指定的job中是否存在active的goal，若是，则意味着job未被cancel过。
	 * @param desc
	 * @return
	 */
	public synchronized boolean checkToCancel(String desc){
		MediaFetchingJob job = workers.get(desc);
		if(job != null){
			for(MediaFetchingGoal goal : job.getGoals()) {
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
	 * @param desc
	 * @return
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
	 * 清除ImageView的tab、显示内容。
	 * 使用场景：参见 bug#301 
	 * 之所以要清除数据，是因为，若本地存在缓存，则加载的速度很快，无法测出此bug。
	 * @param view
	 */
	public void clearImage(ImageView view){
		if(view != null){
			view.setImageDrawable(null);
			view.setTag(null);
		}
	}
	
	/**
	 * 从L1Cache中清除多余的数据，使其保持在指定大小。
	 * @param size
	 */
	public void reserveMemory(int size) {
		memoryCache.reserveMemory(size);
	}
	
	/**
	 * 静态接口，当图片加载的状态，会调用其内的方法。
	 *
	 */
	public static abstract class ImageLoaderCallback {
		/**
		 * 当图片加载的进度改变时，回调此方法。
		 * @param curSize
		 * @param maxSize
		 */
		public void onProgressChange(int curSize,int maxSize){};
		
		/**
		 * 当图片加载完毕后，回调此方法。
		 * @param success
		 */
		public void onImageLoadFinish(boolean success){};
	}

}
