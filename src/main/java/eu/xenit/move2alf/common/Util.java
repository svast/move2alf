package eu.xenit.move2alf.common;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
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
	
    public static String ISO8601format(Date isoDate)
    {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(isoDate);
        
        StringBuilder formatted = new StringBuilder(28);
        padInt(formatted, calendar.get(Calendar.YEAR), 4);
        formatted.append('-');
        padInt(formatted, calendar.get(Calendar.MONTH) + 1, 2);
        formatted.append('-');
        padInt(formatted, calendar.get(Calendar.DAY_OF_MONTH), 2);
        formatted.append('T');
        padInt(formatted, calendar.get(Calendar.HOUR_OF_DAY), 2);
        formatted.append(':');
        padInt(formatted, calendar.get(Calendar.MINUTE), 2);
        formatted.append(':');
        padInt(formatted, calendar.get(Calendar.SECOND), 2);
        formatted.append('.');
        padInt(formatted, calendar.get(Calendar.MILLISECOND), 3);
        
        TimeZone tz = calendar.getTimeZone();
        int offset = tz.getOffset(calendar.getTimeInMillis());
        if (offset != 0)
        {
            int hours = Math.abs((offset / (60 * 1000)) / 60);
            int minutes = Math.abs((offset / (60 * 1000)) % 60);
            formatted.append(offset < 0 ? '-' : '+');
            padInt(formatted, hours, 2);
            formatted.append(':');
            padInt(formatted, minutes, 2);
        } 
        else
        {
            formatted.append('Z');
        }
        
        return formatted.toString();
    }
    
    /*
     * Based on "Java Concurrency in Practice"
     * 
     * Used to convert Throwable to RuntimeException. This is useful when
     * rethrowing the cause of an Exception (for example the ExecutionException
     * when using java.util.concurrent) since getCause returns a Throwable
     * which is not practical to use.
     */
    public static RuntimeException launderThrowable(Throwable t) {
    	if (t instanceof RuntimeException) {
    		return (RuntimeException) t;
    	} else if (t instanceof Error) {
    		throw (Error) t;
    	} else {
    		throw new IllegalStateException("Not unchecked", t);
    	}
    }


    private static void padInt(StringBuilder buffer, int value, int length)
    {
        String strValue = Integer.toString(value);
        for (int i = length - strValue.length(); i > 0; i--)
        {
            buffer.append('0');
        }
        buffer.append(strValue);
    }

	public static String formatDuration(final long seconds) {
		return String.format("%d:%02d:%02d",
				seconds / 3600, (seconds % 3600) / 60,
				(seconds % 60));
	}

	public static String getFullErrorMessage(final Exception e) {
		List<Throwable> causalChain = Throwables.getCausalChain(e);
		List<String> errorMessages = new ArrayList<String>();
		for(Throwable t : causalChain) {
			if (t instanceof Move2AlfException) {
				errorMessages.add(t.getMessage());
			}  else {
				errorMessages.add(t.getClass().getSimpleName() + ": " + t.getMessage());
			}
		}
		return Joiner.on(" < ").join(errorMessages);
	}
}
