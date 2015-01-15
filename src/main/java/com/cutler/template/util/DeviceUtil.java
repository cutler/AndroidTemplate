package com.cutler.template.util;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.telephony.TelephonyManager;


/**
 * 获取设备的软件、硬件信息工具类
 * @author cutler
 */
public class DeviceUtil {

	/**
     * 判断某个应用程序是 用户安装的应用程序
     */
    public static boolean isUserInstallApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
        	// 用户升级了原来的系统的程序。
            return false;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
        	// 用户自己安装的app。
            return true;
        }
        return false;
    }
    
    /**
	 * 依据包名获取应用程序的基本信息。
	 */
	public static PackageInfo getApplicationInfo(Context context, String packageName){
		PackageManager mgr = context.getPackageManager();
		try {
			return mgr.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
    
	/**
	 * 返回设备上用户安装的所有App的信息(包名、名称、图标)。
	 */
	public static List<PackageInfo> getInstallAppInfo(Context context) {
		PackageManager mgr = context.getPackageManager();
		List<PackageInfo> list = new ArrayList<PackageInfo>();
		// 0表示只获取基本信息(包名、版本号、applicationInfo等)。
		for (PackageInfo packageInfo : mgr.getInstalledPackages(0)) {
			if(isUserInstallApp(packageInfo.applicationInfo)){
				list.add(packageInfo);
			}
		}
		return list;
	}
	
	/**
	 * 返回手机的唯一标识码。
	 */
	public static String getDeviceUniqueCode(Context ctx){
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);    
		String uniqueCode = tm.getDeviceId();
		// 如果获取不到deviceId，则依据手机的其他硬件信息生成一个唯一的设备Id。
		if(uniqueCode == null || uniqueCode.length() <= 10){
			uniqueCode = "35" + Build.BOARD.length()%10 + Build.BRAND.length()%10 + 
					Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 + 
					Build.DISPLAY.length()%10 + Build.HOST.length()%10 + Build.ID.length()%10 + 
					Build.MANUFACTURER.length()%10 + Build.MODEL.length()%10 + Build.PRODUCT.length()%10 + 
					Build.TAGS.length()%10 + Build.TYPE.length()%10 + Build.USER.length()%10 ; //13 digits  
		}
		return uniqueCode;
	}
	
    /**
     * 指定包名，启动该App的入口Activity。
     */
	public static void startAppByPackageName(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();
		Intent intent = packageManager.getLaunchIntentForPackage(packageName);
		context.startActivity(intent);
	}
	
	/**
	 * 获取当前进程的名字。
	 */
	public static String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (android.app.ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}
	
	/**
	 * 关闭指定的进程。
	 */
	public static void killProcesse(Context context, String packageName){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcessInfos = am.getRunningAppProcesses();
		if (runningAppProcessInfos == null
				|| runningAppProcessInfos.size() == 0)
			return;
		/*
		 * for (RunningTaskInfo info : runningTaskInfos) { if
		 * (!info.baseActivity.getPackageName().equals(getPackageName())) {
		 * Log.d(TAG, "Will kill " + info.baseActivity.getPackageName());
		 * am.killBackgroundProcesses(info.baseActivity.getPackageName()); }
		 * }
		 */
		for (RunningAppProcessInfo info : runningAppProcessInfos) {
			if (/* info.pid != android.os.Process.myPid() */info.processName.equals(packageName)) {
				android.os.Process.killProcess(info.pid);
				am.killBackgroundProcesses(info.processName);
			}
		}
	}
	
	public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
}
