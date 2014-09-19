package com.cutler.template.common.http;

import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;

/**
 * 在AsyncTask基础上封装一层，用于Http请求。
 * @author cutler
 *
 */
public class HttpTask extends AsyncTask<Object, Object, Object>{
	private String method;
	private IHttpHandler callback;
	private Map<String,Object> params;
	
	public HttpTask(Map<String,Object> params){
		this.params = params;
		method = (String) params.get(HttpCaller.KEY_METHOD);
		callback = (IHttpHandler) params.get(HttpCaller.KEY_CALLBACK);
	}
	
	@Override
	protected Object doInBackground(Object... objects) {
		Object result = null;
		if (method.equals(HttpCaller.METHOD_POST)) {
			result = HttpCaller.getInstance().doPost(params);
		} else if (method.equals(HttpCaller.METHOD_POST_JSON)) {
			result = HttpCaller.getInstance().doPostForJSON(params);
		} else if (method.equals(HttpCaller.METHOD_GET)) {
			result = HttpCaller.getInstance().doGet(params);
		} else if (method.equals(HttpCaller.METHOD_GET_BYTE)) {
			result = HttpCaller.getInstance().doGetForByteArray(params);
		}
		if (result != null && result.equals("")) {
			return result;
		}
		if (result instanceof String) {
			JSONTokener jsonParser = new JSONTokener((String) result);
			try {
				result = jsonParser.nextValue();
			} catch (Exception e) { }
		}
		return result;
	}

	@Override
	protected void onPostExecute(Object json) {
		boolean isSuccess = true;
		Map<String, String> map = null;
		if(json == null || json instanceof JSONObject){
			map = HttpResponseUtils.getInstance().validateJsonIsException(json);
			isSuccess = map.get(HttpResponseUtils.KEY_ERROR_NAME) == null;
		}
		callback.onBeforeHandle(isSuccess);
		if(isSuccess){
			callback.handleResult(json);
		} else {
			if(!callback.handleError(map.get(HttpResponseUtils.KEY_ERROR_NAME))){
				HttpResponseUtils.getInstance().defaultException(map.get(HttpResponseUtils.KEY_ERROR_NAME));
			}
		}
		callback.onAfterHandle(isSuccess);
	}
	

}