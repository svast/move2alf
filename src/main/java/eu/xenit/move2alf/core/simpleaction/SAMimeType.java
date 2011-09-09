package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;

public class SAMimeType extends SimpleActionSingleResult {

	private static final Logger logger = LoggerFactory.getLogger(SAMimeType.class);
	
	@Override
	public Map<String, Object> executeSingleResult(
			final Map<String, Object> parameterMap, final Map<String, String> config) {
		Map<String, Object> output = new HashMap<String, Object>(parameterMap);
		File file = (File) parameterMap.get(Parameters.PARAM_FILE);
		output.put(Parameters.PARAM_MIMETYPE, determineMimeType(file.getName()));
		return output;
	}

	public static String determineMimeType(String fileName) {
		String mimeType = null;
		if (fileName.toLowerCase().endsWith("pdf")) {
			mimeType = "application/pdf";
		} else if (fileName.toLowerCase().endsWith("fdf")) {
			mimeType = "application/pdf";
		} else if (fileName.toLowerCase().endsWith("tif")) {
			mimeType = "image/tif";
		} else if (fileName.toLowerCase().endsWith("xls")) {
			mimeType = "application/vnd.ms-excel";
		} else if (fileName.toLowerCase().endsWith("doc")) {
			mimeType = "application/msword";
		} else if (fileName.toLowerCase().endsWith("ppt")) {
			mimeType = "application/vnd.ms-powerpoint";
		} else if (fileName.toLowerCase().endsWith("txt")) {
			mimeType = "text/plain";
		} else if (fileName.toLowerCase().endsWith("html")) {
			mimeType = "text/html";
		} else if (fileName.toLowerCase().endsWith("xml")) {
			mimeType = "application/xml";
		} else if (fileName.toLowerCase().endsWith("svg")) {
			mimeType = "image/svg+xml";
		} else if (fileName.toLowerCase().endsWith("docx")) {
			mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		} else if (fileName.toLowerCase().endsWith("xslx")) {
			mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		} else if (fileName.toLowerCase().endsWith("pptx")) {
			mimeType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
		} else {
			logger.warn("Mimetype is unknown");
			mimeType = "unknown";
		}
		return mimeType;
	}
}