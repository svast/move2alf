package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;

public class SAMoveBeforeProcessing extends SimpleAction {

	public static final String PARAM_MOVE_BEFORE_PROCESSING_PATH = "moveBeforeProcessingPath";

	@Override
	public List<FileInfo> execute(
			final FileInfo parameterMap,
			final ActionConfig config, final Map<String, Serializable> state) {
		List<FileInfo> output = new ArrayList<FileInfo>();
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
