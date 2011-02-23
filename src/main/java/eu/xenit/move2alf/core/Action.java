package eu.xenit.move2alf.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.xenit.move2alf.common.ParameterDefinition;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public abstract class Action {
	protected String name = "Undefined";
	protected String description = "Undefined";
	protected Map<String, ParameterDefinition> configParameterMap = new HashMap<String, ParameterDefinition>();
	protected Map<String, ParameterDefinition> aprioriInputParameterMap = new HashMap<String, ParameterDefinition>();
	protected Map<String, ParameterDefinition> aprioriOutputParameterMap = new HashMap<String, ParameterDefinition>();

	private ActionFactory actionFactory;

	public void setActionFactory(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}

	public ActionFactory getActionFactory() {
		return actionFactory;
	}

	protected void addConfigParameter(ParameterDefinition parameterDefinition) {
		configParameterMap.put(parameterDefinition.getName(),
				parameterDefinition);
	}

	protected void addAprioriInputParameter(
			ParameterDefinition parameterDefinition) {
		aprioriInputParameterMap.put(parameterDefinition.getName(),
				parameterDefinition);
	}

	protected void addAprioriOutputParameter(
			ParameterDefinition parameterDefinition) {
		aprioriOutputParameterMap.put(parameterDefinition.getName(),
				parameterDefinition);
	}

	public String getName() {
		return name;
	};

	public String getDescription() {
		return description;
	};

	public Collection<ParameterDefinition> getConfigParameters() {
		return configParameterMap.values();
	}

	public ParameterDefinition getConfigParameter(String parameterName) {
		return configParameterMap.get(parameterName);
	}

	public Set<String> getConfigParameterNames() {
		return configParameterMap.keySet();
	}

	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		executeImpl(configuredAction, parameterMap);
		ConfiguredAction nextAction = configuredAction
				.getAppliedConfiguredActionOnSuccess();
		if (nextAction != null) {
			getActionFactory().getAction(nextAction.getActionClassName())
					.execute(nextAction, parameterMap); // TODO: helper method
			// in ActionFactory?
		}
	}

	protected abstract void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap);

	/*
	 * the actual input and output parameters can depend on the config and can
	 * be oveloaded in subclass
	 */
	public Collection<ParameterDefinition> getInputParameters(
			ConfiguredAction configuredAction) {
		return aprioriInputParameterMap.values();
	}

	public Collection<ParameterDefinition> getOutputParameters(
			ConfiguredAction configuredAction) {
		return aprioriOutputParameterMap.values();
	}

}
