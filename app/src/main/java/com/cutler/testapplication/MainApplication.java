package com.cutler.testapplication;

import android.app.Application;

import com.cutler.template.Template;

/**
 * Created by cuihu on 15/9/7.
 */
public class MainApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Template.init(this);
    }
}
