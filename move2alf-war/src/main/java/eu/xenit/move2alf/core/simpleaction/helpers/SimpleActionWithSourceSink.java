package eu.xenit.move2alf.core.simpleaction.helpers;

import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.simpleaction.SimpleAction;
import eu.xenit.move2alf.core.sourcesink.SourceSink;

public abstract class SimpleActionWithSourceSink extends SimpleAction {

	private SourceSink sink;
	private ConfiguredSourceSink sinkConfig;

	public SimpleActionWithSourceSink(SourceSink sink, ConfiguredSourceSink sinkConfig) {
		this.sink = sink;
		this.sinkConfig = sinkConfig;
	}

	public SourceSink getSink() {
		return sink;
	}

	public ConfiguredSourceSink getSinkConfig() {
		return sinkConfig;
	}

}