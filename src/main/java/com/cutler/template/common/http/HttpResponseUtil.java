package com.cutler.template.common.http;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.cutler.template.MainApplication;
import com.cutler.template.R;

/**
 * 处理Http请求结果的工具类。 
 * @author cutler
 */
class HttpResponseUtil {
	
	/**
	 * 验证服务端返回的json是否合法。
	 * 示例： 服务端返回的JSON值为： {"err":"reponseError","success":false}
	 * success用来标识服务端是否成功处理了请求。
	 * - 若值为true，则成功处理。
	 * - 若值为false，则客户端会尝试读取err字段，获取错误的类型。
	 */
	protected static String validateJsonIsException(Object result) {
		String errorName = null;
		if (result == null) {
			// 当无网络等原因导致请求超时时，result的值为null。
			errorName = "network";
		} else if (result instanceof JSONObject) {
			try {
				JSONObject jsonObj = (JSONObject) result;
				// 如果返回的json没有遵循上面列出的规范（即没返回success字段），那么就视json为正确的json。
				if(!jsonObj.optBoolean("success", true)) {
					errorName = jsonObj.optString("err", "unknown");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return errorName;
	}
	
	/**
	 * 此方法会在主线程中被调用，必须得在strings.xml文件中配置下面的值：
	 * <string name="error_unknown">未知异常</string>
	 * 可选的配置有：
	 * <string name="error_network">网络不给力啊</string>
	 * <string name="error_500">服务端异常啊</string>
	 */
	protected static void defaultException(String errorName) {
		Context ctx = MainApplication.getInstance();
		String errorText = null;
		int textResId = ctx.getResources().getIdentifier("error_" + errorName.replace(".", "_"), "string", ctx.getPackageName());
		if(textResId <= 0){
			errorText = ctx.getString(R.string.error_unknown) + ":"+errorName;
		} else {
			errorText = ctx.getString(textResId);
		}
		Toast.makeText(ctx, errorText, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 如果请求返回了200、400以外的状态吗，则为它们构造一个json，以便后续统一处理。
	 * @param code
	 * @return
	 */
	protected static String buildJsonByErrorCode(int code) {
		JSONObject result = new JSONObject();
		try {
			// {"err":"500","success":false}
			result.put("success", false);
			result.put("err", ""+code);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
}
