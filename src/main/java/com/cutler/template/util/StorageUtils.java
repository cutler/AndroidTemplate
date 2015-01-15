
package com.cutler.template.util;

import java.io.File;

import com.cutler.template.MainApplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

/**
 * 管理SD卡等外部存储设备的工具类。
 * @author cutler
 */
public class StorageUtils {
    private static final long LOW_STORAGE_THRESHOLD = 1024 * 1024 * 10;

    /**
     * 检测外部存储设备是否已经装载成功并可写。
     */
    public static boolean isSdCardWrittenable() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }
    
    /**
     * 返回sdcard的更目录。
     */
	public static File getSdCardRootDir() {
		File rootFile = null;
		if (isSdCardWrittenable()) {
			rootFile = Environment.getExternalStorageDirectory();
		}
		return rootFile;
	}

    /**
     * 获取外部存储设备的剩余可用字节。
     */
    public static long getAvailableStorage() {
        String storageDirectory = null;
        storageDirectory = Environment.getExternalStorageDirectory().toString();
        try {
            StatFs stat = new StatFs(storageDirectory);
            long avaliableSize = ((long) stat.getAvailableBlocks() * (long) stat.getBlockSize());
            return avaliableSize;
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    /**
     * 检测出sd卡是否有足够的剩余内存可用。
     */
    public static boolean checkAvailableStorage() {
        if (getAvailableStorage() < LOW_STORAGE_THRESHOLD) {
            return false;
        }
        return true;
    }
    
    /**
	 * Get a usable cache directory (external if available, internal otherwise).
	 * 
	 * @param context
	 *            The context to use
	 * @param uniqueName
	 *            A unique directory name to append to the cache dir
	 * @return The cache dir
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
        File cacheFile = null;
        try{
            String cachePath = null;
            if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !isExternalStorageRemovable()){
            	cachePath = getExternalCacheDir(context).getPath();
            } else {
            	cachePath = context.getFilesDir().getPath();
            }
            cacheFile = new File(cachePath + File.separator + uniqueName);
        } catch(Exception e) {
              e.printStackTrace();
              if(context == null){ 
                  context = MainApplication.getInstance();
              }
              cacheFile = new File(context.getFilesDir().getPath() + File.separator + uniqueName);
        }
        return cacheFile ;
	}

	/**
	 * Check if external storage is built-in or removable.
	 * 
	 * @return True if external storage is removable (like an SD card), false
	 *         otherwise.
	 */
	@TargetApi(9)
	public static boolean isExternalStorageRemovable() {
		if (DeviceUtil.hasGingerbread()) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	/**
	 * Get the external app cache directory.
	 * 
	 * @param context
	 *            The context to use
	 * @return The external cache dir
	 */
	@TargetApi(8)
	public static File getExternalCacheDir(Context context) {
		if (DeviceUtil.hasFroyo()) {
			return context.getExternalCacheDir();
		}

		// Before Froyo we need to construct the external cache dir ourselves
		final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
	}

}
