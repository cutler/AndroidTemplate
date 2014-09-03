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
 */
public class HttpCaller {
	public static String base_url = "http://addp.zlimits.com/addp/admin/service/";
	private static HttpCaller instance;
	/** post请求方式 */
	public static final String REQUEST_POST_METHOD = "POST";
	/** get请求方式 */
	public static final String REQUEST_GET_METHOD = "GET";
	/** 请求参数的编码 */
	public static final String ENCODE_CHARSETNAME = "UTF-8";
    /** 连接超时时间  **/
	public static final int REQUEST_CONNECT_TIMEOUT = 10 * 1000;
    /** 读取数据超时时间**/
	public static final int REQUEST_READ_TIMEOUT = 15 * 1000;
	
	private HttpCaller(){
		
	}
	
	public static HttpCaller getInstance(){
		if(instance == null){
			instance = new HttpCaller();
		}
		return instance;
	}
	
	/**
	 * 发送get请求。 
	 * @return 若连接超时或发生其他错误，则返回null。 
	 */
	public String doGet(String url, Map<String, String> params) {
		HttpURLConnection conn = null;
		String json = null;
		try {
			url = appendParamsToUrl(url, params);
			conn = prepareConnection(url, REQUEST_GET_METHOD);
			json = executeRequest(conn, json);
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
	public String doPost(String url, Map<String, String> params) {
		HttpURLConnection conn = null;
		String json = null;
		try {
			conn = prepareConnectionForPost(url, REQUEST_POST_METHOD, params, false);
			json = executeRequest(conn, json);
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
	 *  数据以json格式发送Post请求。
	 * @return 若连接超时或发生其他错误，则返回null。 
	 */
	public String doPost(String url, Map<String, String> params, boolean isJson) {
		HttpURLConnection conn = null;
		String json = null;
		try {
			conn = prepareConnectionForPost(url, REQUEST_POST_METHOD, params, isJson);
			json = executeRequest(conn, json);
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
	 * 发送http请求，当请求结束后通过回调方法通知前端代码。
	 * @param url	接口的地址
	 * @param name	接口名称
	 * @param method	请求方法(get、post)
	 * @param params	请求参数
	 * @param handler	回调接口
	 * @param isJsonForRequst	是否以JSON格式发送请求参数
	 */
	public void service(String url, String name, String method, Map<String, String> params, 
			IHttpHandler handler, boolean isJsonForRequst){
		HttpTask httpTask = new HttpTask(url, name, method, handler, isJsonForRequst);
		httpTask.execute(params);
	}
	
	/**
	 * 拼接请求参数。
	 * @param url
	 * @param params
	 * @return
	 */
	public String appendParamsToUrl(String url,Map<String, String> params)throws Exception {
		StringBuilder sub = new StringBuilder(url);
		sub.append("?");
		sub.append(encodingRequestParams(params, ENCODE_CHARSETNAME, false));
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
	 * @param conn
	 * @param json
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	private String executeRequest(HttpURLConnection conn, String json)throws IOException, Exception {
		int code = conn.getResponseCode();
		switch(code){
		case 200:	// 200 和 400 时解析服务器返回的json。
			json = inputStream2String(conn, conn.getInputStream());
			break;
		case 400:
			json = inputStream2String(conn, conn.getErrorStream());
			break;
		case 404:
			json = createJson(code);
			break;
		default:	// 其他状态码，如5xx等，统一按照500处理。
			json = createJson(500);
			break;
		}
		return json;
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
	 * 将InputStream中的数据以字符串的形式返回。 并保存服务器返回的cookie。
	 * @param input
	 * @return
	 */
	public String inputStream2String(HttpURLConnection conn ,InputStream input)throws Exception{
		// 保存本次获取到的cookie。 和服务器端已约定好了，只有注册、登录接口会返回cookie。
		String cookie = conn.getHeaderField("set-cookie");
		if (cookie != null && cookie.trim().length() > 0) {
			// 保存cookie。
		}
		if ("gzip".equals(conn.getContentEncoding())) {
            input = new GZIPInputStream(input);
        }
		return IOUtil.inputStream2String(input, ENCODE_CHARSETNAME);
	}

	/**
	 * 初始化HttpURLConnection对象。
	 */
	public HttpURLConnection prepareConnection(String url,String method)throws Exception{
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setConnectTimeout(REQUEST_CONNECT_TIMEOUT);
		conn.setReadTimeout(REQUEST_READ_TIMEOUT);
		conn.setRequestMethod(method);
		// 请求之前，设置cookie。
		// StringBuilder cookie = new StringBuilder();
		// conn.setRequestProperty("Cookie", cookie.toString());
		conn.setRequestProperty("Accept-Encoding", "gzip");
		return conn;
	}
	
	/**
	 * 为Post请求初始化HttpURLConnection对象。
	 */
	public HttpURLConnection prepareConnectionForPost(String url,String method,Map<String,String> params,boolean isJson)throws Exception{
		HttpURLConnection conn = prepareConnection(url, method);
		conn.setDoOutput(true);
		OutputStream output = null;
		try {
			if (isJson) {
				conn.setRequestProperty("Content-Type","text/json; charset=UTF-8");
			}
			output = conn.getOutputStream();
			output.write(encodingRequestParams(params, ENCODE_CHARSETNAME,isJson).getBytes());
		} finally {
			if (output != null) {
				output.close();
			}
		}
		return conn;
	}
	
}
