package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionWithSourceSink;
import eu.xenit.move2alf.core.sourcesink.SourceSink;

public class SAList extends SimpleActionWithSourceSink {

	public static final String PARAM_PATH = "path";

	public SAList(SourceSink sink, ConfiguredSourceSink sinkConfig) {
		super(sink, sinkConfig);
	}

	@Override
	public String getDescription() {
		return "Listing documents in Alfresco";
	}

	@Override
	public List<FileInfo> execute(final FileInfo parameterMap,
			final ActionConfig config, final Map<String, Serializable> state) {
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

		if (!sink.exists(sinkConfig, remotePath, name)) {
			throw new Move2AlfException("Document not found");
		}

		output.add(newParameterMap);
		return output;
	}

	@Override
	public List<FileInfo> initializeState(final ActionConfig config, final Map<String, Serializable> state) {
		getSink().clearCaches(getSinkConfig());
		return null;
	}
}
