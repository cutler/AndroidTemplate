package com.cutler.template.base.util.io;

import android.database.Cursor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * IO读取的工具类
 *
 * @author cutler
 */
public class IOUtil {
    /**
     * 将InputStream中的数据读出来，放入一个byte[]中。
     */
    public static byte[] inputStream2ByteArray(InputStream input) {
        if (input == null) {
            return null;
        }
        ByteArrayOutputStream output = null;
        byte[] data = null;
        try {
            data = new byte[1024 * 32];
            output = new ByteArrayOutputStream();
            int len;
            while ((len = input.read(data)) != -1) {
                output.write(data, 0, len);
            }
            data = output.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeInputStream(input);
            closeOutputStream(output);
        }
        return data;
    }

    /**
     * 将InputStream中的数据以字符串的形式返回。
     */
    public static String inputStream2String(InputStream input, String encoding) {
        String str = null;
        try {
            str = new String(inputStream2ByteArray(input), encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 将InputStream中的数据写入到OutputStream中，完毕后关闭两个流。
     */
    public static boolean inputStream2OutputStream(InputStream input, OutputStream output) {
        boolean isFinish = true;
        try {
            byte[] array = new byte[1024];
            int len = -1;
            while ((len = input.read(array)) != -1) {
                output.write(array, 0, len);
            }
        } catch (Exception e) {
            isFinish = false;
        } finally {
            closeInputStream(input);
            closeOutputStream(output);
        }
        return isFinish;
    }

    /**
     * 将str写入到OutputStream中。
     */
    public static void stringToOutputStream(String str, OutputStream output) {
        if (str != null && output != null) {
            try {
                output.write(str.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeOutputStream(output);
            }
        }
    }

    /**
     * 关闭输入流。
     */
    public static void closeInputStream(InputStream input) {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 关闭输出流。
     */
    public static void closeOutputStream(OutputStream output) {
        if (output != null) {
            try {
                output.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 关闭Cursor
     */
    public static void closeCursor(Cursor c) {
        if (c != null) {
            c.close();
        }
    }

}
