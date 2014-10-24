package com.cutler.template.common.transloader.goal;

import java.util.Map;

/**
 * 前端代码传递给上传/下载模块的回调接口。
 * @author cutler
 *
 */
public interface TransloadObserver {

	/**
	 * 本次的动作。
	 */
	public static String KEY_ACTION = "action";
	
	/**
	 * 上传/下载模块的动作。
	 */
	public class TransloadAction {
		/**
		 *  初始化
		 */
		public static final int INIT = 0;
		
		/**
		 * 添加一个新的上传/下载任务
		 */
		public static final int ADD = 1;
		
		/**
		 * 暂停上传/下载
		 */
		public static final int PAUSE = 2;
		
		/**
		 * 暂停所有上传/下载（包括正在上传/下载的和等待上传/下载的）
		 */
		public static final int PAUSE_ALL = 3;
		
		/**
		 * 继续上传/下载
		 */
		public static final int CONTINUE = 4;
		
		/**
		 * 上传/下载完成
		 */
		public static final int COMPLETE = 5;
		
		/**
		 * 删除上传/下载
		 */
		public static final int DELETE = 6;
		
		/**
		 * 停止上传/下载器。
		 */
		public static final int STOP = 7; 
		
	}
	
	/**
	 * 上传/下载的状态。
	 */
	public class TransloadStates {
		
		/**
		 * 上传/下载进度改变
		 */
		public static final int PROGRESS = 1001;
		
		/**
		 * 上传/下载出错
		 */
		public static final int ERROR = 1002;
		
	}
	
	
	
	/**
	 * 传输速率
	 */
	public static String KEY_SPEED = "speed";

	/**
	 * 总大小
	 */
	public static String KEY_TOTAL_SIZE = "totalSize";

	/**
	 * 当前进度
	 */
	public static String KEY_PROGRESS = "progress";

	/**
	 * 上传/下载的文件
	 */
	public static String KEY_FILE = "file";

	/**
	 * 本地路径
	 */
	public static String KEY_LOCAL_PATH = "localPath";

	/**
	 * 是否是从暂停状态转为开始状态。
	 */
	public static String KEY_IS_PAUSED = "isPaused";

	/**
	 * 当下载数据改变时下载器会在主线程中回调此方法。
	 * 
	 * @param downloadList
	 *            下载列表
	 * @param curFile
	 *            当前数据改变的文件对象。
	 */
	public void onTransloadDataChanged(Map<String, Object> params);
}
