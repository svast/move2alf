package eu.xenit.move2alf.core.action;

import java.util.Map;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;

public class SourceAction extends Action {

	@Override
	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// get sourcesink
		// path
		// recursive
		// filters
		for (ConfiguredSourceSink sourceSink : configuredAction
				.getConfiguredSourceSinkSet()) {

		}
	}

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// empty; SourceAction overrides the execute method to execute the next
		// action for each file separately
	}

}
