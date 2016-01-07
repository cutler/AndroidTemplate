package com.cutler.template.base.util.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * 网络状态工具类
 *
 * @author cutler
 */
public class NetworkUtil {

    /**
     * 判断手机是否打开了wifi开关
     */
    public static boolean isOpenWifi(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 获取当前手机连接到的Wifi网络的名称，如果没连接到任何Wifi，则返回null。
     */
    public static String getCurrentWifiName(Context context) {
        String ssid = null;
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            ssid = wifiInfo.getSSID();
            if (ssid.trim().length() == 0) {
                ssid = null;
            }
        }
        return ssid;
    }

    /**
     * 判断当前网络是否是wifi
     */
    public static boolean isWifi(Context context) {
        boolean isWifi = false;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                isWifi = true;
            }
        }
        return isWifi;
    }


    /**
     * 判断当前设备是否可以连接网络。
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = manager.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED || info[i].getState() == NetworkInfo.State.CONNECTING) {
                    return true;
                }
            }
        }
        return false;
    }
}
