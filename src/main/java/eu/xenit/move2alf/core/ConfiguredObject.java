package eu.xenit.move2alf.core;

import java.util.Set;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.dto.ConfiguredObjectParameter;

public abstract class ConfiguredObject extends IdObject {
	private String className;
	private Set<ConfiguredObjectParameter> configuredObjectParameterSet;

	public void setClassName(String sourceSinkClassName) {
		this.className = sourceSinkClassName;
	}

	public String getClassName() {
		return className;
	}

	public Set<ConfiguredObjectParameter> getConfiguredObjectParameterSet() {
		return configuredObjectParameterSet;
	}

	public void setConfiguredObjectParameterSet(Set<ConfiguredObjectParameter> configuredObjectParameterSet) {
		this.configuredObjectParameterSet = configuredObjectParameterSet;
	}
}
