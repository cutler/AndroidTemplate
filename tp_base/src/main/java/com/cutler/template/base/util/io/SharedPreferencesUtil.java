package com.cutler.template.base.util.io;

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
        if (sharedPreferences == null) {
            synchronized (SharedPreferencesUtil.class) {
                if (sharedPreferences == null) {
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
        getSharedPreferences(context).edit().putString(key, paramValue).commit();
    }

    /**
     * 保存long型的参数。
     */
    public static synchronized void putParams(Context context, String key, long paramValue) {
        getSharedPreferences(context).edit().putLong(key, paramValue).commit();
    }

    /**
     * 保存boolean型的参数。
     */
    public static synchronized void putParams(Context context, String key, boolean paramValue) {
        getSharedPreferences(context).edit().putBoolean(key, paramValue).commit();
    }

    /**
     * 获取long类型参数
     */
    public static synchronized long getParams(Context context, String key, long defValue) {
        return getSharedPreferences(context).getLong(key, defValue);
    }

    /**
     * 获取String型的参数。
     */
    public static synchronized String getParams(Context context, String key, String defValue) {
        return getSharedPreferences(context).getString(key, defValue);
    }

    /**
     * 获取Boolean型的参数。
     */
    public static synchronized boolean getParams(Context context, String key, boolean defValue) {
        return getSharedPreferences(context).getBoolean(key, defValue);
    }

    /**
     * 删除指定的参数。
     */
    public static synchronized void removeParam(Context context, String key) {
        getSharedPreferences(context).edit().remove(key).commit();
    }

}