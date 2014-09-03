package com.cutler.template.model.block1;

import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.cutler.template.common.dao.DataBaseHelper;

public class BlockDAO {
	/** 数据库操作类 */
	private DataBaseHelper dbHelper;
		
	/** 本类的单例对象 */
	private static BlockDAO instance;
	
	/** 表名 */
	public static final String KEY_TAB_NAME = "block";
	
    /** 主键  */
	public static final String KEY_ID = "_id";
    
    /** 本条消息的发送时间 */
    public static final String KEY_TIME = "time";

    /** 本条消息是否由当前用户发出 */
    public static final String KEY_FROMSELF = "fromSelf";

    /** 本条消息的文本内容 */
    public static final String KEY_CONTENT = "content";
    
    /** 建表语句 */
	public static final String CREATE_TAB = "CREATE TABLE IF NOT EXISTS "+ KEY_TAB_NAME + 
            "(" 
            + KEY_ID + " integer PRIMARY KEY AUTOINCREMENT, "
            + KEY_TIME + " , "
            + KEY_FROMSELF + " , " + KEY_CONTENT + 
    " )";
	
	/** 删除表语句 */
    public static final String DELETE_TAB = "DROP TABLE IF EXISTS " + KEY_TAB_NAME;
    
    public static String[] allColumns = new String[]{KEY_ID, KEY_TIME, KEY_FROMSELF, KEY_CONTENT};
    
    /** 私有化构造方法 */
    private BlockDAO(Context context){
    	dbHelper = DataBaseHelper.getInstances(context);
    }
    
    /**
     * 创建单例对象。
     * @param context
     * @return
     */
    public static BlockDAO getInstance(Context context){
    	if(instance == null){
    		synchronized (BlockDAO.class) {
				if(instance == null){
					instance = new BlockDAO(context);
				}
			}
    	}
    	return instance;
    }
    
    /**
     * 指定当前用户的Id和topicId，获取旗下所有的 消息。
     * @param userId 对方的id。
     * @return
     */
    public List<BlockEntity> findListByKey(int type, String key, int start, int limit){
    	return null;
    }
    
    /**
     * 指定id，查看其是否在数据库中。
     * @param msg
     */
    public BlockEntity findById(int id){
		return null;
    }
    
    /**
     * 向数据库中插入一个Message对象。
     * @param entity
     */
    public void doInsert(BlockEntity entity){
    	
    }
    
    /**
     * 从数据库中删除一个Message对象。
     * @param msg
     */
    public void doRemove(int message_Id){
    }  
    
    /**
     * 更新数据库中的一个Message对象。
     * @param entity
     */
    public int doUpdate(BlockEntity entity){
		return 0;
    }
    
	/**
     * 关闭
     * @param c
     */
	private void closeCursor(Cursor c) {
		if(c != null){
    		c.close();
    		c = null;
    	}
	}
}
