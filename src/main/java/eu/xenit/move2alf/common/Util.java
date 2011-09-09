package eu.xenit.move2alf.common;

import java.io.File;
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

	public static String convertToMd5(final String str) {
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

	public static String relativePath(final String inputPath, final File file) {
		logger.debug("RELATIVE PATH: " + inputPath + " - " + file.getAbsolutePath());
		String filePath = normalizePath(file.getAbsolutePath());
		String relativePath = filePath;
		if (filePath.contains("/")) {
			relativePath = filePath.substring(0, filePath.lastIndexOf("/"));
		}
		relativePath = relativePath.substring(inputPath.length());
		return relativePath;
	}

	public static String normalizePath(final String in) {
		String out = in;
		if (in.endsWith("/")) {
			out = in.substring(0, in.length() - 1);
		}
		return out.replace("\\", "/");
	}

	// moveFile method in MoveDocumentsAction
	public static File moveFile(final String src, final String dest, final File file) {		
		String nSrc = normalizePath(src);
		String nDest = normalizePath(dest);
		String fullPath = normalizePath(file.getAbsolutePath());
		String relativePath = fullPath.substring(nSrc.length(), fullPath.lastIndexOf("/"));
		String fullDestinationPath = nDest + relativePath;
		File moveFolder = new File(fullDestinationPath);
	
		// check if full path exists, otherwise create path
		boolean destinationPathExists = true;
		if(!moveFolder.exists()) {
			destinationPathExists = moveFolder.mkdirs();
		}
	
		// If path exists move document
		if (destinationPathExists) {
			String newFileName = fullDestinationPath + "/" + file.getName();
			File newFile = new File(newFileName);
			deleteIfExists(newFile);
			if (file.renameTo(new File(newFileName))) {
				File movedFile = new File(newFileName);
				logger.info("Moved file to " + movedFile.getAbsolutePath());
				return movedFile;
			} else {
				logger.warn("Could not move document "
						+ file.getAbsolutePath());
				return null;
			}
		} else {
			logger.warn("Destination path could not be made for document "
					+ file.getAbsolutePath());
			return null;
		}
	}

	public static void deleteIfExists(File file) {
		if (file.exists()) {
			try {
				file.delete();
			} catch (Exception e) {
			}
		}
	}
}
