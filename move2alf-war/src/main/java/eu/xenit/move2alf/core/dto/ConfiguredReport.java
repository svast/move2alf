package eu.xenit.move2alf.core.dto;

import java.util.Set;

import eu.xenit.move2alf.core.ConfiguredObject;

public class ConfiguredReport extends ConfiguredObject {

	private Set<ConfiguredSourceSink> configuredSourceSinkSet;

	public Set<ConfiguredSourceSink> getConfiguredSourceSinkSet() {
		return configuredSourceSinkSet;
	}

	public void setConfiguredSourceSinkSet(
			Set<ConfiguredSourceSink> configuredSourceSinkSet) {
		this.configuredSourceSinkSet = configuredSourceSinkSet;
	}

}
