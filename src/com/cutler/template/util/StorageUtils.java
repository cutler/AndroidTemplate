
package com.cutler.template.util;

import android.os.Environment;
import android.os.StatFs;

public class StorageUtils {

    private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/";
    public static final String FILE_ROOT = SDCARD_ROOT + "testDM/";

    private static final long LOW_STORAGE_THRESHOLD = 1024 * 1024 * 10;

    public static boolean isSdCardWrittenable() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

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
     * @return
     */
    public static boolean checkAvailableStorage() {
        if (getAvailableStorage() < LOW_STORAGE_THRESHOLD) {
            return false;
        }
        return true;
    }

}
