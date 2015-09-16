package com.cutler.template.util.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.List;

/**
 * 获取Activity、Process信息工具类
 *
 * @author cutler
 */
public class AppUtil {
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
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 关闭指定的进程。
     */
    public static void killProcesse(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = am.getRunningAppProcesses();
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
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfos) {
            if (/* info.pid != android.os.Process.myPid() */info.processName.equals(packageName)) {
                android.os.Process.killProcess(info.pid);
                am.killBackgroundProcesses(info.processName);
            }
        }
    }

    /**
     * 返回屏幕当前的宽度
     *
     * @param activity
     * @return
     */
    public static int getScreenWidth(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * 返回屏幕当前的高度
     *
     * @param activity
     * @return
     */
    public static int getScreenHeight(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    /**
     * 开启全屏
     *
     * @param activity
     */
    public static void openFullScreen(Activity activity) {
        // 隐藏状态栏
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(params);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 关闭全屏
     *
     * @param activity
     */
    public static void closeFullScreen(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(attrs);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}
