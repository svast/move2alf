package eu.xenit.move2alf.common;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.enums.ERole;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Util.class);
    public static String fileSeparator = File.separator; // DONT MODIFY, ONLY FOR TESTING

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
        gas.add(new SimpleGrantedAuthority( ERole.ROLE_CONSUMER.toString()));
        gas.add(new SimpleGrantedAuthority(ERole.ROLE_SCHEDULE_ADMIN.toString()));
        gas.add(new SimpleGrantedAuthority(ERole.ROLE_JOB_ADMIN.toString()));
        gas.add(new SimpleGrantedAuthority(ERole.ROLE_SYSTEM_ADMIN.toString()));
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
        relativePath = relativePath.substring(normalizePath(inputPath).length());
        return relativePath;
    }

    public static String normalizePath(final String in) {
        String out = in;
        if (in.endsWith("/")) {
            out = in.substring(0, in.length() - 1);
        }
        return out.replace("\\", "/");
    }

    public static File moveFile(final String dest, final File file) {
        logger.debug("Moving file " + file + " from " + file.getParent() + " to " + dest);
        String fullDestinationPath = normalizePath(dest);
        File moveFolder = new File(fullDestinationPath);

        // check if full path exists, otherwise create path
        boolean destinationPathExists = true;
        if(!moveFolder.exists()) {
            destinationPathExists = moveFolder.mkdirs();
        }

        // If path exists move document
        if (destinationPathExists) {
            String newFileName = fullDestinationPath + fileSeparator + file.getName();
            File movedFile = new File(newFileName);
            deleteIfExists(movedFile);
            try {
                FileUtils.moveFile(file, movedFile);
            } catch (IOException e) {
                logger.error("Could not move document "
                        + file.getAbsolutePath(), e);
                return null;
            }
            return movedFile;
        }
        else {
            logger.warn("Resource path could not be made for document "
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
                final String message = t.getMessage();
                if (message != null) {
                    errorMessages.add(message);
                } else {
                    errorMessages.add("null");
                }
            }  else {
                errorMessages.add(t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        return Joiner.on(" < ").join(errorMessages);
    }

    public static int countLines(File file) throws IOException {
        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));
        lineNumberReader.skip(Long.MAX_VALUE);
        return lineNumberReader.getLineNumber();

    }

    public static void executeCommand(String command) {
        if (command != null && !"".equals(command)) {
            logger.debug("Executing command [including arguments " + command);
            List<String> cmdList = new ArrayList<String>();
            for (String c:command.split(" "))
            {
                cmdList.add(c);
            }
            final ProcessBuilder pb = new ProcessBuilder(cmdList);
            pb.redirectErrorStream(true);

            Process process;
            try {
                process = pb.start();
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }

            try {
                final InputStream is = process.getInputStream();
                final InputStreamReader isr = new InputStreamReader(is);
                final BufferedReader br = new BufferedReader(isr);
                String line;

                while ((line = br.readLine()) != null) {
                    logger.debug(line);

                }
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                process.waitFor();
            } catch (final InterruptedException ie) {
                logger.error("Problem running command");
            }

            logger.info("Command finished");
        }
    }

    public static String createRelativePath(String path, String file, String directory) {
        String newPath = path;
        if(!(newPath.endsWith(fileSeparator)))
            newPath = newPath.concat(fileSeparator);
        if(directory.endsWith(fileSeparator))
            directory = directory.substring(0,directory.length()-1);

        String separatorRegex = String.format("\\%s", fileSeparator);

        String[] fileParts = file.split(separatorRegex);
        String[] directoryParts = directory.split(separatorRegex);
        for(int i=(directoryParts.length); i< (fileParts.length - 1) ; i++) {
            newPath = newPath.concat(fileParts[i]).concat(fileSeparator);
        }
        return newPath;
    }
}
