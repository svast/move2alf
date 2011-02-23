package eu.xenit.move2alf.core.dto;

import eu.xenit.move2alf.core.ConfiguredObject;

public class ConfiguredObjectParameter {
	private ConfiguredObject configuredObject;
	private String name;
	private String value;

	public ConfiguredObject getConfiguredObject() {
		return configuredObject;
	}

	public void setConfiguredObject(ConfiguredObject configuredObject) {
		this.configuredObject = configuredObject;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
