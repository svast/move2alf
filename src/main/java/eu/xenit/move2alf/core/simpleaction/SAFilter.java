package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;

public class SAFilter extends SimpleAction {

	public static final String PARAM_EXTENSION = "extension";

	private static final Logger logger = LoggerFactory
			.getLogger(SAFilter.class);

	@Override
	public List<Map<String, Object>> execute(
			final Map<String, Object> parameterMap,
			final Map<String, String> config) {
		String extension =  config.get(PARAM_EXTENSION);
		File file = (File) parameterMap.get(Parameters.PARAM_FILE);
		
		if (extension != null && extension.startsWith("*")) {
			extension = extension.substring(1);
		}

		List<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
		if ("".equals(extension)
				|| extension == null
				|| file.getPath().toLowerCase().endsWith(
						extension.toLowerCase())) {
			output.add(parameterMap);
		} else {
			logger.debug("File " + file.getName()
					+ " does not have the correct extension - skip.");
		}
		return output;
	}
}
