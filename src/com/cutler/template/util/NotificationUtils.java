package com.cutler.template.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.RemoteViews;

import com.cutler.template.R;

/**
 * 描述：显示通知的工具类
 *
 * @author jianxiong.yj
 *
 */
public class NotificationUtils {

	/**
	 * 描述：关闭通知栏
	 * @param context 上下文信息
	 * @param notifyId 通知id
	 */
	public static void cancelNotify(Context context, int notifyId){
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(notifyId);
	}

	/**
	 * 显示一个进度条通知。
	 * @param context
	 * @param notifyId
	 */
	@SuppressWarnings("deprecation")
	public static OnProgressListener showDownloadNotify(final Context context, final String fileName, final int notifyId) {
		final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		final Notification notification= new Notification(R.drawable.ic_action_download, 
				context.getString(R.string.download_start, fileName), System.currentTimeMillis());
    	notification.contentView = new RemoteViews(context.getPackageName(), R.layout.statebar_progress); 
    	// 设置进度条，最大值 为100，当前值为0。
    	notification.contentView.setProgressBar(R.id.pb, 100, 0, false);
    	notification.contentView.setTextViewText(R.id.down_tv, context.getString(R.string.download_doing, fileName, 0)); 
    	notification.flags|=Notification.FLAG_NO_CLEAR;
    	// 发送一个不可删除、点击没任何效果的通知到状态栏中。
    	mNotificationManager.notify(notifyId, notification);
    	// 返回给外界一个回调接口，当需要更新状态栏通知的进度时，就回调改接口中的方法。
    	return new OnProgressListener(){
			public void onProgessChanged(int curProgress) {
		    	notification.contentView.setProgressBar(R.id.pb, 100, curProgress, false);
		    	notification.contentView.setTextViewText(R.id.down_tv, context.getString(R.string.download_doing, fileName, curProgress)); 
		    	mNotificationManager.notify(notifyId, notification);  
			}
    	};
	}
	
	public static interface OnProgressListener {
		/**
		 * 当下载进度发生改变时，回调此方法更新进度条。
		 * @param curProgress 新的进度
		 */
		void onProgessChanged(int curProgress);
	}
}
