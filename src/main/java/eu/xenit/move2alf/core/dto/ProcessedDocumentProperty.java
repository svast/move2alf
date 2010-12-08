package eu.xenit.move2alf.core.dto;

public class ProcessedDocumentProperty {
	private String reportPropertyName;
	
	private String value;
	
	private ConfiguredAction configuredAction;
	
	public ProcessedDocumentProperty() {
		
	}

	public void setReportPropertyName(String reportPropertyName) {
		this.reportPropertyName = reportPropertyName;
	}

	public String getReportPropertyName() {
		return reportPropertyName;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setConfiguredAction(ConfiguredAction configuredAction) {
		this.configuredAction = configuredAction;
	}

	public ConfiguredAction getConfiguredAction() {
		return configuredAction;
	}
}
