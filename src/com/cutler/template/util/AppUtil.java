package com.cutler.template.util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


/**
 * 访问系统API的工具类
 * @author cutler
 *
 */
public class AppUtil {

	/**
	 * 返回设备上已安装的所有App的信息(包名、名称、图标)。
	 * @param context
	 * @return 每个App的信息都封装在一个Map<String,Object>中。
	 * 		appName:App名称。
	 * 		pckName:App的包名。
	 * 		appDrawable:App的图标所对应的Drawable对象。
	 */
	public static List<Map<String,Object>> getInstallAppInfo(Context context) {
		PackageManager mgr = context.getPackageManager();
		// 0表示只获取基本信息(包名、版本号、applicationInfo等)。
		List<PackageInfo> packs = mgr.getInstalledPackages(0);
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (PackageInfo packageInfo : packs) {
			if(isUserInstallApp(packageInfo.applicationInfo)){
				list.add(packageInfo2Map(mgr, packageInfo));
			}
		}
		return list;
	}
	
	/**
	 * 返回设备上已安装的所有App的信息(包名、名称、图标)的JSON串。
	 * @param context
	 * @return
	 */
	public static JSONArray getInstallAppInfoJSONArray(Context context) {
		List<Map<String,Object>> list = getInstallAppInfo(context);
		JSONArray array = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			JSONObject obj = new JSONObject();
			try {
				obj.put("index", i+1);
				obj.put("packageName", map.get("packageName"));
				obj.put("appName", map.get("appName"));
				obj.put("versionName", map.get("versionName"));
				obj.put("versionCode", map.get("versionCode"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			array.put(obj);
		}
		return array;
	}
	
	/**
	 * 依据包名获取应用程序的基本信息。
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static Map<String,Object> getApplicationInfo(Context context, String packageName){
		PackageManager mgr = context.getPackageManager();
		try {
			PackageInfo packageInfo = mgr.getPackageInfo(packageName, 0);
			return packageInfo2Map(mgr, packageInfo);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * 将PackageInfo对象的基本信息放入到一个Map中去。
	 */
	private static Map<String,Object> packageInfo2Map(PackageManager mgr, PackageInfo packageInfo){
		Map<String,Object> item = new HashMap<String, Object>();
		item.put("packageName", packageInfo.packageName);
		item.put("versionName", packageInfo.versionName);
		item.put("versionCode", packageInfo.versionCode);
		item.put("appName", packageInfo.applicationInfo.loadLabel(mgr).toString());
		item.put("appDrawable", packageInfo.applicationInfo.loadIcon(mgr));
		return item;
	}
	
	/**
	 * 返回手机的唯一标识码。
	 * @param ctx
	 * @return
	 */
	public static String getDeviceUniqueCode(Context ctx){
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);    
		String uniqueCode = tm.getDeviceId();
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
     * 判断某个应用程序是 用户安装的应用程序
     * @param info
     * @return
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
     * 指定包名，启动该App的入口Activity。
     * @param packageName
     */
	public static void startAppByPackageName(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();
		Intent intent = packageManager.getLaunchIntentForPackage(packageName);
		context.startActivity(intent);
	}
	
	/**
	 * 获取当前进程的名字。
	 * @param context
	 * @return
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
	 * @param context
	 * @param packageName
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
				System.out.println("关闭进程："+packageName);//TODO
				android.os.Process.killProcess(info.pid);
				am.killBackgroundProcesses(info.processName);
			}
		}
	}

    public static void kill(Context context, String packageName) {
        List<RunningAppProcessInfo> runningProcesses;
        String packName;
        PackageManager pManager = null;
        ActivityManager manager = null;
        pManager = context.getPackageManager();
        manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        runningProcesses = manager.getRunningAppProcesses();

        for (RunningAppProcessInfo runningProcess : runningProcesses) {
            try {
                //                packName = runningProcess.processName;  
                //                ApplicationInfo applicationInfo = pManager.getPackageInfo(packName, 0).applicationInfo;  
                //                if (packageName.equals(packName)&&filterApp(applicationInfo)) {  
                //                    forceStopPackage(packName,context);  
                //                    System.out.println(packName+"JJJJJJ");  
                //                }  
                Log.d("AAAAAAAAAAAAAAAAAAA", runningProcess.processName);
                Toast.makeText(context, "=====================", Toast.LENGTH_LONG).show();
                for (String str : runningProcess.pkgList) {
                    Toast.makeText(context, "##############" + str, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 强制停止应用程序
     * 
     * @param pkgName
     */
    private static void forceStopPackage(String pkgName, Context context) throws Exception {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
        method.invoke(am, pkgName);
    }

    public static void installPkg(Service service, String apkName) {
        String fileName = Environment.getExternalStorageDirectory() + apkName;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
        service.startActivity(intent);
    }

    /**
     * 获得当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度 SCREEN_BRIGHTNESS_MODE_MANUAL=0
     * 为手动调节屏幕亮度
     */
    public static int getScreenMode(Service service) {
        int screenMode = 0;
        try {
            screenMode = Settings.System.getInt(service.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception localException) {

        }
        return screenMode;
    }

    /**
     * 获得当前屏幕亮度值 0--255
     */
    public static int getScreenBrightness(Service service) {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(service.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException) {

        }
        return screenBrightness;
    }

    public static boolean isScreenOn(Service service) {
        PowerManager pm = (PowerManager) service.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }
}
