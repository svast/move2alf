package eu.xenit.move2alf.core.simpleaction.helpers;

import eu.xenit.move2alf.core.action.Move2AlfAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.simpleaction.SimpleAction;
import eu.xenit.move2alf.core.sourcesink.SourceSink;
import eu.xenit.move2alf.pipeline.AbstractMessage;

public abstract class SimpleActionWithSourceSink<T extends AbstractMessage> extends Move2AlfAction<T> {

    public static final String PARAM_SINK = "sink";
	private SourceSink sink;
    public void setSink(SourceSink sink){
        this.sink = sink;
    }

    public static final String PARAM_SINKCONFIG = "sinkConfig";
	private ConfiguredSourceSink sinkConfig;
    public void setSinkConfig(ConfiguredSourceSink sinkConfig){
        this.sinkConfig = sinkConfig;
    }

	public SourceSink getSink() {
		return sink;
	}

	public ConfiguredSourceSink getSinkConfig() {
		return sinkConfig;
	}

}