package com.cutler.template.common;

import com.cutler.template.MainApplication;
import com.cutler.template.util.SharedPreferencesUtil;


/**
 * 本类保存一些程序运行时所需要的全部变量的值。
 * @author cutler
 *
 */
public class SystemParams {

	/**
	 * 返回服务端的当前时间。
	 * @return
	 */
	public static long getServerCurrentTime(){
		return System.currentTimeMillis() - SystemParams.getTimeOffset();
	}

	/**
	 * 从本地读取客户端时间与服务端时间的差值。
	 * 注：服务器的时间差应当又你事先保存在本地。
	 * @return
	 */
	public static long getTimeOffset() {
		return SharedPreferencesUtil.getParams(MainApplication.getInstance(), Config.KEY_TIME_OFFSET, 0);
	}

}