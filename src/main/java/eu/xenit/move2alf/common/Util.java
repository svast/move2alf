package eu.xenit.move2alf.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import eu.xenit.move2alf.core.enums.ERole;

public class Util {
	private static final Logger logger = LoggerFactory.getLogger(Util.class);

	public static String convertToMd5(String str) {
		try {
			byte[] bytes = str.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] md5 = md.digest(bytes);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < md5.length; i++) {
				sb.append(Integer.toString((md5[i] & 0xff) + 0x100, 16)
						.substring(1));
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

	public static void authenticateAsSystem() {
		List<GrantedAuthority> gas = new ArrayList<GrantedAuthority>();
		gas.add(new GrantedAuthorityImpl(ERole.CONSUMER.toString()));
		gas.add(new GrantedAuthorityImpl(ERole.SCHEDULE_ADMIN.toString()));
		gas.add(new GrantedAuthorityImpl(ERole.JOB_ADMIN.toString()));
		gas.add(new GrantedAuthorityImpl(ERole.SYSTEM_ADMIN.toString()));
		UserDetails ud = new User("System", "", true, true, true, true, gas);

		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
				ud, "", ud.getAuthorities());
		auth.setDetails(ud);
		
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
}
