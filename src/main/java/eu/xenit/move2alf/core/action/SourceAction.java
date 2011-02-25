package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfiguredObject;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;

public class SourceAction extends Action {

	public static final String PARAM_PATH = "path";
	public static final String PARAM_RECURSIVE = "recursive";

	@Override
	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// TODO: filters; in separate action? in sourcesink?
		String path = configuredAction.getParameter(PARAM_PATH);
		boolean recursive = "true".equals(configuredAction
				.getParameter(PARAM_RECURSIVE));

		ConfiguredSourceSink sourceConfig = (ConfiguredSourceSink) configuredAction
				.getConfiguredSourceSinkSet().toArray()[0];
		SourceSink source = getSourceSinkFactory().getObject(
				sourceConfig.getClassName());
		List<File> files = source.list(sourceConfig, path, recursive);
		ConfiguredAction nextAction = configuredAction.getAppliedConfiguredActionOnSuccess();
		parameterMap.put("threadpool", getSourceSinkFactory().getThreadPool(sourceConfig));
		if (nextAction != null) {
			for (File file : files) {
				Map<String, Object> newParameterMap = new HashMap<String, Object>();
				newParameterMap.putAll(parameterMap);
				newParameterMap.put("file", file);
				getActionFactory().execute(nextAction, newParameterMap);
			}
		}
	}

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// empty; SourceAction overrides the execute method to execute the next
		// action for each file separately
	}

}
