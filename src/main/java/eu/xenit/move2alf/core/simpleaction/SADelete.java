package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;

public class SADelete extends SimpleActionWithSourceSink {

	public static final String PARAM_PATH = "path";

	public SADelete(SourceSink sink, ConfiguredSourceSink sinkConfig) {
		super(sink, sinkConfig);
	}

	@Override
	public List<Map<String, Object>> execute(Map<String, Object> parameterMap,
			Map<String, String> config) {
		List<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
		Map<String, Object> newParameterMap = new HashMap<String, Object>(
				parameterMap);

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
			sink.delete(sinkConfig, remotePath, name);
			newParameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_OK);
		} catch (Move2AlfException e) {
			newParameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_FAILED);
			newParameterMap.put(Parameters.PARAM_ERROR_MESSAGE, e.getMessage());
		}

		output.add(newParameterMap);
		return output;
	}

}
