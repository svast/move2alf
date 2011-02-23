package eu.xenit.move2alf.core.dto;

import eu.xenit.move2alf.core.ConfiguredObject;

public class ProcessedDocumentParameter {
	private ConfiguredObject configuredAction;

	private String name;
	
	private String value;
	
	public ProcessedDocumentParameter() {
		
	}

	public ConfiguredObject getConfiguredAction() {
		return configuredAction;
	}

	public void setConfiguredAction(ConfiguredObject configuredAction) {
		this.configuredAction = configuredAction;
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
