package com.cutler.template.util.base;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

import com.cutler.template.R;

import java.util.Map;

/**
 * 通知工具类
 */
public class NotificationUtil {

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
	 *
	 * 描述：显示可以自动取消的通知
	 * @param context 上下文
	 */
	public static void showAutoCancelNotify(Context context, Map<String, Object> params) {
		String tickertText = (String) params.get("tickertText");
		String title = (String) params.get("title");
		String content = (String) params.get("content");
		Integer notifyId = (Integer) params.get("notifyId");
		Intent intent = (Intent) params.get("intent");
		Integer iconResId = (Integer) params.get("iconResId");
		if (iconResId == null) {
			iconResId = R.drawable.ic_common_transparent;
		}
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// 创建通知。
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setContentTitle(title)	//设置通知栏标题
		.setSmallIcon(iconResId)
		.setContentText(content)
		.setTicker(tickertText) 		//通知首次出现在通知栏，带上升动画效果的
		.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
		.setAutoCancel(true)			//设置这个标志当用户单击面板就可以让通知将自动取消  
		.setContentIntent(pendingIntent);
		Bitmap iconBitmap = (Bitmap) params.get("iconBitmap");
		if (iconBitmap != null) {
			mBuilder.setLargeIcon(iconBitmap);
		}
		// 多行文本风格
		NotificationCompat.InboxStyle  inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(title);
        inboxStyle.setSummaryText(content);
        DisplayMetrics m = context.getResources().getDisplayMetrics();  
        int end = 0;
        int step = m.widthPixels/40;
		for (int i = 0; i < content.length(); i+=step) {
			end = i+step;
			end = end > content.length()? content.length():end;
			inboxStyle.addLine(content.substring(i, end));
		}
        mBuilder.setStyle(inboxStyle);
		// 发出通知。
		mNotificationManager.notify(notifyId, mBuilder.build());
	}
	
	/**
	 * 显示一个进度条通知。
	 * @param context
	 * @param notifyId
	 */
	@SuppressWarnings("deprecation")
	public static OnProgressListener showProgressNotify(final Context context, 
			int tickerResId, int textResId, final String fileName, final int notifyId) {
		final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		final Notification notification= new Notification(R.drawable.ic_action_download,
				context.getString(tickerResId, fileName), System.currentTimeMillis());
    	notification.contentView = new RemoteViews(context.getPackageName(), R.layout.statebar_progress);
    	// 设置进度条，最大值 为100，当前值为0。
    	notification.contentView.setProgressBar(R.id.pb, 100, 0, false);
    	notification.contentView.setTextViewText(R.id.down_tv, context.getString(textResId, fileName, 0));
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
