package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.Map;

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
		
		String basePath = configuredAction.getParameter("path");
		if (!basePath.endsWith("/")) {
			basePath = basePath + "/";
		}

		String relativePath = (String) parameterMap.get("relativePath");
		relativePath = relativePath.replace("\\", "/");

		if (relativePath.startsWith("/")) {
			relativePath = relativePath.substring(1);
		}

		// add "cm:" in front of each path component
		String remotePath = basePath + relativePath;
		
		String name = ((File) parameterMap.get("file")).getName();
		try {
			if (sink.exists(sinkConfig, remotePath, name)) {
				parameterMap.put("status", "ok");
			} else {
				parameterMap.put("status", "failed");
				parameterMap.put("errormessage", "Document not found");
			}
		} catch (Move2AlfException e) {
			parameterMap.put("status", "failed");
			parameterMap.put("errormessage", e.getMessage());
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
