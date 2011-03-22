package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;

public class SourceAction extends Action {
	
	private static final Logger logger = LoggerFactory
	.getLogger(SourceAction.class);

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
		logger.debug("Reading files from " + path);
		List<File> files = source.list(sourceConfig, path, recursive);
		ConfiguredAction nextAction = configuredAction.getAppliedConfiguredActionOnSuccess();
		parameterMap.put("threadpool", getSourceSinkFactory().getThreadPool(sourceConfig));
		//parameterMap.put("path", path);
		if (nextAction != null) {
			for (File file : files) {
				Map<String, Object> newParameterMap = new HashMap<String, Object>();
				newParameterMap.putAll(parameterMap);
				newParameterMap.put("file", file);
				newParameterMap.put("relativePath", file.getParent().substring(path.length()));
				getJobService().executeAction((Integer) parameterMap.get("cycle"), nextAction, newParameterMap);
			}
		}
	}

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// empty; SourceAction overrides the execute method to execute the next
		// action for each file separately
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_DEFAULT;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Source";
	}

}
