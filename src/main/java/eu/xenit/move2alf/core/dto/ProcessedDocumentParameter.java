package eu.xenit.move2alf.core.dto;

public class ProcessedDocumentParameter {
	private ConfiguredAction configuredAction;

	private String name;

	private String value;

	public ProcessedDocumentParameter() {

	}

	public ConfiguredAction getConfiguredAction() {
		return configuredAction;
	}

	public void setConfiguredAction(ConfiguredAction configuredAction) {
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
