package com.cutler.template.common.transloader.download.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cutler.template.common.dao.InnerDataBaseHelper;
import com.cutler.template.util.io.IOUtil;

import java.util.ArrayList;
import java.util.List;

public class DownloadListDAO {
	/** 数据库操作类 */
	private InnerDataBaseHelper dbHelper;
	
	/** 本类的单例对象 */
	private static DownloadListDAO instance;
	
	/** 表名 */
	public static final String KEY_TAB_NAME = "upload_list";
	
    /** 主键 */
	public static final String KEY_ID = "_id";
    
	/** 下载地址 */
	public static final String KEY_URL = "url";

	/** 文件名称，用于在界面上显示 */
	public static final String KEY_FILENAME = "fileName";
	
	/** 文件的传输状态 */
	public static final String KEY_STATE = "state";
	
	/** 是否使用本地已经存在的文件 */
	public static final String KEY_USECACHE = "useCache";
	
	/** 是否删除本地的缓存文件 */
	public static final String KEY_DELETECACHEFILE = "deleteCacheFile";
    

	/** 建表语句 */
	public static final String CREATE_TAB = "CREATE TABLE IF NOT EXISTS "+ KEY_TAB_NAME +
            "(" 
            + KEY_ID + " integer PRIMARY KEY AUTOINCREMENT, "
            + KEY_URL +  " , " +  KEY_FILENAME +  " , " +  KEY_STATE +  " , "
            + KEY_USECACHE +  " , " +  KEY_DELETECACHEFILE +  
        " )";
	
	/** 删除表语句 */
    public static final String DELETE_TAB = "DROP TABLE IF EXISTS " + KEY_TAB_NAME;
    
    private String[] allColumns = new String[]{KEY_ID, KEY_URL, KEY_FILENAME, KEY_STATE,
    		KEY_USECACHE, KEY_DELETECACHEFILE};
    
    /** 私有化构造方法 */
    private DownloadListDAO(Context context){
		dbHelper = InnerDataBaseHelper.getInstances();
	}
    
    /**
     * 返回单例对象。
     * @param context
     * @return
     */
    public static DownloadListDAO getInstance(Context context){
    	if(instance == null){
    		synchronized (DownloadListDAO.class) {
				if(instance == null){
					instance = new DownloadListDAO(context);
				}
			}
    	}
    	return instance;
    }

	/**
	 * 查询出所有的下载记录
	 * @return
	 */
	public List<DownloadFile> findAllDownloadFile() {
		return findDownloadFile(null, null);
	}
	
	/**
	 * 创建新的或者更新已有DownloadFile对象。
	 * @param downloadFile
	 * @return
	 */
	public void doCreateOrUpdate(DownloadFile downloadFile){
		SQLiteDatabase dbc = dbHelper.getWritableDatabase();
		// 若文件存在，则更新数据。
		if(findByUrl(downloadFile.getUrl()) != null) {
			dbc.update(KEY_TAB_NAME, downloadFile.toContentValues(), KEY_URL + " = ? ", 
					new String[] { downloadFile.getUrl() });
		} else {
			// 新建一条记录。
			dbc.insert(KEY_TAB_NAME, null, downloadFile.toContentValues());
		}
	}
    
    /**
	 * 依据下载地址查询出指定的DownloadFile对象。
	 * @param url
	 * @return
	 */
	public DownloadFile findByUrl(String url) {
		List<DownloadFile> fileList = findDownloadFile(KEY_URL + " = ? ", new String[] { url });
		DownloadFile inst = null;
		if (fileList.size() > 0) {
			inst = fileList.get(0);
		}
		return inst;
	}
	
    /*
     * 查询出一组DownloadFile对象。
     */
    private List<DownloadFile> findDownloadFile(String where, String[] args) {
		List<DownloadFile> fileList = new ArrayList<DownloadFile>();
		SQLiteDatabase dbc = dbHelper.getWritableDatabase();
		Cursor c = null;
		try {
			c = dbc.query(KEY_TAB_NAME, allColumns, where, args, null, null, null);
			while (c.moveToNext()) {
				DownloadFile inst = DownloadFile.parseCursor(c);
				if(inst != null){
					fileList.add(inst);
				}
			}
		} finally {
			IOUtil.closeCursor(c);
		}
		return fileList;
	}
}
