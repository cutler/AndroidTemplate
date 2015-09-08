package com.cutler.template.common.transloader.upload.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.cutler.template.common.transloader.common.TransferableFile;
import com.cutler.template.common.transloader.upload.UploadManager.UploadType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UploadFile extends TransferableFile {
    // 上传地址
    private String url;
    // 本地保存路径
    private String localPath;
    // 文件的上传方式
    private UploadType uploadType = UploadType.NORMARL;
    // 文件当前的上传状态
    private FileState state;
    // 文件上传完成的时间，未完成则为0
    private long finishedTime;
    // 上传文件的同时需要传递给服务端的字符串参数。
    private Map<String, String> params;

    @Override
    public String getUniqueNumber() {
        return localPath;
    }

    /**
     * 上传任务被取消时，是否同时删除本地的文件。
     */
    @Override
    public boolean isDeleteCacheFile() {
        return false;
    }

    public String getLocalPath() {
        return localPath;
    }

    public FileState getState() {
        return state;
    }

    public long getFinishedTime() {
        return finishedTime;
    }

    public String getUrl() {
        return url;
    }

    public UploadType getUploadType() {
        return uploadType;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public void setState(FileState state) {
        this.state = state;
    }

    public static UploadFile parseCursor(Cursor c) {
        UploadFile inst = null;
        try {
            inst = new UploadFile();
            inst.localPath = c.getString(c.getColumnIndex(UploadListDAO.KEY_LOCAL_PATH));
            inst.url = c.getString(c.getColumnIndex(UploadListDAO.KEY_URL));
            inst.uploadType = UploadType.valueOf(c.getString(c.getColumnIndex(UploadListDAO.KEY_UPLOADTYPE)));
            inst.state = FileState.valueOf(c.getString(c.getColumnIndex(UploadListDAO.KEY_STATE)));
            String paramsJson = c.getString(c.getColumnIndex(UploadListDAO.KEY_PARAMS));
            if (paramsJson != null && paramsJson.length() > 0) {
                JSONObject jsonObj = new JSONObject(paramsJson);
                inst.params = new HashMap<String, String>();
                Iterator iter = jsonObj.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    inst.params.put(key, jsonObj.getString(key));
                }
            }
            inst.finishedTime = c.getLong(c.getColumnIndex(UploadListDAO.KEY_FINISHED_TIME));
        } catch (Exception e) {
            inst = null;
            e.printStackTrace();
        }
        return inst;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(UploadListDAO.KEY_LOCAL_PATH, localPath);
        values.put(UploadListDAO.KEY_URL, url);
        values.put(UploadListDAO.KEY_UPLOADTYPE, uploadType.toString());
        if (params != null) {
            try {
                JSONObject jsonObj = new JSONObject();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    jsonObj.put(entry.getKey(), entry.getValue());
                }
                values.put(UploadListDAO.KEY_PARAMS, jsonObj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        values.put(UploadListDAO.KEY_STATE, state.toString());
        values.put(UploadListDAO.KEY_FINISHED_TIME, finishedTime);
        return values;
    }
}
