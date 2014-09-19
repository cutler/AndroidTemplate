package com.cutler.template.common;

import android.content.Context;

import com.cutler.template.MainApplication;
import com.cutler.template.util.SharedPreferencesUtil;


/**
 * 本类保存一些程序运行时所需要的全部变量的值。
 * @author cutler
 *
 */
public class SystemParams {

	/**
	 * 更新当前用户的位置信息。
	 * @param longitude	精度
	 * @param latitude	纬度
	 */
	public static void setLocation(double longitude, double latitude) {
		SharedPreferencesUtil.putParams(MainApplication.getInstance(), Config.KEY_LONGITUDE, String.valueOf(longitude));
        SharedPreferencesUtil.putParams(MainApplication.getInstance(), Config.KEY_LATITUDE, String.valueOf(latitude));
	}
	
    /**
     * 返回最后一次定位的精度
     * @return
     */
	public static double getLongitude(Context context) {
		return Double.parseDouble(SharedPreferencesUtil.getParams(context, Config.KEY_LONGITUDE, Config.INVALID_LOCATION));
	}
	
	/**
     * 返回最后一次定位的纬度
     * @return
     */
	public static double getLatitude(Context context) {
		return Double.parseDouble(SharedPreferencesUtil.getParams(context, Config.KEY_LATITUDE, Config.INVALID_LOCATION));
	}
	
	
	/**
	 * 返回服务端的当前时间。
	 * @return
	 */
	public static long getServerCurrentTime(){
		return System.currentTimeMillis() - SystemParams.getTimeOffset();
	}

	/**
	 * 从本地读取客户端时间与服务端时间的差值。
	 * 注：服务器的时间差应当又你事先保存在本地。
	 * @return
	 */
	public static long getTimeOffset() {
		return SharedPreferencesUtil.getParams(MainApplication.getInstance(), Config.KEY_TIME_OFFSET, 0);
	}

}