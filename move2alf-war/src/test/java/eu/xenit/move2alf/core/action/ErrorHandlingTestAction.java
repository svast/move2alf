package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ErrorHandlingTestAction extends Action {
	@Override
	protected void executeImpl(final ConfiguredAction configuredAction, final Map<String, Object> parameterMap) {
		// ignore
	}

	@Override
	public void execute(final ConfiguredAction configuredAction, final Map<String, Object> parameterMap) {
		for (int i = 0; i<3; i++) {
			Map<String, Object> newParameterMap = new HashMap<String, Object>(parameterMap);
			newParameterMap.put(Parameters.PARAM_FILE, new File("foo" + i + ".txt"));
			newParameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_FAILED);
			newParameterMap.put(Parameters.PARAM_ERROR_MESSAGE, "testing error handling");
			ConfiguredAction nextAction = configuredAction
					.getAppliedConfiguredActionOnSuccess();
			if (nextAction != null) {
				getJobService().executeAction((Integer) parameterMap.get("cycle"), nextAction, newParameterMap);
			}
		}
	}

	@Override
	public String getName() {
		return "Error handling test action";
	}

	@Override
	public String getDescription() {
		return "Test error handling";
	}

	@Override
	public String getCategory() {
		return CAT_METADATA;
	}
}
