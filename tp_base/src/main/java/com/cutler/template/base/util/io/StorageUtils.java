
package com.cutler.template.base.util.io;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * 管理SD卡等外部存储设备的工具类。
 *
 * @author cutler
 */
public class StorageUtils {

    /**
     * 获取外部存储设备的剩余可用字节。
     */
    public static long getAvailableStorage() {
        try {
            String storageDirectory = Environment.getExternalStorageDirectory().toString();
            StatFs stat = new StatFs(storageDirectory);
            return ((long) stat.getAvailableBlocks() * (long) stat.getBlockSize());
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    /**
     * 检测出sd卡是否有足够的剩余内存可用。
     */
    public static boolean checkAvailableStorage() {
        return getAvailableStorage() >= 1024 * 1024 * 10;   //10MB
    }

    /**
     * 检测外部存储设备是否已经装载成功并可写。
     */
    public static boolean isSdCardWriteAble() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取本地缓存目录。
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath = null;
        // 若SD卡已就绪，或者SD卡不可移除。
        if (isSdCardWriteAble()) {
            // 缓存路径为：/Android/data/packageName/cache
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            // 缓存路径为：/data/data/packageName/cache
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath, uniqueName);
    }


}
