package eu.xenit.move2alf.core.simpleaction.helpers;

import eu.xenit.move2alf.core.ApplicationContextProvider;
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.sourcesink.SourceSink;
import eu.xenit.move2alf.core.sourcesink.SourceSinkFactory;
import eu.xenit.move2alf.logic.JobService;
import org.springframework.context.ApplicationContext;

public abstract class SimpleActionWithSourceSink<T> extends Move2AlfReceivingAction<T> {

	private SourceSink sink;

    public static final String PARAM_DESTINATION = "destination";
	private ConfiguredSourceSink sinkConfig;
    public void setDestination(String dest){
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        sinkConfig = ((JobService)context.getBean("jobService")).getDestination(Integer.parseInt(dest));
        sink = ((SourceSinkFactory)context.getBean("sourceSinkFactory")).getObject(sinkConfig.getClassId());
    }

	public SourceSink getSink() {
		return sink;
	}

	public ConfiguredSourceSink getSinkConfig() {
		return sinkConfig;
	}

}