package com.cutler.template.common.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cutler.template.Template;
import com.cutler.template.common.transloader.download.model.DownloadListDAO;
import com.cutler.template.common.transloader.upload.model.UploadListDAO;


/**
 * 数据库操作类
 *
 * @author cutler
 */
public class InnerDataBaseHelper extends SQLiteOpenHelper {

    /**
     * 默认的数据库名称
     */
    public static final String DB_NAME = "tmp_databases.db";
    /**
     * 数据库当前版本号
     */
    public static final int DB_VERSION = 1;

    private static InnerDataBaseHelper instances = null;

    /**
     * 私有化构造器。
     */
    private InnerDataBaseHelper() {
        super(Template.getApplication(), DB_NAME, null, DB_VERSION);
    }

    public static InnerDataBaseHelper getInstances() {
        if (instances == null) {
            synchronized (InnerDataBaseHelper.class) {
                if (instances == null) {
                    instances = new InnerDataBaseHelper();
                }
            }
        }
        return instances;
    }

    /**
     * 数据库创建后，执行建表语句。
     */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DownloadListDAO.CREATE_TAB);
        db.execSQL(UploadListDAO.CREATE_TAB);
    }

    /**
     * 数据库版本号被更新时，删除原来的表，并重新建立。
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL(DownloadListDAO.DELETE_TAB);
            db.execSQL(UploadListDAO.DELETE_TAB);
            onCreate(db);
        }
    }

    /**
     * 判断某张表中是否存在某字段(注，该方法无法判断表是否存在，因此应与isTableExist一起使用)
     *
     * @param tableName  表名
     * @param columnName 列名
     * @return
     */
    public static boolean isColumnExist(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        try {
            Cursor cursor = null;
            String sql = "select count(1) as c from sqlite_master where type ='table' and name ='"
                    + tableName.trim()
                    + "' and sql like '%"
                    + columnName.trim() + "%'";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
            cursor.close();
        } catch (Exception e) {
        }
        return result;
    }


    /**
     * 关闭数据库
     */
    public void closeDB() {
        if (instances != null) {
            instances.close();
            instances = null;
        }
    }
}
