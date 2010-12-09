package eu.xenit.move2alf.core.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.xenit.move2alf.common.ParameterDefinition;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredActionParameter;

public class ParameterRenameAction extends Action {
	// config parameters
	private static final String PoldParameterName = "oldParameterName";
	private static final String PnewParameterName = "newParameterName";

	/*
	 * the ActionFactory will only register Actions that have implemented the singleton pattern
	 */
	private static final ParameterRenameAction instance = new ParameterRenameAction();
	
    public static ParameterRenameAction getInstance() {
        return instance;
    }

	private ParameterRenameAction() {
		name = "Parameter Rename Action";
		description = "Action that renames the parameter name, without changing the value";
		addConfigParameter(new ParameterDefinition(PoldParameterName,
				String.class, null));
		addConfigParameter(new ParameterDefinition(PnewParameterName,
				String.class, null));
	}

	@Override
	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		String oldName = null;
		String newName = null;
		for (ConfiguredActionParameter configuredActionParameter : configuredAction
				.getConfiguredActionParameterSet()) {
			if (configuredActionParameter.getName().equals(PoldParameterName)) {
				oldName = configuredActionParameter.getValue();
			}
			if (configuredActionParameter.getName().equals(PnewParameterName)) {
				newName = configuredActionParameter.getValue();
			}
		}
		if (oldName != null && newName != null) {
			parameterMap.put(newName, parameterMap.get(oldName));
			parameterMap.remove(oldName);
			configuredAction.getAppliedConfiguredActionOnSuccess().execute(
					parameterMap);
		} else {
			configuredAction.getAppliedConfiguredActionOnFailure().execute(
					parameterMap);
		}
	}

	public Collection<ParameterDefinition> getInputParameters(
			ConfiguredAction configuredAction) {
		Map<String, ParameterDefinition> inputParameterMap = new HashMap<String, ParameterDefinition>();
		inputParameterMap.putAll(aprioriInputParameterMap);
		String oldName = null;
		for (ConfiguredActionParameter configuredActionParameter : configuredAction
				.getConfiguredActionParameterSet()) {
			if (configuredActionParameter.getName().equals(PoldParameterName)) {
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
			ConfiguredAction configuredAction) {
		Map<String, ParameterDefinition> outputParameterMap = new HashMap<String, ParameterDefinition>();
		outputParameterMap.putAll(aprioriOutputParameterMap);
		String newName = null;
		for (ConfiguredActionParameter configuredActionParameter : configuredAction
				.getConfiguredActionParameterSet()) {
			if (configuredActionParameter.getName().equals(PnewParameterName)) {
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
