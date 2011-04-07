package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;

public class SourceAction extends Action {
	
	private static final Logger logger = LoggerFactory
	.getLogger(SourceAction.class);

	@Override
	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// TODO: filters; in separate action? in sourcesink?
		String path = configuredAction.getParameter(Parameters.PARAM_PATH);
		boolean recursive = "true".equals(configuredAction
				.getParameter(Parameters.PARAM_RECURSIVE));

		ConfiguredSourceSink sourceConfig = (ConfiguredSourceSink) configuredAction
				.getConfiguredSourceSinkSet().toArray()[0];
		SourceSink source = getSourceSinkFactory().getObject(
				sourceConfig.getClassName());
		ConfiguredAction nextAction = configuredAction.getAppliedConfiguredActionOnSuccess();

		List<File> files = source.list(sourceConfig, path, recursive);
		String moveNotLoaded = configuredAction.getParameter(Parameters.PARAM_MOVE_NOT_LOADED);
		String failedPath = configuredAction.getParameter(Parameters.PARAM_MOVE_NOT_LOADED_PATH);
		if("true".equals(moveNotLoaded)) {
			files.addAll(source.list(sourceConfig, failedPath, recursive));
		}
		CountDownLatch counter = new CountDownLatch(files.size());
		parameterMap.put(Parameters.PARAM_COUNTER, counter);
		readFiles(files, parameterMap, recursive, configuredAction, sourceConfig, source,
				nextAction);
		try {
			counter.await();
		} catch (InterruptedException e) {
			logger.warn("Execution interrupted");
		}
	}

	private void readFiles(List<File> files, Map<String, Object> parameterMap,
			boolean recursive, ConfiguredAction action, ConfiguredSourceSink sourceConfig,
			SourceSink source, ConfiguredAction nextAction) {
		parameterMap.put(Parameters.PARAM_THREADPOOL, getSourceSinkFactory().getThreadPool(sourceConfig));
		if (nextAction != null) {
			for (File file : files) {
				Map<String, Object> newParameterMap = new HashMap<String, Object>();
				newParameterMap.putAll(parameterMap);
				newParameterMap.put(Parameters.PARAM_FILE, file);
				
				// add to list of files to transform
				List<File> transformFiles = new ArrayList<File>();
				transformFiles.add(file);
				newParameterMap.put(Parameters.PARAM_TRANSFORM_FILE_LIST, transformFiles);
				
				String relativePath = file.getParent();
				relativePath = relativePath.replace("\\", "/");
				String path = action.getParameter(Parameters.PARAM_PATH);
				path = path.replace("\\", "/");
				String pathFailed = action.getParameter(Parameters.PARAM_MOVE_NOT_LOADED_PATH);
				pathFailed = pathFailed.replace("\\", "/");
				if (relativePath.startsWith(path)) {
					relativePath = relativePath.substring(path.length());
				} else if (relativePath.startsWith(pathFailed)) {
					relativePath = relativePath.substring(pathFailed.length());
				}
				newParameterMap.put(Parameters.PARAM_RELATIVE_PATH, relativePath);
				getJobService().executeAction((Integer) parameterMap.get(Parameters.PARAM_CYCLE), nextAction, newParameterMap);
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
