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

public class SAExistenceCheck extends SimpleActionWithSourceSink {

	public SAExistenceCheck(SourceSink sink, ConfiguredSourceSink sinkConfig) {
		super(sink, sinkConfig);
	}

	@Override
	public String getDescription() {
		return "Check if filename exists in Alfresco";
	}

	@Override
	public List<FileInfo> execute(final FileInfo parameterMap,
			final ActionConfig config, final Map<String, Serializable> state) {
		List<FileInfo> output = new ArrayList<FileInfo>();
		FileInfo newParameterMap = new FileInfo();
		newParameterMap.putAll(parameterMap);

		ConfiguredSourceSink sinkConfig = getSinkConfig();
		SourceSink sink = getSink();

		String name = ((File) newParameterMap.get(Parameters.PARAM_FILE))
				.getName();

		if (!sink.fileNameExists(sinkConfig, name)) {
			throw new Move2AlfException("Document not found");
		}

		output.add(newParameterMap);
		return output;
	}

}
