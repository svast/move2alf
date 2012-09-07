package eu.xenit.move2alf.core.action.metadata;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.ProcessErrorException;
import eu.xenit.move2alf.common.StreamGobbler;
import eu.xenit.move2alf.common.Util;

public class FilesystemMetadataLoader implements MetadataLoader {

	private final static Logger logger = LoggerFactory.getLogger(FilesystemMetadataLoader.class);

	final static String fsPropCreationDate = "created";
	final static String fsPropCreator = "creator";
	final static String fsPropModifyDate = "modified";

	public boolean hasMetadata(File file) {
		return true;
	}

	public Map<String, String> loadMetadata(File file) {
		Map<String, String> fileSystemPropertyMap = new HashMap<String, String>();
		String os = System.getProperty("os.name");
		logger.debug("OS {}", os);
		boolean windows = os.contains("Windows");
		try {
			if (windows) {
				// the program extractWindowsFileSystemProperties outputs:
				// line 0: owner
				// line 1: creationDate
				// line 2: modifyDate
				// this program was created by Ian
				String filePath = file.getAbsolutePath();
				
				URL programURL = this.getClass().getClassLoader().getResource("extractWindowsFileSystemProperties.exe");
				URI programURI = programURL.toURI();
				String programPath = new File(programURI).getAbsolutePath();
				
				String[] commandArray = { programPath, filePath };
				
				java.lang.Process p = Runtime.getRuntime().exec(commandArray);
				StringBuffer errorBuffer = new StringBuffer();
				try {
					String output = StreamGobbler.executeCommand(p);
					String[] lines = output.split("\n");
					logger.debug("ExtractWindowsFileSystemProperties execution ok");
					if (lines.length > 0) {
						fileSystemPropertyMap.put(fsPropCreator, lines[0]);
					}
					if (lines.length > 1) {
						fileSystemPropertyMap.put(fsPropCreationDate, lines[1]);
					}
					if (lines.length > 2) {
						fileSystemPropertyMap.put(fsPropModifyDate, lines[2]);
					}

				} catch (ProcessErrorException e) {
					logger.error("Error while running ExtractWindowsFileSystemProperties {}", errorBuffer.toString());
				}

			} else {
				// UNIX: get creator with ls command
				String[] commandArray = { "/bin/ls", "-l", file.getAbsolutePath() };

				java.lang.Process p = Runtime.getRuntime().exec(commandArray);
				try {
					String output = StreamGobbler.executeCommand(p);
					String[] lines = output.split("\n");
					if (lines.length > 0) {
						String[] lineSplit = lines[0].split(" ");
						fileSystemPropertyMap.put(fsPropCreator, lineSplit[2]);
					}
				} catch (ProcessErrorException e) {
					logger.error("Error while running /bin/ls {}", e.getMessage());
				}
				// Getting the date
				// On linux with filesystems ETX1,2,3 there is no creation date
				// For this case we set creation date to modify date
				// For ETX4 a creation date will be present
				Date dateLastModified = new Date(file.lastModified());
				String lastModifyDate = Util.ISO8601format(dateLastModified);
				fileSystemPropertyMap.put(fsPropCreationDate, lastModifyDate);
				fileSystemPropertyMap.put(fsPropModifyDate, lastModifyDate);
			}
		} catch (IOException e) {
			logger.error("", e);
		} catch (URISyntaxException e) {
			logger.error("", e);
		}

		return fileSystemPropertyMap;
	}
}