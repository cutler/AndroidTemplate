package com.cutler.template.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cutler.template.MainApplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Environment;


/**
 * 文件读写、IO读取的工具类
 * 
 * @author cutler
 * 
 */
public class IOUtil {
	/**
	 * 将InputStream中的数据读出来。
	 * @param input
	 * @return
	 */
	public static byte[] inputStream2ByteArray(InputStream input) {
		if (input == null) {
			return null;
		}
		ByteArrayOutputStream output = null;
		byte[] data = null;
		try {
			data = new byte[1024 * 32];
			output = new ByteArrayOutputStream();
			int len;
			while ((len = input.read(data)) != -1) {
				output.write(data, 0, len);
			}
			data = output.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeInputStream(input);
			closeOutputStream(output);
		}
		return data;
	}
	
	/**
	 * 将InputStream中的数据以字符串的形式返回。
	 * @param input
	 * @return
	 */
	public static String inputStream2String(InputStream input, String encoding) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		String str = null;
		try {
			byte[] array = new byte[1024];
			int len = -1;
			while ((len = input.read(array)) != -1) {
				output.write(array, 0, len);
			}
			str = output.toString(encoding);
		} catch (Exception e) {
		} finally {
			closeInputStream(input);
			closeOutputStream(output);
		}
		return str;
	}
	
	/**
	 * 将InputStream中的数据写入到OutputStream中，完毕后，关闭两个流。
	 * @param input
	 * @param output
	 * @return
	 */
	public static boolean inputStream2OutputStream(InputStream input, OutputStream output) {
		boolean isFinish = true;
		try {
			byte[] array = new byte[1024];
			int len = -1;
			while ((len = input.read(array)) != -1) {
				output.write(array, 0, len);
			}
		} catch (Exception e) {
			isFinish = false;
		} finally {
			closeInputStream(input);
			closeOutputStream(output);
		}
		return isFinish;
	}
	
	/**
	 * 关闭输入流。
	 * @param input
	 */
	public static void closeInputStream(InputStream input) {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) { }
		}
	}

	/**
	 * 关闭输出流。
	 * @param input
	 */
	public static void closeOutputStream(OutputStream output) {
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) { }
		}
	}

	/**
	 * 将InputStream中的数据，写入到OutputStream中。
	 * @param output
	 */
	public static void stringToOutputStream(String str, OutputStream output) {
		if (str != null && output != null) {
			try {
				output.write(str.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeOutputStream(output);
			}
		}
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
            	cachePath = context.getCacheDir().getPath();
            }
            cacheFile = new File(cachePath + File.separator + uniqueName);
        } catch(Exception e) {
              e.printStackTrace();
              if(context == null){ 
                  context = MainApplication.getInstance();
              }
              cacheFile = new File(context.getCacheDir().getPath() + File.separator + uniqueName);
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
		if (Utils.hasGingerbread()) {
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
		if (Utils.hasFroyo()) {
			return context.getExternalCacheDir();
		}

		// Before Froyo we need to construct the external cache dir ourselves
		final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
	}
}
