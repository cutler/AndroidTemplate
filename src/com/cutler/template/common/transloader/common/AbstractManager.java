package com.cutler.template.common.transloader.common;

import android.os.Handler;

import com.cutler.template.common.transloader.goal.TransloadObserver;

public abstract class AbstractManager<T extends AbstractLoader> {
	// 主线程中的Handler。
	private static Handler mHandler = new Handler();
	protected AbstractLoader mLoader;

	/**
	 * 添加一个观察者。
	 * 
	 * @param observer
	 */
	public void addObserver(TransloadObserver observer) {
		mLoader.addObserver(observer);
	}

	/**
	 * 删除一个观察者。
	 * 
	 * @param observer
	 */
	public void removeObserver(TransloadObserver observer) {
		mLoader.removeObserver(observer);
	}

	/**
	 * 业务方法，完成上传或下载操作。
	 * 
	 * @param type
	 * @param file
	 */
	public void service(final int type, final T... file) {
		// 保证子类的doService方法在主线程中被调用。
		mHandler.post(new Runnable() {
			public void run() {
				doService(type, file);
			}
		});
	}
	
	/**
	 * 在主线程中执行操作。
	 * @param type
	 * @param file
	 */
	protected abstract void doService(int type, T... file);
	
	/**
	 * 上传（或下载）模块所支持的动作。
	 */
	public class TransloadAction {
		/**
		 *  初始化上传（或下载）模块
		 */
		public static final int INITIALIZE = 0;
		
		/**
		 * 添加一个新的上传（或下载）任务
		 */
		public static final int ADD = 1;
		
		/**
		 * 暂停一个上传（或下载）任务
		 */
		public static final int PAUSE = 2;
		
		/**
		 * 暂停所有上传（或下载）任务（包括正在进行的和等待进行的）
		 */
		public static final int PAUSE_ALL = 3;
		
		/**
		 * 继续一个上传（或下载）任务
		 */
		public static final int CONTINUE = 4;
		
		/**
		 * 删除一个上传（或下载）任务
		 */
		public static final int DELETE = 6;
		
		/**
		 * 停止上传（或下载）模块
		 */
		public static final int TERMINATE = 7; 
		
	}
	
}
