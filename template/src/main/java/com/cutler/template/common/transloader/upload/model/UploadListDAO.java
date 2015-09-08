package com.cutler.template.common.transloader.upload.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cutler.template.common.dao.InnerDataBaseHelper;
import com.cutler.template.util.io.IOUtil;

import java.util.ArrayList;
import java.util.List;

public class UploadListDAO {
    /**
     * 数据库操作类
     */
    private InnerDataBaseHelper dbHelper;

    /**
     * 本类的单例对象
     */
    private static UploadListDAO instance;

    /**
     * 表名
     */
    public static final String KEY_TAB_NAME = "upload_list";

    /**
     * 主键
     */
    public static final String KEY_ID = "_id";

    /**
     * 文件的本地保存路径
     */
    public static final String KEY_LOCAL_PATH = "localPath";

    /**
     * 文件要上传到的地址
     */
    public static final String KEY_URL = "url";

    /**
     * 文件当前的上传状态
     */
    public static final String KEY_STATE = "state";

    /**
     * 文件当前的上传方式
     */
    public static final String KEY_UPLOADTYPE = "uploadType";

    /**
     * 上传文件时同时传递给服务端的参数
     */
    public static final String KEY_PARAMS = "params";

    /**
     * 上传完成的时间
     */
    public static final String KEY_FINISHED_TIME = "finishedTime";

    /**
     * 建表语句
     */
    public static final String CREATE_TAB = "CREATE TABLE IF NOT EXISTS " + KEY_TAB_NAME +
            "("
            + KEY_ID + " integer PRIMARY KEY AUTOINCREMENT, "
            + KEY_LOCAL_PATH + " , " + KEY_URL + " , " + KEY_UPLOADTYPE + " , "
            + KEY_STATE + " , " + KEY_FINISHED_TIME + " , " + KEY_PARAMS +
            " )";

    /**
     * 删除表语句
     */
    public static final String DELETE_TAB = "DROP TABLE IF EXISTS " + KEY_TAB_NAME;

    private String[] allColumns = new String[]{KEY_ID, KEY_LOCAL_PATH, KEY_STATE, KEY_FINISHED_TIME, KEY_URL, KEY_UPLOADTYPE, KEY_PARAMS};

    /**
     * 私有化构造方法
     */
    private UploadListDAO() {
        dbHelper = InnerDataBaseHelper.getInstances();
    }

    /**
     * 返回单例对象。
     *
     * @return
     */
    public static UploadListDAO getInstance() {
        if (instance == null) {
            synchronized (UploadListDAO.class) {
                if (instance == null) {
                    instance = new UploadListDAO();
                }
            }
        }
        return instance;
    }

    /**
     * 查询出所有的下载上传记录
     *
     * @return
     */
    public List<UploadFile> findAllDownloadFile() {
        return findUploadFile(null, null);
    }

    /**
     * 将上传文件保存到数据库中。
     *
     * @param uploadFile
     * @return
     */
    public void doCreateOrUpdate(UploadFile uploadFile) {
        SQLiteDatabase dbc = dbHelper.getWritableDatabase();
        // 若文件存在，则更新数据。
        if (findByLocalPath(uploadFile.getLocalPath()) != null) {
            dbc.update(KEY_TAB_NAME, uploadFile.toContentValues(), KEY_LOCAL_PATH + " = ? ", new String[]{uploadFile.getLocalPath()});
        } else {
            // 新建一条记录。
            dbc.insert(KEY_TAB_NAME, null, uploadFile.toContentValues());
        }
    }

    private UploadFile findByLocalPath(String url) {
        List<UploadFile> fileList = findUploadFile(KEY_LOCAL_PATH + " = ? ", new String[]{url});
        UploadFile inst = null;
        if (fileList.size() > 0) {
            inst = fileList.get(0);
        }
        return inst;
    }

    /*
     * 查询出一组UploadFile对象。
     */
    private List<UploadFile> findUploadFile(String where, String[] args) {
        List<UploadFile> fileList = new ArrayList<UploadFile>();
        SQLiteDatabase dbc = dbHelper.getWritableDatabase();
        Cursor c = null;
        try {
            c = dbc.query(KEY_TAB_NAME, allColumns, where, args, null, null, null);
            while (c.moveToNext()) {
                UploadFile inst = UploadFile.parseCursor(c);
                if (inst != null) {
                    fileList.add(inst);
                }
            }
        } finally {
            IOUtil.closeCursor(c);
        }
        return fileList;
    }

}
