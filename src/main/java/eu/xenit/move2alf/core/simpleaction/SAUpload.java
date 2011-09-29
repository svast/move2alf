package eu.xenit.move2alf.core.simpleaction;

import java.util.ArrayList;
import java.util.List;

import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionWithSourceSink;

public class SAUpload extends SimpleActionWithSourceSink {

	public static final String PARAM_PATH = "path";
	/**
	 * SourceSink.MODE_SKIP or SourceSink.MODE_SKIP_AND_LOG or
	 * SourceSink.MODE_OVERWRITE
	 */
	public static final String PARAM_DOCUMENT_EXISTS = "documentExists";

	public SAUpload(SourceSink sink, ConfiguredSourceSink sinkConfig) {
		super(sink, sinkConfig);
	}

	@Override
	public List<FileInfo> execute(FileInfo parameterMap,
			ActionConfig config) {
		List<FileInfo> output = new ArrayList<FileInfo>();
		FileInfo newParameterMap = new FileInfo();
		newParameterMap.putAll(parameterMap);
		getSink().send(getSinkConfig(), newParameterMap, config.get(PARAM_PATH),
				config.get(PARAM_DOCUMENT_EXISTS));
		output.add(newParameterMap);
		return output;
	}

}
