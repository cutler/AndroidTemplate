package com.cutler.template.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 编码加密
 * @author cutler
 *
 */
public class CryptoUtil {

	/**
	 * 将参数key，进行md5加密，然后转换成16进制的表示形式。
	 * 
	 * @param key
	 * @return
	 */
	public static String getSha256Text(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
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
