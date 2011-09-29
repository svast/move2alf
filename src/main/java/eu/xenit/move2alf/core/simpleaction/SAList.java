package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionWithSourceSink;

public class SAList extends SimpleActionWithSourceSink {

	public static final String PARAM_PATH = "path";
	
	public SAList(SourceSink sink, ConfiguredSourceSink sinkConfig) {
		super(sink, sinkConfig);
	}

	@Override
	public List<FileInfo> execute(final FileInfo parameterMap,
			final ActionConfig config) {
		List<FileInfo> output = new ArrayList<FileInfo>();
		FileInfo newParameterMap = new FileInfo();
		newParameterMap.putAll(parameterMap);
		
		ConfiguredSourceSink sinkConfig = getSinkConfig();
		SourceSink sink = getSink();

		String basePath = config.get(PARAM_PATH);
		if (!basePath.endsWith("/")) {
			basePath = basePath + "/";
		}

		if (!basePath.startsWith("/")) {
			basePath = "/" + basePath;
		}

		String relativePath = (String) newParameterMap
				.get(Parameters.PARAM_RELATIVE_PATH);
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

		String name = ((File) newParameterMap.get(Parameters.PARAM_FILE))
				.getName();
		try {
			if (sink.exists(sinkConfig, remotePath, name)) {
				newParameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_OK);
			} else {
				newParameterMap.put(Parameters.PARAM_STATUS,
						Parameters.VALUE_FAILED);
				newParameterMap.put(Parameters.PARAM_ERROR_MESSAGE,
						"Document not found");
			}
		} catch (Move2AlfException e) {
			newParameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_FAILED);
			newParameterMap.put(Parameters.PARAM_ERROR_MESSAGE, e.getMessage());
		}
		
		output.add(newParameterMap);
		return output;
	}
}
