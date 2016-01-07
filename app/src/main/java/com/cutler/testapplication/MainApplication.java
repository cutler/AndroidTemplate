package com.cutler.testapplication;

import android.app.Application;

import com.cutler.template.base.Template;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by cuihu on 15/9/7.
 */
public class MainApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Template.init(this);

        // 为了简单起见，我们这里创建一个默认的配置对象，而不是自定义一个。
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        // 使用这个配置对象进行初始化操作。
        ImageLoader.getInstance().init(config);
    }
}
