package eu.xenit.move2alf.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
	private static final Logger logger = LoggerFactory
			.getLogger(Util.class);

	public static String convertToMd5(String str) {
		try {
			byte[] bytes = str.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] md5 = md.digest(bytes);
			StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < md5.length; i++) {
	          sb.append(Integer.toString((md5[i] & 0xff) + 0x100, 16).substring(1));
	        }
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			logger.error("UTF-8 encoding not supported");
			return null;
		} catch (NoSuchAlgorithmException e) {
			logger.error("MD5 algorithm not supported");
			return null;
		}
	}
}
