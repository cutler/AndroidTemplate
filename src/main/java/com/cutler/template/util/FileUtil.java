package com.cutler.template.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 文件操作的工具类。
 * @author cutler
 */
public class FileUtil {

	/**
	 * 将一个File对象递归copy的另一个位置中。
	 */
	public static void deepCopyFileTo(File srcFile, File destFile) {
		if (srcFile == null || !srcFile.exists()) {
			return ;
		}
		// 如果当前是文件夹，则递归遍历。
		if (srcFile.isDirectory()) {
			for (File subFile : srcFile.listFiles()) {
				deepCopyFileTo(subFile, new File(destFile, subFile.getName()));
			}
		} else {
			destFile.getParentFile().mkdirs();
			try {
				IOUtil.inputStream2OutputStream(new FileInputStream(srcFile), new FileOutputStream(destFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 递归删除一个File对象。
	 */
	public static void deepDeleteFile(File srcFile) {
		if (srcFile == null || !srcFile.exists()) {
			return ;
		}
		if (srcFile.isFile()) {
			srcFile.delete();
		} else if (srcFile.isDirectory()) {
			for (File subFile : srcFile.listFiles()) {
				deepDeleteFile(subFile);
			}
			srcFile.delete();
		}
	}
}
