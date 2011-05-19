package eu.xenit.move2alf.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
	private static final Logger logger = LoggerFactory.getLogger(Config.class);

	public static final String CONFIG_FILE = "/move2alf.properties";

	public static String get(String key) {
		InputStream configIS = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(CONFIG_FILE);
		Properties config = new Properties();
		try {
			config.load(configIS);
		} catch (IOException e) {
			logger.error("Could not read config file: " + e);
			return null;
		} catch (NullPointerException e) {
			logger.error("Could not read config file: " + e);
			return null;
		}
		return config.getProperty(key);
	}
}
