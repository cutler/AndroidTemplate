package com.cutler.template.util.base;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 编码加密
 *
 * @author cutler
 */
public class CryptoUtil {

    /**
     * 将参数key，进行sha256加密，然后转换成16进制的表示形式。
     *
     * @param text
     * @return
     */
    public static String getSha256Text(String text) {
        return digest(text, "SHA-256");
    }

    /**
     * 将参数key，进行MD5加密，然后转换成16进制的表示形式。
     *
     * @param text
     * @return
     */
    public static String getMD5Text(String text) {
        return digest(text, "MD5");
    }

    private static String digest(String text, String method) {
        String retVal;
        try {
            MessageDigest mDigest = MessageDigest.getInstance(method);
            mDigest.update(text.getBytes());
            retVal = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            retVal = String.valueOf(text.hashCode());
        }
        return retVal;
    }

    /**
     * 将字节数组转换成16进制的字符串。
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
