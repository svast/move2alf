package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;

public class SAFilter extends SimpleAction {

	public static final String PARAM_EXTENSION = "extension";

	private static final Logger logger = LoggerFactory
			.getLogger(SAFilter.class);

	@Override
	public List<FileInfo> execute(
			final FileInfo parameterMap,
			final ActionConfig config) {
		String extension =  config.get(PARAM_EXTENSION);
		File file = (File) parameterMap.get(Parameters.PARAM_FILE);
		
		if (extension != null && extension.startsWith("*")) {
			extension = extension.substring(1);
		}

		List<FileInfo> output = new ArrayList<FileInfo>();
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
