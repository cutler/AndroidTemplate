package com.cutler.template.common.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.cutler.template.util.IOUtil;

/**
 * 本类用来发送post和get请求。
 * 使用 HttpCaller.getInstance().service(params)来调用接口。 其中必须要提供的参数有：
 * -  KEY_URL、KEY_METHOD、KEY_CALLBACK、KEY_PARAMS
 */
public class HttpCaller {
	public static String base_url = "http://addp.zlimits.com/addp/admin/service/";
	private static HttpCaller instance;
	/** post请求方式 */
	public static final String METHOD_POST = "POST";
	/** post请求方式  并要求请求参数以json的形式发送给服务端*/
	public static final String METHOD_POST_JSON = "POST_JSON";
	/** get请求方式 */
	public static final String METHOD_GET = "GET";
	/** get请求方式  并要求返回byte[]类型的数据 */
	public static final String METHOD_GET_BYTE = "GET_BYTE";
	/** 请求参数的编码 */
	public static final String ENCODE_CHARSETNAME = "UTF-8";
    /** 连接超时时间  **/
	public static final int REQUEST_CONNECT_TIMEOUT = 10 * 1000;
    /** 读取数据超时时间**/
	public static final int REQUEST_READ_TIMEOUT = 15 * 1000;
	
	/** 请求的url **/
	public static final String KEY_URL = "url";
	/** 请求的方式 **/
	public static final String KEY_METHOD = "method";
	/** 请求完成后的回调 **/
	public static final String KEY_CALLBACK = "callback";
	/** 请求时需要传递给服务器端的参数 **/
	public static final String KEY_PARAMS = "params";
	
	private HttpCaller(){
		
	}
	
	public static HttpCaller getInstance(){
		if(instance == null){
			instance = new HttpCaller();
		}
		return instance;
	}
	
	/**
	 * 在子线程中发送http请求，当请求结束后通过回调方法通知前端代码。
	 * @param params
	 */
	public void service(Map<String, Object> params) {
		HttpTask httpTask = new HttpTask(params);
		httpTask.execute();
	}
	
	/**
	 * 发送get请求。 
	 * @return 若连接超时或发生其他错误，则返回null。 
	 */
	public String doGet(Map<String, Object> params) {
		HttpURLConnection conn = null;
		String json = null;
		try {
			String url = appendParamsToUrl(params);
			conn = prepareConnection(url, METHOD_GET);
			json = (String) executeRequest(conn, true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return json;
	}
	
	/**
	 * 发送Post请求。
	 * @return 若连接超时或发生其他错误，则返回null。 
	 */
	public String doPost(Map<String, Object> params) {
		HttpURLConnection conn = null;
		String json = null;
		try {
			conn = prepareConnectionForPost(params);
			json = (String) executeRequest(conn, true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return json;
	}
	
	
	/**
	 * 发送get请求，并将InputStream中的数据转为byte[]类型的数据返回。
	 * @param url
	 * @param params
	 * @return
	 */
	public byte[] doGetForByteArray(Map<String, Object> params) {
		HttpURLConnection conn = null;
		byte[] array = null;
		try {
			String url = appendParamsToUrl(params);
			conn = prepareConnection(url, METHOD_GET);
			array = (byte[]) executeRequest(conn, false);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return array;
	}

	/**
	 *  数据以json格式发送Post请求。
	 * @return 若连接超时或发生其他错误，则返回null。 
	 */
	public String doPostForJSON(Map<String, Object> params) {
		HttpURLConnection conn = null;
		String json = null;
		try {
			conn = prepareConnectionForPost(params);
			json = (String) executeRequest(conn, true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return json;
	}
	
	/**
	 * 拼接请求参数。
	 * @param url
	 * @param params
	 * @return
	 */
	public String appendParamsToUrl(Map<String, Object> params)throws Exception {
		StringBuilder sub = new StringBuilder((String) params.get(HttpCaller.KEY_URL));
		sub.append("?");
		sub.append(encodingRequestParams((Map<String, String>) params.get(KEY_PARAMS), ENCODE_CHARSETNAME, false));
		return sub.toString();
	}
	
	/**
	 *	将参数按照指定的编码进行编码。 
	 */
	public String encodingRequestParams(Map<String,String> params,String encode,boolean isJson) throws Exception{
		StringBuilder sub = new StringBuilder();
		// 发送Json格式的数据。
		if (isJson) {
			JSONObject jsonData = new JSONObject();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				jsonData.put(entry.getKey(), entry.getValue());
			}
			sub.append(jsonData.toString());
		} else {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sub.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode));
				sub.append("&");
			}
			if (sub.length() > 0) {
				sub.deleteCharAt(sub.length() - 1);
			}
		}
		return sub.toString();
	}
	
	/**
	 * 向服务器端发送请求，并依据响应码来决定处理的方式。
	 */
	private Object executeRequest(HttpURLConnection conn, boolean parseToString)throws IOException, Exception {
		Object result;
		int code = conn.getResponseCode();
		switch(code){
		case 200:	// 200 和 400 时解析服务器返回的json。
			result = inputStream2String(conn, conn.getInputStream(), parseToString);
			break;
		case 400:
			result = inputStream2String(conn, conn.getErrorStream(), parseToString);
			break;
		case 404:
			result = createJson(code);
			break;
		default:	// 其他状态码，如5xx等，统一按照500处理。
			result = createJson(500);
			break;
		}
		return result;
	}

	/**
	 * 客户端自己构建json,以便后续统一处理
	 * @param code
	 * @return
	 * @throws JSONException
	 */
	private String createJson(int code) throws JSONException {
		String json;
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("err", String.valueOf(code));
		json = jsonObj.toString();
		return json;
	}

	/**
	 * 将InputStream中的数据以字符串或字节数据的形式返回。 并保存服务器返回的cookie。
	 * @param input
	 * @return
	 */
	public Object inputStream2String(HttpURLConnection conn ,InputStream input, boolean parseToString)throws Exception{
		// 保存本次获取到的cookie。 和服务器端已约定好了，只有注册、登录接口会返回cookie。
		String cookie = conn.getHeaderField("set-cookie");
		if (cookie != null && cookie.trim().length() > 0) {
			// 保存cookie。TODO
		}
		if ("gzip".equals(conn.getContentEncoding())) {
            input = new GZIPInputStream(input);
        }
		return parseToString ? IOUtil.inputStream2String(input, ENCODE_CHARSETNAME)
				: IOUtil.inputStream2ByteArray(input);
	}

	/**
	 * 初始化HttpURLConnection对象。
	 */
	public HttpURLConnection prepareConnection(String url, String method)throws Exception{
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setConnectTimeout(REQUEST_CONNECT_TIMEOUT);
		conn.setReadTimeout(REQUEST_READ_TIMEOUT);
		conn.setRequestMethod(method);
		// 请求之前，设置cookie。 TODO
		// conn.setRequestProperty("Cookie", cookie);
		conn.setRequestProperty("Accept-Encoding", "gzip");
		return conn;
	}
	
	/**
	 * 为Post请求初始化HttpURLConnection对象。
	 */
	public HttpURLConnection prepareConnectionForPost(Map<String, Object> params)throws Exception{
		HttpURLConnection conn = prepareConnection((String) params.get(HttpCaller.KEY_URL), METHOD_POST);
		conn.setDoOutput(true);
		OutputStream output = null;
		try {
			boolean isJson = METHOD_POST_JSON.equals(params.get(HttpCaller.KEY_METHOD));
			if (isJson) {
				conn.setRequestProperty("Content-Type","text/json; charset=UTF-8");
			}
			output = conn.getOutputStream();
			output.write(encodingRequestParams((Map<String, String>) params.get(KEY_PARAMS), ENCODE_CHARSETNAME, isJson).getBytes());
		} finally {
			if (output != null) {
				output.close();
			}
		}
		return conn;
	}
	
}