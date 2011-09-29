package eu.xenit.move2alf.core.simpleaction.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.simpleaction.SimpleAction;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;

public abstract class SimpleActionSingleResult extends SimpleAction {

	@Override
	public final List<FileInfo> execute(
			final FileInfo parameterMap,
			final ActionConfig config) {
		List<FileInfo> output = new ArrayList<FileInfo>();
		output.add(executeSingleResult(parameterMap, config));
		return output;
	}

	public abstract FileInfo executeSingleResult(
			final FileInfo parameterMap,
			final ActionConfig config);
}
