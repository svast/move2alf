package eu.xenit.move2alf.core.simpleaction;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;

public abstract class SimpleAction {
	public abstract List<FileInfo> execute(final FileInfo parameterMap,
			final ActionConfig config, final Map<String, Serializable> state);

	public List<FileInfo> initializeState(final ActionConfig config, final Map<String, Serializable> state) {
		return null;
	}

	public List<FileInfo> cleanupState(final ActionConfig config, final Map<String, Serializable> state) {
		return null;
	}
}
