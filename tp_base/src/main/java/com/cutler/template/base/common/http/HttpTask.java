package com.cutler.template.base.common.http;

import android.os.AsyncTask;

import com.cutler.template.base.common.http.HttpCaller.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
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
    private List<File> fileList;

    public HttpTask(String url, Map<String, String> params, HttpHandler callback, Method method) {
        this.url = url;
        this.params = params;
        this.callback = callback;
        this.method = method;
    }

    public HttpTask(String url, Map<String, String> params, HttpHandler callback, Method method, List<File> fileList) {
        this(url, params, callback, method);
        this.fileList = fileList;
    }

    protected Object doInBackground(Object... objects) {
        Object result = null;
        switch (method) {
            case POST:
                result = HttpCaller.doPost(url, params, String.class);
                break;
            case POST_UPLOAD_FILE:
                result = HttpCaller.doPostWithFiles(url, params, fileList, String.class);
                break;
            case GET:
                result = HttpCaller.doGet(url, params, String.class);
                break;
            case GET_BYTE:
                result = HttpCaller.doGet(url, params, byte[].class); // 主要用于服务端返回一个验证码图片。
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

    /**
     * 注意只有请求被顺利执行完毕后才会调用onPostExecute方法。
     * 如果想再请求发出后取消请求，则调用HttpTask的cancel方法即可。
     * @param result
     */
    protected void onPostExecute(Object result) {
        // 验证返回的数据是否正确。
        String errorName = HttpResponseUtil.validateJsonIsException(result);
        boolean isSuccess = (errorName == null);
        if (callback == null) {
            if (!isSuccess) {
                HttpResponseUtil.defaultException(errorName);
            }
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

    /**
     * 当请求被cancel的时候会在主线程中调用此方法。
     */
    protected void onCancelled() {
        if (callback != null) {
            callback.onCancelled();
        }
    }
}