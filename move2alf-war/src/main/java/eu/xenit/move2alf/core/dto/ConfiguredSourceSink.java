package eu.xenit.move2alf.core.dto;

import java.util.Set;

import eu.xenit.move2alf.core.ConfiguredObject;

public class ConfiguredSourceSink extends ConfiguredObject {
	
	private Set<ConfiguredAction> configuredActionSet;
	
	private Set<ConfiguredReport> configuredReportSet;

	public void setConfiguredActionSet(Set<ConfiguredAction> configuredActionSet) {
		this.configuredActionSet = configuredActionSet;
	}

	public Set<ConfiguredAction> getConfiguredActionSet() {
		return configuredActionSet;
	}

	public void setConfiguredReportSet(Set<ConfiguredReport> configuredReportSet) {
		this.configuredReportSet = configuredReportSet;
	}

	public Set<ConfiguredReport> getConfiguredReportSet() {
		return configuredReportSet;
	}

}