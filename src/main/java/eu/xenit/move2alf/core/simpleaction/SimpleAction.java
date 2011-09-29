package eu.xenit.move2alf.core.simpleaction;

import java.util.List;

import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;

public abstract class SimpleAction {
	public abstract List<FileInfo> execute(
			final FileInfo parameterMap,
			final ActionConfig config);
}
