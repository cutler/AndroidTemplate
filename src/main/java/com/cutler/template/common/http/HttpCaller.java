package com.cutler.template.common.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.cutler.template.util.IOUtil;

/**
 * 本类用来发送http的post和get请求。
 * @author cutler
 */
public class HttpCaller {
	/**
	 * Http请求的根url
	 */
	public static String base_url = "http://gc.ditu.aliyun.com/";
	
	/**
	 * 请求参数的编码 
	 */
	public static final String ENCODE_CHARSETNAME = "UTF-8";
	
    /**
     * 连接超时时间
     */
	public static final int REQUEST_CONNECT_TIMEOUT = 10 * 1000;
	
    /**
     * 连接成功后，读取数据超时时间
     */
	public static final int REQUEST_READ_TIMEOUT = 15 * 1000;
	
	/**
	 * 单例对象
	 */
	private static HttpCaller instance;
	private HttpCaller(){ }
	public static HttpCaller getInstance() {
		if (instance == null) {
			synchronized (HttpCaller.class) {
				if (instance == null) {
					instance = new HttpCaller();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 本方法会在子线程中发送http请求，当请求结束后通过回调方法通知前端代码。
	 * @param url 请求的url
	 * @param params 请求参数
	 * @param callback 回调接口
	 * @param method 请求方式，默认为GET请求
	 */
	public void service(String url, Map<String, String> params, HttpHandler callback, Method method) {
		HttpTask httpTask = new HttpTask(url, params, callback, method);
		httpTask.execute();
	}
	
	public void service(String url, Map<String, String> params, HttpHandler callback) {
		service(url, params, callback, Method.GET);
	}
	
	/**
	 * HttpCaller自定义的请求的方法
	 * @author cutler
	 */
	public enum Method {
		/**
		 * POST请求方式 ，且请求返回数据的为String。
		 * */
		POST,
		
		/**
		 * GET请求方式，且请求返回数据的为String。
		 */
		GET,

		/**
		 * GET请求方式，且请求返回数据的为byte[]。
		 */
		GET_BYTE;
	}
	
	/**
	 * 在当前线程中发送GET请求。 
	 * @param <T> 返回值的类型由方法的调用者指定
	 * @param url 请求的url
	 * @param params 请求参数
	 * @return 若连接超时或发生其他错误，则返回null
	 */
	public <T> T doGet(String url, Map<String, String> params, Class<T> clazz) {
		HttpURLConnection conn = null;
		T result = null;
		try {
			// 将请求参数放到url末尾。
			StringBuilder sub = new StringBuilder(url);
			sub.append("?");
			sub.append(encodingRequestParams(params, ENCODE_CHARSETNAME));
			conn = prepareConnection(sub.toString(), Method.GET.toString());
			result = (T) executeRequest(conn, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return result;
	}
	
	/**
	 * 在当前线程中发送POST请求。
	 * @param <T> 返回值的类型由方法的调用者指定
	 * @param url 请求的url
	 * @param params 请求参数
	 * @return 若连接超时或发生其他错误，则返回null
	 */
	public <T> T doPost(String url, Map<String, String> params, Class<T> clazz) {
		HttpURLConnection conn = null;
		T result = null;
		try {
			conn = prepareConnectionForPost(url, params);
			result = (T) executeRequest(conn, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return result;
	}
	
	/**
	 * 将参数按照指定的编码进行编码。 
	 * @return 编码后的字符串
	 */
	public String encodingRequestParams(Map<String,String> params, String encode) throws Exception{
		StringBuilder sub = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sub.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode));
			sub.append("&");
		}
		if (sub.length() > 0) {
			sub.deleteCharAt(sub.length() - 1);
		}
		return sub.toString();
	}
	
	/**
	 * 向服务器端发送请求，并依据响应码来决定处理的方式。
	 */
	public Object executeRequest(HttpURLConnection conn, Class clazz)throws IOException, Exception {
		Object result;
		int code = conn.getResponseCode();
		// 200 和 400 时解析服务器返回的json。
		switch(code){
		case 200:	
			result = readInputStream(conn, conn.getInputStream(), clazz);
			break;
		case 400:
			result = readInputStream(conn, conn.getErrorStream(), clazz);
			break;
		default:
			result = HttpResponseUtil.buildJsonByErrorCode(code);
			break;
		}
		return result;
	}

	/**
	 * 将InputStream中的数据以字符串或字节数据的形式返回。 并保存服务器返回的cookie。
	 * @param input
	 * @return
	 */
	public Object readInputStream(HttpURLConnection conn ,InputStream input, Class clazz)throws Exception{
		// 保存本次获取到的cookie。 
		String cookie = conn.getHeaderField("set-cookie");
		if (cookie != null && cookie.trim().length() > 0) {
			// TODO
		}
		if ("gzip".equals(conn.getContentEncoding())) {
            input = new GZIPInputStream(input);
        }
		if (clazz == byte[].class) {
			return IOUtil.inputStream2ByteArray(input);
		} else {
			return IOUtil.inputStream2String(input, ENCODE_CHARSETNAME);
		}
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
	public HttpURLConnection prepareConnectionForPost(String url, Map<String, String> params)throws Exception{
		HttpURLConnection conn = prepareConnection(url, Method.POST.toString());
		conn.setDoOutput(true);
		OutputStream output = null;
		try {
			output = conn.getOutputStream();
			output.write(encodingRequestParams(params, ENCODE_CHARSETNAME).getBytes());
		} finally {
			if (output != null) {
				output.close();
			}
		}
		return conn;
	}
	
}