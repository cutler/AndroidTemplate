package com.cutler.template.common.http;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;

import com.cutler.template.MainApplication;
import com.cutler.template.R;

/**
 * 处理Http响应的工具类。 比如：显示错误信息等
 * @author cutler
 *
 */
public class HttpResponseUtils{
	/**
	 * 异常的名称。
	 */
	public static String KEY_ERROR_NAME = "errorName";
	
	// 单例对象。
	private static HttpResponseUtils inst;
	
	private HttpResponseUtils(){ }
	
	/**
	 * 返回单例对象。
	 * @param ctx
	 * @return
	 */
	public static HttpResponseUtils getInstance(){
		if(inst == null){
			synchronized (HttpResponseUtils.class) {
				if(inst == null){
					inst = new HttpResponseUtils();
				}
			}
		}
		return inst;
	}
	
	/**
	 * 验证服务端返回的json是否合法。
	 * 	示例： 服务端返回的一个错误的JSON值： 	{"err":{"type":"Sys.Server"},"success":false}
	 * @param jsonObj
	 * @return
	 */
	public Map<String, String> validateJsonIsException(Object json){
		Map<String, String> map = new HashMap<String, String>();
		if (json == null) {
			map.put(KEY_ERROR_NAME, "error_network");
		} else {
			try {
				JSONObject jsonObj = (JSONObject) json;
				if(!jsonObj.optBoolean("success", false)){
					JSONObject errObj = jsonObj.optJSONObject("err");
					if(errObj != null){
						map.put(KEY_ERROR_NAME, errObj.optString("type", "unknown"));
					} else {
						map.put(KEY_ERROR_NAME, "unknown");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	
	/**
	 * 验证参数json是否是异常json。
	 *  若是，则返回其对应的提示信息。
	 *  若不是，则返回null，随后程序可以执行解析json的操作。
	 * @param json
	 * @param result 本方法返回的提示信息。
	 * @return
	 */
	public void defaultException(String errorName) {
		Context ctx = MainApplication.getInstance();
		String stringName = "error_" + errorName.replace(".", "_");
		int textResId = ctx.getResources().getIdentifier(stringName, "string", ctx.getPackageName());
		stringName = ctx.getString(textResId <= 0 ? R.string.error_unknown : textResId);
		System.out.println("Http请求发生了错误："+stringName);
	}
	
}