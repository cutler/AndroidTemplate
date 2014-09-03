package com.cutler.template.common.http;

import java.util.Map;

import org.json.JSONTokener;

import android.os.AsyncTask;

/**
 * 在AsyncTask基础上封装一层，用于Http请求。
 * @author cutler
 *
 */
public class HttpTask extends AsyncTask<Map<String, String>, Integer, Object>{
	
	private String name;
	private String method;
	private IHttpHandler handler;
	private boolean isJsonForRequst;
	private String url;
	
	public HttpTask(String url, String name, String method, IHttpHandler handler
			, boolean isJsonForRequst){
		this.url = url;
		this.name = name;
		this.method = method;
		this.handler = handler;
		this.isJsonForRequst = isJsonForRequst;
	}

	@Override
	protected void onPostExecute(Object json) {
		Map<String, String> map = HttpResponseUtils.getInstance().validateJsonIsException(json);
		boolean isSuccess = map.get(HttpResponseUtils.KEY_ERROR_NAME) == null;
		handler.onBeforeHandle(isSuccess);
		if(isSuccess){
			handler.handleResult(json);
		} else {
			if(!handler.handleError(map.get(HttpResponseUtils.KEY_ERROR_NAME))){
				HttpResponseUtils.getInstance().defaultException(map.get(HttpResponseUtils.KEY_ERROR_NAME));
			}
		}
		handler.onAfterHandle(isSuccess);
	}
	
	@Override
	protected Object doInBackground(Map<String, String>... params) {
		String json = null;
		String serviceName;
		serviceName = url + name;
		if (method.equals(HttpCaller.REQUEST_POST_METHOD)) {
			json = HttpCaller.getInstance().doPost(serviceName, params[0], isJsonForRequst);
		} else if (method.equals(HttpCaller.REQUEST_GET_METHOD)) {
			json = HttpCaller.getInstance().doGet(serviceName, params[0]);
		}
		if (json != null && json.equals("")) {
			return json;
		}
		JSONTokener jsonParser = new JSONTokener(json);
		Object obj = null;
		try {
			obj = jsonParser.nextValue();
		} catch (Exception e) {
		}
		return obj;
	}

}