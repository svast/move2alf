package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.Map;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;

public class ListAction extends Action {

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		ConfiguredSourceSink sinkConfig = (ConfiguredSourceSink) configuredAction
				.getConfiguredSourceSinkSet().toArray()[0];
		SourceSink sink = getSourceSinkFactory().getObject(
				sinkConfig.getClassName());
		
		String basePath = configuredAction.getParameter(SourceAction.PARAM_PATH);
		if (!basePath.endsWith("/")) {
			basePath = basePath + "/";
		}
		
		if(!basePath.startsWith("/")) {
			basePath = "/" + basePath;
		}

		String relativePath = (String) parameterMap.get(Parameters.PARAM_RELATIVE_PATH);
		relativePath = relativePath.replace("\\", "/");

		if (relativePath.startsWith("/")) {
			relativePath = relativePath.substring(1);
		}

		// add "cm:" in front of each path component
		String remotePath = basePath + relativePath;
		String[] components = remotePath.split("/");
		remotePath = "";
		for (String component : components) {
			if ("".equals(component)) {
				remotePath += "/";
			} else if (component.startsWith("cm:")) {
				remotePath += component + "/";
			} else {
				remotePath += "cm:" + component + "/";
			}
		}
		remotePath = remotePath.substring(0, remotePath.length() - 1);
		
		String name = ((File) parameterMap.get(Parameters.PARAM_FILE)).getName();
		try {
			if (sink.exists(sinkConfig, remotePath, name)) {
				parameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_OK);
			} else {
				parameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_FAILED);
				parameterMap.put(Parameters.PARAM_ERROR_MESSAGE, "Document not found");
			}
		} catch (Move2AlfException e) {
			parameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_FAILED);
			parameterMap.put(Parameters.PARAM_ERROR_MESSAGE, e.getMessage());
		}
	}

	@Override
	public String getCategory() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

}
