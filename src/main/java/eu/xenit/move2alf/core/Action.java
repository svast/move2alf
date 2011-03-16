package eu.xenit.move2alf.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.xenit.move2alf.common.ParameterDefinition;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public abstract class Action extends ConfigurableObject {
	protected Map<String, ParameterDefinition> aprioriInputParameterMap = new HashMap<String, ParameterDefinition>();
	protected Map<String, ParameterDefinition> aprioriOutputParameterMap = new HashMap<String, ParameterDefinition>();

	private ActionFactory actionFactory;
	
	private SourceSinkFactory sourceSinkFactory;

	public void setActionFactory(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}

	public ActionFactory getActionFactory() {
		return actionFactory;
	}

	public void setSourceSinkFactory(SourceSinkFactory sourceSinkFactory) {
		this.sourceSinkFactory = sourceSinkFactory;
	}

	public SourceSinkFactory getSourceSinkFactory() {
		return sourceSinkFactory;
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

	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		executeImpl(configuredAction, parameterMap);
		ConfiguredAction nextAction = configuredAction
				.getAppliedConfiguredActionOnSuccess();
		if (nextAction != null) {
			getActionFactory().execute(nextAction, parameterMap);
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
