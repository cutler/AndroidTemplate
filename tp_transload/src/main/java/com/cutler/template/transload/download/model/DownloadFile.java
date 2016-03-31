package com.cutler.template.transload.download.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.cutler.template.transload.common.TransferableFile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 描述一个下载文件
 *
 * @author cutler
 */
public class DownloadFile extends TransferableFile {
    public static final int RETRY_TIME_GAP = 5000;    // 重试下载的时间间隔
    private String url;        // 下载地址
    private String fileName;    // 用于在界面上显示。
    private int curRetryTimes;    // 当前已经重试的次数。
    private FileState state = FileState.WAITING;    // 文件的传输状态。
    private boolean useCache;            // 是否使用本地已经存在的文件。

    public String getFileName() {
        if (fileName == null) {
            try {
                fileName = new File(new URL(url).getFile()).getName();
            } catch (MalformedURLException e) {
            }
        }
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return url;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    @Override
    public String getUniqueNumber() {
        return url;
    }

    /**
     * 判断当前DownloadFile是否需要重试下载
     *
     * @return
     */
    public boolean canRetryDownload() {
        return curRetryTimes <= Integer.MAX_VALUE;
    }

    /**
     * 更新当前已经重试的次数
     */
    public void iterateRetryTimes() {
        curRetryTimes++;
    }

    public FileState getState() {
        return state;
    }

    public void setState(FileState state) {
        this.state = state;
    }

    public static DownloadFile parseCursor(Cursor c) {
        DownloadFile inst = null;
        try {
            inst = new DownloadFile();
            inst.url = c.getString(c.getColumnIndex(DownloadListDAO.KEY_URL));
            inst.fileName = c.getString(c.getColumnIndex(DownloadListDAO.KEY_FILENAME));
            inst.state = FileState.valueOf(c.getString(c.getColumnIndex(DownloadListDAO.KEY_STATE)));
            inst.useCache = Boolean.parseBoolean(c.getString(c.getColumnIndex(DownloadListDAO.KEY_USE_CACHE)));
        } catch (Exception e) {
            inst = null;
            e.printStackTrace();
        }
        return inst;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(DownloadListDAO.KEY_URL, url);
        values.put(DownloadListDAO.KEY_FILENAME, fileName);
        values.put(DownloadListDAO.KEY_STATE, state.toString());
        values.put(DownloadListDAO.KEY_USE_CACHE, String.valueOf(useCache));
        return values;
    }

}
