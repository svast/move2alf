package eu.xenit.move2alf.core.dto;

import java.util.Set;

import eu.xenit.move2alf.core.ConfiguredObject;

public class ConfiguredAction extends ConfiguredObject {

	private ConfiguredAction appliedConfiguredActionOnSuccess;

	private ConfiguredAction appliedConfiguredActionOnFailure;

	private Set<ConfiguredSourceSink> configuredSourceSinkSet;
    private Set<Object> receivers;

    public Set<Object> getReceivers() {
        return receivers;
    }

    public void setReceivers(Set<Object> receivers) {
        this.receivers = receivers;
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

	public Set<ConfiguredSourceSink> getConfiguredSourceSinkSet() {
		return configuredSourceSinkSet;
	}

	public void setConfiguredSourceSinkSet(
			Set<ConfiguredSourceSink> configuredSourceSinkSet) {
		this.configuredSourceSinkSet = configuredSourceSinkSet;
	}
}
