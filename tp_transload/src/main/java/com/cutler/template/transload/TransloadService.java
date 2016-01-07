package com.cutler.template.transload;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 为了提高进程优先级，所以创建一个空服务。
 *
 * @author cutler
 */
public class TransloadService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
