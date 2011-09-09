package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;

public class SAMoveBeforeProcessing extends SimpleAction {

	private static final Logger logger = LoggerFactory
			.getLogger(SAMoveBeforeProcessing.class);

	public static final String PARAM_MOVE_BEFORE_PROCESSING_PATH = "moveBeforeProcessingPath";

	@Override
	public List<Map<String, Object>> execute(
			final Map<String, Object> parameterMap,
			final Map<String, String> config) {
		List<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
		String destination = config.get(PARAM_MOVE_BEFORE_PROCESSING_PATH);
		String source = (String) parameterMap.get(Parameters.PARAM_INPUT_PATH);
		File file = (File) parameterMap.get(Parameters.PARAM_FILE);
		File newFile = Util.moveFile(source, destination, file);
		if (newFile != null) {
			parameterMap.put(Parameters.PARAM_FILE, newFile);
		} else {
			throw new Move2AlfException("Could not move file "
					+ file.getAbsolutePath() + " to " + destination);
		}
		output.add(parameterMap);
		return output;
	}
}
