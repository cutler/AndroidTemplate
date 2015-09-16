package com.cutler.template.util.io;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 偏好文件操作类
 *
 * @author cutler
 */
public class SharedPreferencesUtil {

	// 配置文件的名称。
	private static final String KEY_SHARE_PRE_FILE_NAME = "share_prefile";

	private static SharedPreferences sharedPreferences;

	/*
	 * 获取配置文件。
	 */
	private static SharedPreferences getSharedPreferences(Context context) {
		if(sharedPreferences == null){
			synchronized (SharedPreferencesUtil.class){
				if (sharedPreferences == null){
					sharedPreferences = context.getSharedPreferences(KEY_SHARE_PRE_FILE_NAME,
							Context.MODE_PRIVATE);
				}
			}
		}
		return sharedPreferences;
	}
	
	/**
	 * 保存String型的参数。
	 */
	public static synchronized void putParams(Context context, String key, String paramValue) {
		SharedPreferences spf = getSharedPreferences(context);
		spf.edit().putString(key, paramValue).commit();
	}

	/**
	 * 保存long型的参数。
	 */
	public static synchronized void putParams(Context context, String key, long paramValue) {
		SharedPreferences spf = getSharedPreferences(context);
		spf.edit().putLong(key, paramValue).commit();
	}

	/**
	 * 保存boolean型的参数。
	 */
	public static synchronized void putParams(Context context, String key, boolean paramValue) {
		SharedPreferences spf = getSharedPreferences(context);
		spf.edit().putBoolean(key, paramValue).commit();
	}
	
	/**
	 * 获取long类型参数
	 */
	public static synchronized long getParams(Context context, String key, long defValue) {
		long longValue = defValue;
		SharedPreferences spf = getSharedPreferences(context);
		longValue = spf.getLong(key, defValue);
		return longValue;
	}
	
	/**
	 * 获取String型的参数。
	 */
	public static synchronized String getParams(Context context, String key, String defValue) {
		String stringValue = defValue;
		SharedPreferences spf = getSharedPreferences(context);
		stringValue = spf.getString(key, defValue);
		return stringValue;
	}

	/**
	 * 获取Boolean型的参数。
	 */
	public static synchronized boolean getParams(Context context, String key,
			boolean defValue) {
		boolean booleanValue = defValue;
		SharedPreferences spf = getSharedPreferences(context);
		booleanValue = spf.getBoolean(key, defValue);
		return booleanValue;
	}
	
	/**
	 * 删除指定的参数。
	 */
	public static synchronized void removeParam(Context context, String key) {
		SharedPreferences spf = getSharedPreferences(context);
		spf.edit().remove(key).commit();
	}

}