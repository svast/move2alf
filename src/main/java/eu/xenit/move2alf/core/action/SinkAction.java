package eu.xenit.move2alf.core.action;

import java.util.Map;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;

public class SinkAction extends Action {

	private static final String PARAM_PATH = "path";

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		ConfiguredSourceSink sinkConfig = (ConfiguredSourceSink) configuredAction
				.getConfiguredSourceSinkSet().toArray()[0];
		SourceSink sink = getSourceSinkFactory().getObject(
				sinkConfig.getClassName());
		String path = configuredAction.getParameter(PARAM_PATH);
		String docExistsMode = configuredAction.getParameter("documentExists");
		parameterMap.put("path", path);
		sink.send(sinkConfig, parameterMap, docExistsMode);
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_DEFAULT;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Sink";
	}

}
