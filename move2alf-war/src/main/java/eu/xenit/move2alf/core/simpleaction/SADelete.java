package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionWithSourceSink;
import eu.xenit.move2alf.core.sourcesink.DeleteOption;
import eu.xenit.move2alf.core.sourcesink.SourceSink;

public class SADelete extends SimpleActionWithSourceSink {

	public static final String PARAM_PATH = "path";
	public static final String PARAM_DELETEOPTION = "deleteOption";

	public SADelete(SourceSink sink, ConfiguredSourceSink sinkConfig) {
		super(sink, sinkConfig);
	}

	@Override
	public String getDescription() {
		return "Deleting documents in Alfresco";
	}

	@Override
	public List<FileInfo> execute(FileInfo parameterMap, ActionConfig config, final Map<String, Serializable> state) {
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
		if(remotePath.length() > 0){
			remotePath = remotePath.substring(0, remotePath.length() - 1);
		}

		String name = ((File) newParameterMap.get(Parameters.PARAM_FILE))
				.getName();

		sink.delete(sinkConfig, remotePath, name, DeleteOption.valueOf(config.get(PARAM_DELETEOPTION)));

		output.add(newParameterMap);
		return output;
	}

}
