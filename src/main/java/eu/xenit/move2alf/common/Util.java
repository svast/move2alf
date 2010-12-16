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
			return new String(md5);
		} catch (UnsupportedEncodingException e) {
			logger.error("UTF-8 encoding not supported");
			return null;
		} catch (NoSuchAlgorithmException e) {
			logger.error("MD5 algorithm not supported");
			return null;
		}
	}
}
