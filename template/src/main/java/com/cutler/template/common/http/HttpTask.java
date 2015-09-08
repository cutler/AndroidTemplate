package com.cutler.template.common.http;

import android.os.AsyncTask;

import com.cutler.template.common.http.HttpCaller.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * 在AsyncTask基础上封装一层，用于Http请求。
 *
 * @author cutler
 */
public class HttpTask extends AsyncTask<Object, Object, Object> {
    private String url;
    private Method method;
    private HttpHandler callback;
    private Map<String, String> params;

    public HttpTask(String url, Map<String, String> params, HttpHandler callback, Method method) {
        this.url = url;
        this.params = params;
        this.method = method;
        this.callback = callback;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        Object result = null;
        switch (method) {
            case POST:
                result = HttpCaller.getInstance().doPost(url, params, String.class);
                break;
            case GET:
                result = HttpCaller.getInstance().doGet(url, params, String.class);
                break;
            case GET_BYTE:
                result = HttpCaller.getInstance().doGet(url, params, byte[].class);
                break;
        }
        /*
		 * 如果返回值是String类型的，则依据它的内容将其解析成不同的JSON数据。
		 * 可能返回的类型有：JSONObject, JSONArray。
		 */
        if (result != null && result instanceof String
                && ((String) result).trim().length() > 0) {
            // 先尝试将字符串转为JSONObject类型。
            try {
                result = new JSONObject((String) result);
            } catch (Exception e) {
                // 再尝试将字符串转为JSONArray类型。 如果依然转换失败，则让字符串保持原值返回到前端代码中。
                try {
                    result = new JSONArray((String) result);
                } catch (JSONException e1) {
                }
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Object result) {
        // 验证返回的数据是否正确。
        String errorName = HttpResponseUtil.validateJsonIsException(result);
        boolean isSuccess = (errorName == null);
        if (callback == null && !isSuccess) {
            HttpResponseUtil.defaultException(errorName);
        } else {
            callback.onBeforeHandle(isSuccess);
            if (isSuccess) {
                // 当且仅当请求成功后，才会调用 handleResult方法。
                callback.handleResult(result);
            } else {
                // 请求失败则调用handleError方法，如果handleError方法返回false，则调用默认的错误处理器进行处理。
                if (!callback.handleError(errorName)) {
                    HttpResponseUtil.defaultException(errorName);
                }
            }
            callback.onAfterHandle(isSuccess);
        }
    }

}