package com.cutler.template.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class CommonUtil {

	/** 进度条对话框 */
	private static ProgressDialog mProDialog;

	/**
	 * 显示进度条对话框。
	 * 
	 * @param message
	 */
	public static void showProgressDialog(Activity context, String message) {
		showProgressDialog(context, message, false);
	}
	
	public static void showProgressDialog(Activity context, String message, boolean isCancelable) {
		if (mProDialog == null && context != null && !context.isFinishing()) {
			mProDialog = new ProgressDialog(context);
			mProDialog.setMessage(message);
			mProDialog.setCancelable(isCancelable);
			mProDialog.show();
		}
	}
	
	/**
	 * 关闭进度条对话框。
	 * 
	 * @param message
	 */
	public static void closeProgressDialog() {
		if (mProDialog != null && mProDialog.getContext() != null) {
			mProDialog.dismiss();
		}
		mProDialog = null;
	}
	
	private static Timer timer = new Timer();

	/**
	 * 在指定的时间上安排任务。
	 * 
	 * @param time
	 * @param runnable
	 */
	public static void scheduleAtTime(long time, TimerTask task) {
		timer.schedule(task, new Date(time));
	}
	
	/** * 根据手机的分辨率从 dp 的单位 转成为 px(像素) */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/** * 根据手机的分辨率从 px(像素) 的单位 转成为 dp */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
}
