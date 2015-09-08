package com.cutler.template.util.io;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 提供针对Assets目录的若干工具方法。
 *
 * @author cutler
 */
public class AssetsUtil {

    public static String ASSETS_ROOT = "";

    /**
     * 将assets目录下指定的文件递归拷贝到dest指向的目录中。
     *
     * @param srcDir 若取值为 AssetsUtil.ASSETS_ROOT，则代表递归复制assets目录下的所有文件。
     *               若取值为"a"，则代表递归复制assets\a目录下的所有文件。
     * @return 是否拷贝成功, true 成功；false 失败
     * @throws IOException
     */
    public static boolean deepCopyAssetsFileTo(Context context, String srcDir, File destDir) {
        // 若目标目录不存在则直接返回。
        if (destDir == null || !destDir.isDirectory()) {
            return false;
        }
        try {
            List<String> list = AssetsUtil.deepGetAssetsFileList(context, srcDir);
            for (String fileName : list) {
                File subFile = new File(destDir, fileName);
                subFile.getParentFile().mkdirs();
                IOUtil.inputStream2OutputStream(context.getAssets().open(fileName), new FileOutputStream(subFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取assets下指定目录下的文件列表。
     *
     * @param rootFile 若取值为 AssetsUtil.ASSETS_ROOT，则代表递归获取assets目录下的所有文件。
     *                 若取值为"a"，则代表递归获取assets\a目录下的所有文件。
     * @return 文件列表
     * @throws IOException
     */
    public static List<String> deepGetAssetsFileList(Context context, String rootFile) {
        AssetManager mgr = context.getAssets();
        List<String> fileList = new ArrayList<String>();
        deepGetAssetsFileList(mgr, rootFile, fileList);
        return fileList;
    }

    /*
     * 递归获取文件列表。
     */
    private static void deepGetAssetsFileList(AssetManager mgr, String curFile, List<String> fileList) {
        boolean isDir = isDir(mgr, curFile);
        // 如果当前是文件夹，则递归遍历。
        if (isDir) {
            String[] fileListStrArr = new String[0];
            try {
                // 对一个已存在的文件、不存在的文件、不存在的文件夹调用list方法时，都会返回一个长度为 0 的String数组。
                fileListStrArr = mgr.list(curFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (String subFile : fileListStrArr) {
                if (curFile.length() > 0) {
                    subFile = curFile + "/" + subFile;
                }
                deepGetAssetsFileList(mgr, subFile, fileList);
            }
        } else {
            fileList.add(curFile);
        }
    }

    /*
     * 判断当前路径是否指向一个文件夹。
     */
    private static boolean isDir(AssetManager mgr, String file) {
        boolean isDir = false;
        InputStream input = null;
        try {
            input = mgr.open(file);
        } catch (Exception e) {
            // 对文件夹执行open操作会抛出异常。  但打开没有后缀名的文件不会抛异常，这样就保证assets下可以保存没有后缀名的文件。
            isDir = true;
        } finally {
            IOUtil.closeInputStream(input);
        }
        return isDir;
    }
}
