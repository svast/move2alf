package eu.xenit.move2alf.core.dto;

import eu.xenit.move2alf.common.IdObject;

public class ConfiguredAction extends IdObject {
	private Action action;
	
	private ConfiguredAction appliedConfiguredActionOnSuccess;
	
	private ConfiguredAction appliedConfiguredActionOnFailure;
	
	public ConfiguredAction() {
		
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Action getAction() {
		return action;
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
}
