package com.cutler.template.base.common.http;

import com.cutler.template.base.util.io.IOUtil;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * 本类用来发送http的post和get请求。
 *
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
    public static final String ENCODE_CHARSET_NAME = "UTF-8";

    /**
     * 连接超时时间
     */
    public static final int REQUEST_CONNECT_TIMEOUT = 10 * 1000;

    /**
     * 连接成功后，读取数据超时时间
     */
    public static final int REQUEST_READ_TIMEOUT = 15 * 1000;

    /**
     * 本方法会在子线程中发送http请求，当请求结束后，会在主线程中通过回调方法通知前端代码。
     *
     * @param url      请求的url
     * @param params   请求参数
     * @param callback 回调接口
     * @param method   请求方式，默认为GET请求
     */
    public static HttpTask service(String url, Map<String, String> params, HttpHandler callback, Method method) {
        HttpTask httpTask = new HttpTask(url, params, callback, method);
        httpTask.execute();
        return httpTask;
    }

    public static HttpTask service(String url, Map<String, String> params, HttpHandler callback) {
        return service(url, params, callback, Method.GET);
    }

    /**
     * 传递参数的同时上传一组文件。
     *
     * @param url      请求的url
     * @param params   请求参数
     * @param fileList 文件列表
     * @param callback 回调接口
     */
    public HttpTask service(String url, Map<String, String> params, List<File> fileList, HttpHandler callback) {
        HttpTask httpTask = new HttpTask(url, params, callback, Method.POST_UPLOAD_FILE, fileList);
        httpTask.execute();
        return httpTask;
    }

    /**
     * HttpCaller自定义的请求的方法
     *
     * @author cutler
     */
    public enum Method {
        /**
         * POST请求方式 ，且请求返回数据的为String。
         */
        POST,

        /**
         * POST请求方式，同时上传一组文件。
         */
        POST_UPLOAD_FILE,

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
     *
     * @param <T>    返回值的类型由方法的调用者指定
     * @param url    请求的url
     * @param params 请求参数
     * @return 若连接超时或发生其他错误，则返回null
     */
    public static <T> T doGet(String url, Map<String, String> params, Class<T> clazz) {
        HttpURLConnection conn = null;
        T result = null;
        try {
            // 将请求参数放到url末尾。
            StringBuilder sub = new StringBuilder(url);
            sub.append("?");
            sub.append(encodingRequestParams(params, ENCODE_CHARSET_NAME));
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
     *
     * @param <T>    返回值的类型由方法的调用者指定
     * @param url    请求的url
     * @param params 请求参数
     * @return 若连接超时或发生其他错误，则返回null
     */
    public static <T> T doPost(String url, Map<String, String> params, Class<T> clazz) {
        HttpURLConnection conn = null;
        T result = null;
        try {
            conn = prepareConnection(url, Method.POST.toString());
            conn.setDoOutput(true);
            OutputStream output = null;
            try {
                output = conn.getOutputStream();
                output.write(encodingRequestParams(params, ENCODE_CHARSET_NAME).getBytes());
            } finally {
                IOUtil.closeOutputStream(output);
            }
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
     * 使用Post方式上传一组文件和参数。
     *
     * @param url      请求的url
     * @param params   请求参数
     * @param fileList 文件列表
     * @param <T>      返回值的类型由方法的调用者指定
     * @return
     */
    public static <T> T doPostWithFiles(String url, Map<String, String> params, List<File> fileList, Class<T> clazz) {
        HttpURLConnection conn = null;
        T result = null;
        try {
            String prefix = "--";
            String boundary = "******";
            String end = "\r\n";

            // 初始化HttpURLConnection。
            conn = prepareConnection(url, Method.POST.toString());
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setChunkedStreamingMode(1024 * 32); // 32k
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", ENCODE_CHARSET_NAME);
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = null;
            try {
                // 创建输出流。
                dos = new DataOutputStream(conn.getOutputStream());
                // 设置请求参数。
                StringBuilder sub = new StringBuilder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    // 加入一个分隔行
                    sub.append(prefix).append(boundary).append(end);
                    // 参数的名称单独占一行
                    sub.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"").append(end);
                    // 参数名称和值之间要来一个空行
                    sub.append(end);
                    // 参数的值
                    try {
                        sub.append(URLEncoder.encode(entry.getValue(), ENCODE_CHARSET_NAME)).append(end);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                dos.writeBytes(sub.toString());
                // 上传文件。
                for (int i = 0; i < fileList.size(); i++) {
                    File file = fileList.get(i);
                    // 文件的基本信息。
                    dos.writeBytes(prefix + boundary + end);
                    dos.writeBytes("Content-Disposition: form-data; name=\"file" + (i + 1) + "\"; filename=\"" + file.getName() + "\"" + end);
                    dos.writeBytes(end);
                    // 读取文件内容，并写入到输出流中。
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file.getAbsolutePath());
                        byte[] buffer = new byte[8192]; // 8k
                        int count = 0;
                        while ((count = fis.read(buffer)) != -1) {
                            dos.write(buffer, 0, count);
                        }
                        dos.writeBytes(end);
                    } finally {
                        IOUtil.closeInputStream(fis);
                    }
                }
                // 结束标志。
                dos.writeBytes(prefix + boundary + prefix + end);
                dos.flush();
            } finally {
                IOUtil.closeOutputStream(dos);
            }
            // 读取服务器返回结果
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
     *
     * @return 编码后的字符串
     */
    public static String encodingRequestParams(Map<String, String> params, String encode) throws Exception {
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
    public static Object executeRequest(HttpURLConnection conn, Class clazz) throws IOException, Exception {
        Object result;
        int code = conn.getResponseCode();
        // 200 和 400 时解析服务器返回的数据。
        switch (code) {
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
     *
     * @param input
     * @return
     */
    private static Object readInputStream(HttpURLConnection conn, InputStream input, Class clazz) throws Exception {
        String cookie = conn.getHeaderField("set-cookie");
        if (cookie != null && cookie.trim().length() > 0) {
            // TODO 保存本次获取到的cookie
        }
        if ("gzip".equals(conn.getContentEncoding())) {
            input = new GZIPInputStream(input);
        }
        if (clazz == byte[].class) {
            return IOUtil.inputStream2ByteArray(input);
        } else if (clazz == String.class) {
            return IOUtil.inputStream2String(input, ENCODE_CHARSET_NAME);
        } else {
            return null;
        }
    }

    /**
     * 初始化HttpURLConnection对象。
     */
    public static HttpURLConnection prepareConnection(String url, String method) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(REQUEST_CONNECT_TIMEOUT);
        conn.setReadTimeout(REQUEST_READ_TIMEOUT);
        conn.setRequestMethod(method);
        // TODO 请求之前设置cookie
        // conn.setRequestProperty("Cookie", cookie);
        conn.setRequestProperty("Accept-Encoding", "gzip");
        return conn;
    }

}