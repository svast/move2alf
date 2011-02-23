package eu.xenit.move2alf.core.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.xenit.move2alf.common.ParameterDefinition;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfiguredObject;

public class ParameterRenameAction extends Action {
	// config parameters
	private static final String CP_OldParameterName = "oldParameterName";
	private static final String CP_NewParameterName = "newParameterName";

	public ParameterRenameAction() {
		name = "Parameter Rename Action";
		description = "Action that renames the parameter name, without changing the value";
		addConfigParameter(new ParameterDefinition(CP_OldParameterName,
				String.class, null));
		addConfigParameter(new ParameterDefinition(CP_NewParameterName,
				String.class, null));
	}

	@Override
	public void executeImpl(ConfiguredObject configuredAction,
			Map<String, Object> parameterMap) {
		String oldName = configuredAction.getParameter(CP_OldParameterName);
		String newName = configuredAction.getParameter(CP_NewParameterName);

		// TODO
		// if (oldName != null && newName != null) {
		// parameterMap.put(newName, parameterMap.get(oldName));
		// parameterMap.remove(oldName);
		// configuredAction.getAppliedConfiguredActionOnSuccess().execute(
		// parameterMap);
		// } else {
		// configuredAction.getAppliedConfiguredActionOnFailure().execute(
		// parameterMap);
		// }
	}

	public Collection<ParameterDefinition> getInputParameters(
			ConfiguredObject configuredAction) {
		Map<String, ParameterDefinition> inputParameterMap = new HashMap<String, ParameterDefinition>();
		inputParameterMap.putAll(aprioriInputParameterMap);
		String oldName = configuredAction.getParameter(CP_OldParameterName);
		if (oldName != null) {
			inputParameterMap.put(oldName, new ParameterDefinition(oldName,
					Object.class, null));
		}
		return inputParameterMap.values();
	}

	public Collection<ParameterDefinition> getOutputParameters(
			ConfiguredObject configuredAction) {
		Map<String, ParameterDefinition> outputParameterMap = new HashMap<String, ParameterDefinition>();
		outputParameterMap.putAll(aprioriOutputParameterMap);
		
		String newName = configuredAction.getParameter(CP_NewParameterName);
		if (newName != null) {
			outputParameterMap.put(newName, new ParameterDefinition(newName,
					Object.class, null));
		}
		return outputParameterMap.values();
	}

}
