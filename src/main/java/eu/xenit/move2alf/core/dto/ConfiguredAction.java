package eu.xenit.move2alf.core.dto;

import java.util.Map;
import java.util.Set;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ActionFactory;

public class ConfiguredAction extends IdObject {
	private String actionClassName;
	
	private ConfiguredAction appliedConfiguredActionOnSuccess;
	
	private ConfiguredAction appliedConfiguredActionOnFailure;
	
	private Set<ConfiguredActionParameter> configuredActionParameterSet;
	
	private Set<ConfiguredSourceSink> configuredSourceSinkSet;
	
	public ConfiguredAction() {
		
	}

	public String getActionClassName() {
		return actionClassName;
	}

	public void setActionClassName(String actionClassName) {
		this.actionClassName = actionClassName;
	}

	public void setAppliedConfiguredActionOnSuccess(
			ConfiguredAction appliedConfiguredActionOnSuccess) {
		this.appliedConfiguredActionOnSuccess = appliedConfiguredActionOnSuccess;
	}

	public ConfiguredAction getAppliedConfiguredActionOnSuccess() {
		return appliedConfiguredActionOnSuccess;
	}

	public void setAppliedConfiguredActionOnFailure(
			ConfiguredAction appliedConfiguredActionOnFailure) {
		this.appliedConfiguredActionOnFailure = appliedConfiguredActionOnFailure;
	}

	public ConfiguredAction getAppliedConfiguredActionOnFailure() {
		return appliedConfiguredActionOnFailure;
	}

	public Set<ConfiguredActionParameter> getConfiguredActionParameterSet() {
		return configuredActionParameterSet;
	}

	public void setConfiguredActionParameterSet(
			Set<ConfiguredActionParameter> configuredActionParameterSet) {
		this.configuredActionParameterSet = configuredActionParameterSet;
	}

	public Set<ConfiguredSourceSink> getConfiguredSourceSinkSet() {
		return configuredSourceSinkSet;
	}

	public void setConfiguredSourceSinkSet(
			Set<ConfiguredSourceSink> configuredSourceSinkSet) {
		this.configuredSourceSinkSet = configuredSourceSinkSet;
	}
	
	// convenience
	public void execute(Map<String,Object> parameterMap){
		ActionFactory.getInstance().getAction(actionClassName).execute(this, parameterMap);
	}
	
}
