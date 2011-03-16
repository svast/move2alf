package eu.xenit.move2alf.core;

import java.io.File;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;

public abstract class SourceSink extends ConfigurableObject {
	public abstract void send(ConfiguredSourceSink configuredSourceSink,
			Map<String, Object> parameterMap);

	public abstract List<File> list(ConfiguredSourceSink sourceConfig, String path,
			boolean recursive);

}
