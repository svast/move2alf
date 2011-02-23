package eu.xenit.move2alf.core.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.xenit.move2alf.common.ParameterDefinition;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfiguredObject;
import eu.xenit.move2alf.core.dto.ConfiguredObjectParameter;

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
		String oldName = null;
		String newName = null;
		for (ConfiguredObjectParameter configuredActionParameter : configuredAction
				.getConfiguredObjectParameterSet()) {
			if (configuredActionParameter.getName().equals(CP_OldParameterName)) {
				oldName = configuredActionParameter.getValue();
			}
			if (configuredActionParameter.getName().equals(CP_NewParameterName)) {
				newName = configuredActionParameter.getValue();
			}
		}
// TODO
//		if (oldName != null && newName != null) {
//			parameterMap.put(newName, parameterMap.get(oldName));
//			parameterMap.remove(oldName);
//			configuredAction.getAppliedConfiguredActionOnSuccess().execute(
//					parameterMap);
//		} else {
//			configuredAction.getAppliedConfiguredActionOnFailure().execute(
//					parameterMap);
//		}
	}

	public Collection<ParameterDefinition> getInputParameters(
			ConfiguredObject configuredAction) {
		Map<String, ParameterDefinition> inputParameterMap = new HashMap<String, ParameterDefinition>();
		inputParameterMap.putAll(aprioriInputParameterMap);
		String oldName = null;
		for (ConfiguredObjectParameter configuredActionParameter : configuredAction
				.getConfiguredObjectParameterSet()) {
			if (configuredActionParameter.getName().equals(CP_OldParameterName)) {
				oldName = configuredActionParameter.getValue();
				if (oldName != null) {
					inputParameterMap.put(oldName, new ParameterDefinition(
							oldName, Object.class, null));
				}
				break;
			}
		}
		return inputParameterMap.values();
	}

	public Collection<ParameterDefinition> getOutputParameters(
			ConfiguredObject configuredAction) {
		Map<String, ParameterDefinition> outputParameterMap = new HashMap<String, ParameterDefinition>();
		outputParameterMap.putAll(aprioriOutputParameterMap);
		String newName = null;
		for (ConfiguredObjectParameter configuredActionParameter : configuredAction
				.getConfiguredObjectParameterSet()) {
			if (configuredActionParameter.getName().equals(CP_NewParameterName)) {
				newName = configuredActionParameter.getValue();
				if (newName != null) {
					outputParameterMap.put(newName, new ParameterDefinition(
							newName, Object.class, null));
				}
				break;
			}
		}
		return outputParameterMap.values();
	}

}
