package com.cutler.testapplication.test.http;


import com.cutler.template.common.http.HttpCaller;
import com.cutler.template.common.http.HttpCaller.Method;
import com.cutler.template.common.http.HttpHandler;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * http模块的测试类
 * @author cutler
 */
public class HttpTest {

    /**
     * GET请求，并要求返回值为byte[]类型。
     */
    public static void testGetByteRequest() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("a", "北京市");
        HttpCaller.getInstance().service(HttpCaller.base_url + "geocoding", params, new HttpHandler() {
            public void handleResult(Object result) {
                try {
                    System.out.println("testGetByteRequest = " + new String((byte[]) result, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                }
            }
        }, Method.GET_BYTE);
    }

    /**
     * GET请求，并要求返回值为String类型。
     */
    public static void testGetStringRequest() {
        HashMap<String, String> params = new HashMap<String, String>();
        HttpCaller.getInstance().service("http://www.baidu.com/", params, new HttpHandler() {
            public void handleResult(Object result) {
                System.out.println("testGetStringRequest = " + result);
            }
        });
    }

    /**
     * 测试Post请求
     */
    public static void testPostStringRequest() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("a", "北京市");
        HttpCaller.getInstance().service(HttpCaller.base_url + "geocoding", params, new HttpHandler() {
            public void handleResult(Object result) {
                System.out.println("testPostStringRequest = " + result);
            }
        }, Method.POST);
    }

    /**
     * 测试404时，程序代码是否能正确处理。
     */
    public static void test404Request() {
        HashMap<String, String> params = new HashMap<String, String>();
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
    }

    /**
     * 测试网络超时、读取超时等原因导致的请求返回值result为null时，程序代码是否能正确处理。
     * 注意，目前的代码，并没有区分导致result为null的具体原因，而是统一视为网络不给力。
     */
    public static void testNetworkRequest() {
        HashMap<String, String> params = new HashMap<String, String>();
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
    }

}
