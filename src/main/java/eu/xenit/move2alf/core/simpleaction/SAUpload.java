package eu.xenit.move2alf.core.simpleaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;

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
	public List<Map<String, Object>> execute(Map<String, Object> parameterMap,
			Map<String, String> config) {
		List<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
		Map<String, Object> newParameterMap = new HashMap<String, Object>(parameterMap);
		getSink().send(getSinkConfig(), newParameterMap, config.get(PARAM_PATH),
				config.get(PARAM_DOCUMENT_EXISTS));
		output.add(newParameterMap);
		return output;
	}

}
