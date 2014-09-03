package com.cutler.template.common.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import com.cutler.template.common.SystemParams;
import com.cutler.template.util.CommonUtil;

import android.app.Activity;

public abstract class EntityManager<T> {
	// 保存所有注册到本Manager中的观察者。
	private List<Observer<T>> observers;
	private List<Observer<T>> removeObservers;
	private List<ModelCallback> reloadCallbacks;
	// 保存所有注册到本Manager中的观察者。
	private T data;
	// 标志Manager管理的数据是否已经过期。
	private boolean invalidate;
	// 标志Manager是否正在加载(大多情况下是异步操作)其管理的数据。
	private boolean isLoading;
	// 数据的过期时间，若为0则不会过期。
	private long expireTime;
	// 是否cancel本次请求。
	private boolean isCancelReload;
	// 标志当前EntityManager是否启用自动更新。
	private boolean autoUpdateEnable;
	/**
	 * 添加一个观察者。
	 * 注意：此方法只能在ui线程中被调用。
	 * @param observer
	 */
	public void addObserver(final Observer<T> observer,Activity act,String msg) {
		if (observers == null) {
			observers = new ArrayList<Observer<T>>();
		}
		if (observer != null) {
			// 有新观察者产生时，则调用load方法加载数据，如果已经加载完毕了，则直接将该数据传递给新观察者，否则执行加载操作。
			load(new ModelCallback() {
				public void callback(boolean success, Object... args) {
						if(removeObservers == null || !removeObservers.remove(observer)){
							observers.add(observer);
							if(data != null){
								observer.onDataLoaded(data);
							}
						}
				}
			},act,msg);
		}
	}
	
	/**
	 * 注意：此方法只能在ui线程中被调用。
	 * @param observer
	 */
	public void addObserver(final Observer<T> observer) {
		addObserver(observer, null,null);
	}
	
	/**
	 * 加载数据。
	 * 注意：此方法只能在ui线程中被调用。
	 * @param callback
	 */
	protected void load(final ModelCallback callback, final Activity act, final String msg){
		if(data == null){				// 若内存中没有数据，则尝试去本地加载。
			DataWithExpireTime<T> val = loadFromLocalCache();
			if(val != null){			// 加载成功。
				data = val.data;
				expireTime = val.expireTime;
			}
		}
		if(data != null){	
			// 若数据有效。若expireTime == 0 则视为数据不会失效。
			if(expireTime > SystemParams.getServerCurrentTime() || expireTime == 0){
				notifyDataLoaded(data, expireTime);
				if (callback != null) {
					callback.callback(true,data);
				}
				return;	// 返回。
			}
		}
		// 若用户传递了提示文本，则在加载数据的时候，将弹出一个可关闭的进度条对话框，并将提示文本显示出来。
		if(act != null && msg != null){
			CommonUtil.showProgressDialog(act, msg, true);
		}
		// 若本地和内存中都没有数据，或者有数据，但是已经失效。 则调用reload()方法，尝试从网络上获取。
		reload(new ModelCallback() {
			public void callback(boolean success, Object... args) {
				CommonUtil.closeProgressDialog();
				// 若从网络获取失败,则将旧的数据传递给Observer显示。 否则将传递新数据。
				if (callback != null) {
					callback.callback(success,data);
				}
			}
		});
	}
	
	/**
	 * 由子类重写。
	 * 注意：此方法只能在ui线程中被调用。
	 * @return
	 */
	protected DataWithExpireTime<T> loadFromLocalCache() {
		return null;
	}
	
	/**
	 * 开启定时更新任务。
	 * 注意：此方法只能在ui线程中被调用。
	 */
	private final void autoUpdate() {
		// 若数据可以过期，则不论数据是否过期，都安排一个任务。
		if (expireTime > 0) {
			// 将服务器端的时间转换为本地时间，因为Handler是以本地时间为准的。
			long taskExecTime = expireTime + SystemParams.getTimeOffset();
			// 若数据已经过期了，则等待10秒后重新加载数据。
			if (SystemParams.getServerCurrentTime() > expireTime) {
				taskExecTime = System.currentTimeMillis() + 10 * 1000;
			}
			CommonUtil.scheduleAtTime(taskExecTime, new TimerTask() {
				public void run() {
					update();
				}
			});
		}
	}
	
	/**
	 * 确保当前EntityManager有数据并且数据未过期，否则重新载入数据。
	 */
	public void update() {
		// 若数据过期了。
		if(SystemParams.getServerCurrentTime() > expireTime){
			load(null, null, null);
		}
	}
	
	/**
	 * 执行网络加载,如果当前已经正在加载了，则不会再次发起加载，而是保存参数callback对象，当加载完毕后，会通知所有callback。
	 * 注意：此方法只能在ui线程中被调用。
	 * @param callback
	 */
	final public void reload(final ModelCallback callback){
		if(reloadCallbacks == null){
			reloadCallbacks = new ArrayList<ModelCallback>();
		}
		if(callback != null){
			reloadCallbacks.add(callback);
		}
		if (!isLoading) {
			isLoading = true;
			// 具体的加载操作则由子类来完成，加载完毕后，在本方法中执行收尾工作。
		    fetchData(new ModelCallback() {
		    	public void callback(final boolean success,final Object... args) {
					invalidate = false;
	    			isLoading = false;
	    			T data =  null;
	    			long expire = 0;
	    			if(args != null){
	    				data = args.length > 0 ? (T) args[0] : null;
	    				expire = (args.length > 1 ? ((Number)args[1]).longValue() : 0);
	    			}
	    			if (success) {	// 通知已经加入到observers中的观察者，数据改变。
	    				notifyDataLoaded(data, expire);
	    			}
	    			// 通知未被加入到observers中的观察者，数据改变。 并将它们加入到observers中。
	    			for (ModelCallback callback : reloadCallbacks){
	    				callback.callback(success);
	    			}
	    			reloadCallbacks.clear();
		    	}
		     });
		}
	}
	
	/**
	 * 重新加载数据。
	 * 注意：此方法只能在ui线程中被调用。
	 * @return 加载后的数据。
	 */
	protected abstract void fetchData(ModelCallback callback);
	
	/**
	 * 启用自动更新机制。
	 */
	protected void enableAutoUpdate() {
		autoUpdateEnable = true;
	}
	
	/**
	 * 当子类从网络上加载完毕数据后，调用此方法，由EntityManager类来通知其所有的观察者，数据已经改变了。
	 * 注意：此方法只能在ui线程中被调用。
	 * @param data
	 * @param expireTime
	 */
	protected final void notifyDataLoaded(T data, long expireTime){
		this.data = data;
		this.expireTime = expireTime;
		if(autoUpdateEnable){
			autoUpdate();
		}
		notifyObservers();
	}
	
	/**
	 * 注意：此方法只能在ui线程中被调用。
	 * @param data
	 */
	protected final void notifyDataLoaded(T data){
		this.data = data;
		if(autoUpdateEnable){
			autoUpdate();
		}
		notifyObservers();
	}
	
	/**
	 * 通知Manager中已注册的所有观察者，数据已经更新了。
	 * 注意：此方法只能在ui线程中被调用。
	 */
	protected void notifyObservers() {
		if (observers != null) {
			for (Observer<T> observer : observers) {
				observer.onDataLoaded(data);
			}
		}
	}

	/**
	 * @return 返回当前Manager当前管理的所有数据。
	 */
	public T getData() {
		return (data == null || invalidate) ? null : data;
	}

	/**
	 * 将Manager中的数据置为无效，若当前存在观察者，则重新加载数据。
	 * 注意：此方法只能在ui线程中被调用。
	 */
	public void invalidate() {
		invalidate = true;
		if (observers != null && observers.size() > 0) {
			reload(null);
		}
	}

	/**
	 * 删除一个观察者。
	 * 注意：此方法只能在ui线程中被调用。
	 * @param observer
	 */
	public void removeObserver(Observer<T> observer) {
		if (observers != null) {
			boolean isFinish = observers.remove(observer);
			if(!isFinish){
				if(removeObservers == null){
					removeObservers = new ArrayList<Observer<T>>();
				}
				removeObservers.add(observer);
			}
		}
	}
	
	/**
	 * 返回当前EntityManager的观察者个数。
	 * @return
	 */
	public int getObserverCount(){
		int retVal = 0;
		if (observers != null) {
			retVal = observers.size();
		}
		return retVal;
	}

	public void setData(T data) {
		this.data = data;
	}

	public synchronized long getExpireTime() {
		return expireTime;
	}

	protected void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public boolean isCancel() {
		return isCancelReload;
	}

	public void setCancel(boolean isCancel) {
		this.isCancelReload = isCancel;
	}

	/**
	 * 本地加载数据后，返回值的类型。
	 * @param <T>
	 */
	public static class DataWithExpireTime<T>{
		private T data;
		private long expireTime;
		
		public DataWithExpireTime(T data, long expireTime) {
			this.data = data;
			this.expireTime = expireTime;
		}
		public T getData() {
			return data;
		}
		public void setData(T data) {
			this.data = data;
		}
		public long getExpireTime() {
			return expireTime;
		}
		public void setExpireTime(long expireTime) {
			this.expireTime = expireTime;
		}
	}
	
}
