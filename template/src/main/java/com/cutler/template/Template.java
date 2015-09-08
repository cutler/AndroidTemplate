package com.cutler.template;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.cutler.template.common.location.LocationService;
import com.cutler.template.util.base.DeviceUtil;

/**
 * Created by cuihu on 15/9/6.
 */
public class Template {

    private static Application mApplication;

    private static Handler mainHandler;

    public static void init(Application application) {
        if (!DeviceUtil.getCurProcessName(application).endsWith(":remote")) {
            mApplication = application;
            mainHandler = new Handler(application.getMainLooper());
            // 启动定位服务。
            LocationService.start();
        }
    }

    public static Application getApplication() {
        return mApplication;
    }

    public static Handler getMainHandler() {
        return mainHandler;
    }
}
