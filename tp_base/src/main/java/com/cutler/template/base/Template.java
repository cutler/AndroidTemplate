package com.cutler.template.base;

import android.app.Application;
import android.os.Handler;

import com.cutler.template.base.util.base.RuntimeUtil;

public class Template {

    private static Application mApplication;

    private static Handler mainHandler;

    public static void init(Application application) {
        if (!RuntimeUtil.getCurProcessName(application).endsWith(":remote")) {
            mApplication = application;
            mainHandler = new Handler(application.getMainLooper());
        }
    }

    public static Application getApplication() {
        return mApplication;
    }

    public static Handler getMainHandler() {
        return mainHandler;
    }
}
