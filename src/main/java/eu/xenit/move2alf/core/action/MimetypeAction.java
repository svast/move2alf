package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class MimetypeAction extends Action {

	private static final Logger logger = LoggerFactory
			.getLogger(MimetypeAction.class);

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		File file = (File) parameterMap.get(Parameters.PARAM_FILE);
		parameterMap.put(Parameters.PARAM_MIMETYPE, determineMimeType(file.getName()));
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
		} else {
			logger.warn("Mimetype is unknown");
			mimeType = "unknown";
		}
		return mimeType;
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_DEFAULT;
	}

	@Override
	public String getDescription() {
		return "Set mimetype for file";
	}

	@Override
	public String getName() {
		return "Mimetype";
	}

}
