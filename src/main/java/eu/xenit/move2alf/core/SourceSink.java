package eu.xenit.move2alf.core;

import java.io.File;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;

public abstract class SourceSink extends ConfigurableObject {
	public static final String MODE_SKIP = "Skip";
	public static final String MODE_SKIP_AND_LOG = "SkipAndLog";
	public static final String MODE_OVERWRITE = "Overwrite";

	public abstract void send(ConfiguredSourceSink configuredSourceSink,
			Map<String, Object> parameterMap, String docExistsMode);

	public abstract List<File> list(ConfiguredSourceSink sourceConfig,
			String path, boolean recursive);

	public abstract boolean exists(ConfiguredSourceSink sinkConfig,
			String relativePath, String name);

}
