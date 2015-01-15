package com.cutler.template.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.cutler.template.MainApplication;

/**
 * 网络状态工具类
 * @author cutler
 */
public class NetworkUtils {
	/** 没有网络 */
    public static final String NETWORKTYPE_INVALID = "unknown";
    /** wap网络 */
    public static final String NETWORKTYPE_WAP = "wap";
    /** 2G网络 */
    public static final String NETWORKTYPE_2G = "2G";
    /** 3G和3G以上网络，或统称为快速网络 */
    public static final String NETWORKTYPE_3G = "3G";
    /** wifi网络 */
    public static final String NETWORKTYPE_WIFI = "wifi";
    
	/**
	 * 判断当时网络是否是wifi
	 */
	public static boolean isWifi() {
		ConnectivityManager connectivityManager = (ConnectivityManager) MainApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		}
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断当前设备是否可以连接网络。
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED || info[i].getState() == NetworkInfo.State.CONNECTING) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 获取网络状态，wifi,wap,2g,3g
	 */
	public static String getNetWorkType(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		String mNetWorkType = NETWORKTYPE_INVALID;
		if (networkInfo != null && networkInfo.isConnected()) {
			String type = networkInfo.getTypeName();
			if (type.equalsIgnoreCase("WIFI")) {
				mNetWorkType = NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
            	String proxyHost = android.net.Proxy.getDefaultHost();
            	mNetWorkType = TextUtils.isEmpty(proxyHost)? (isFastMobileNetwork(context) ? NETWORKTYPE_3G : NETWORKTYPE_2G) : NETWORKTYPE_WAP;
            }
		}
		return mNetWorkType;
	}
	
	private static boolean isFastMobileNetwork(Context context) { 
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		switch (telephonyManager.getNetworkType()) {
	        case TelephonyManager.NETWORK_TYPE_1xRTT:
	            return false; // ~ 50-100 kbps
	        case TelephonyManager.NETWORK_TYPE_CDMA:
	            return false; // ~ 14-64 kbps
	        case TelephonyManager.NETWORK_TYPE_EDGE:
	            return false; // ~ 50-100 kbps
	        case TelephonyManager.NETWORK_TYPE_EVDO_0:
	            return true; // ~ 400-1000 kbps
	        case TelephonyManager.NETWORK_TYPE_EVDO_A:
	            return true; // ~ 600-1400 kbps
	        case TelephonyManager.NETWORK_TYPE_GPRS:
	            return false; // ~ 100 kbps
	        case TelephonyManager.NETWORK_TYPE_HSDPA:
	            return true; // ~ 2-14 Mbps
	        case TelephonyManager.NETWORK_TYPE_HSPA:
	            return true; // ~ 700-1700 kbps
	        case TelephonyManager.NETWORK_TYPE_HSUPA:
	            return true; // ~ 1-23 Mbps
	        case TelephonyManager.NETWORK_TYPE_UMTS:
	            return true; // ~ 400-7000 kbps
	        case TelephonyManager.NETWORK_TYPE_EHRPD:
	            return true; // ~ 1-2 Mbps
	        case TelephonyManager.NETWORK_TYPE_EVDO_B:
	            return true; // ~ 5 Mbps
	        case TelephonyManager.NETWORK_TYPE_HSPAP:
	            return true; // ~ 10-20 Mbps
	        case TelephonyManager.NETWORK_TYPE_IDEN:
	            return false; // ~25 kbps
	        case TelephonyManager.NETWORK_TYPE_LTE:
	            return true; // ~ 10+ Mbps
	        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
	            return false;
	        default:
	            return false;
		}
	}
	
}
