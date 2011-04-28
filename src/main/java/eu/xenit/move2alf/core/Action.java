package eu.xenit.move2alf.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.xenit.move2alf.common.ParameterDefinition;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.logic.JobService;

public abstract class Action extends ConfigurableObject {
	protected Map<String, ParameterDefinition> aprioriInputParameterMap = new HashMap<String, ParameterDefinition>();
	protected Map<String, ParameterDefinition> aprioriOutputParameterMap = new HashMap<String, ParameterDefinition>();

	private ActionFactory actionFactory;
	
	private SourceSinkFactory sourceSinkFactory;
	
	private JobService jobService;

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
	
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}
	
	public JobService getJobService() {
		return jobService;
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
			getJobService().executeAction((Integer) parameterMap.get("cycle"), nextAction, parameterMap);
		}
	}

	protected abstract void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap);
	
	/*
	 * TODO: set up and tear down, what input is required?
	 * Better (or complementary?) solution: CycleContext to store state during cycle
	public void startCycle() {
		
	}
	
	public void endCycle() {
		
	}
	 */

	/*
	 * the actual input and output parameters can depend on the config and can
	 * be overloaded in subclass
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
