package com.cutler.template.base.util.base;

import android.app.ActivityManager;
import android.content.Context;

/**
 * @author cutler
 */
public class RuntimeUtil {

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

}
