package com.cutler.template.test.http;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import android.test.AndroidTestCase;

import com.cutler.template.common.http.HttpCaller;
import com.cutler.template.common.http.HttpCaller.Method;
import com.cutler.template.common.http.HttpHandler;

/**
 * 测试类得继承AndroidTestCase，这样测试类才能有getContext()来获取当前的上下文变量。
 * @author cutler
 */
public class HttpTest extends AndroidTestCase {

	/**
	 * GET请求，并要求返回值为byte[]类型。
	 * @throws InterruptedException 
	 */
	public void testGetByteRequest() throws InterruptedException {
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("a", "北京市");
		HttpCaller.getInstance().service(HttpCaller.base_url+"geocoding", params, new HttpHandler() {
			public void handleResult(Object result) {
				try {
					System.out.println("testGetByteRequest = " + new String((byte[])result,"UTF-8"));
				} catch (UnsupportedEncodingException e) {}
			}
		}, Method.GET_BYTE);
		// 让当前线程阻塞，不然在请求返回之前，测试就结束了。
		Thread.sleep(HttpCaller.REQUEST_CONNECT_TIMEOUT);
	}
	
	/**
	 * GET请求，并要求返回值为String类型。
	 * @throws InterruptedException 
	 */
	public void testGetStringRequest() throws InterruptedException {
		HashMap<String,String> params = new HashMap<String,String>();
		HttpCaller.getInstance().service("http://www.baidu.com/", params, new HttpHandler() {
			public void handleResult(Object result) {
				System.out.println("testGetStringRequest = " + result);
			}
		});
		// 让当前线程阻塞，不然在请求返回之前，测试就结束了。
		Thread.sleep(HttpCaller.REQUEST_CONNECT_TIMEOUT);
	}
	
	/**
	 * 测试Post请求
	 * @throws InterruptedException 
	 */
	public void testPostStringRequest() throws InterruptedException {
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("a", "北京市");
		HttpCaller.getInstance().service(HttpCaller.base_url+"geocoding", params, new HttpHandler() {
			public void handleResult(Object result) {
				System.out.println("testPostStringRequest = " + result);
			}
		}, Method.POST);
		// 让当前线程阻塞，不然在请求返回之前，测试就结束了。
		Thread.sleep(HttpCaller.REQUEST_CONNECT_TIMEOUT);
	}
	
	/**
	 * 测试404时，程序代码是否能正确处理。
	 * @throws InterruptedException 
	 */
	public void test404Request() throws InterruptedException {
		HashMap<String,String> params = new HashMap<String,String>();
		HttpCaller.getInstance().service("http://gc.ditu.aliyun.com/geocoding2", params, new HttpHandler() {
			public void handleResult(Object result) {
				System.out.println("test404Request handleResult = " + result);
			}
			@Override
			public boolean handleError(String result) {
				System.out.println("test404Request handleError = " + result);
				return false;
			}
		});
		// 让当前线程阻塞，不然在请求返回之前，测试就结束了。
		Thread.sleep(HttpCaller.REQUEST_CONNECT_TIMEOUT);
	}
	
	/**
	 * 测试网络超时、读取超时等原因导致的请求返回值result为null时，程序代码是否能正确处理。
	 * 注意，目前的代码，并没有区分导致result为null的具体原因，而是统一视为网络不给力。
	 * @throws InterruptedException 
	 */
	public void testNetworkRequest() throws InterruptedException{
		HashMap<String,String> params = new HashMap<String,String>();
		HttpCaller.getInstance().service("http://www.sfsd.sdfsfs", params, new HttpHandler() {
			public void handleResult(Object result) {
				System.out.println("testNetworkRequest handleResult = " + result);
			}
			@Override
			public boolean handleError(String result) {
				System.out.println("testNetworkRequest handleError = " + result);
				return false;
			}
		});
		// 让当前线程阻塞，不然在请求返回之前，测试就结束了。  由于是测试超时，所以此处需要多等待5秒。
		Thread.sleep(HttpCaller.REQUEST_CONNECT_TIMEOUT + 5000);
	}
	
}
