package eu.xenit.move2alf.core.action.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.exceptions.Move2AlfException;


/**
 * XmlPropertiesFileMetadataLoader loads metadata from a java properties file in XML format.
 * 
 * The properties file must have exactly the same name and extension as the file
 * for whom it is storing metadata, but with the suffix ".metadata.properties.xml".
 */
public class XmlPropertiesFileMetadataLoader implements MetadataLoader {

	private final static Logger logger = LoggerFactory.getLogger(XmlPropertiesFileMetadataLoader.class);

	private final static String XML_METADATA_EXTENSION = "metadata.properties.xml";

	public boolean hasMetadata(File file) {
		return !file.getName().endsWith(XML_METADATA_EXTENSION);
	}

	public Map<String, String> loadMetadata(String dirname, File file) {
		String filename = file.getName();
		
		Map<String, String> metadata = null;
		if ( hasMetadata(file) ) {
			File metadataFile = new File(dirname, filename + "." + XML_METADATA_EXTENSION);
	
			Properties properties = new Properties();
			try {
				FileInputStream fileInputStream = new FileInputStream(metadataFile);
				try {
					properties.loadFromXML(fileInputStream);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				} finally {
					try {
						fileInputStream.close();
					} catch (IOException e) {
						//TODO
					}
				}
				properties.remove("type");
				properties.remove("aspects");
	
				@SuppressWarnings({ "unchecked", "rawtypes" })
				Map<String, String> propertyMap = new HashMap<String, String>((Map) properties);
				metadata = propertyMap;
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage(), e);
				throw new Move2AlfException(e.getMessage(), e);
			}
		} else {
			metadata = new HashMap<String, String>();
		}
		return metadata;
	}
}