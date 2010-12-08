package eu.xenit.move2alf.core.dto;

public class ReportActionProperty {
	private ConfiguredAction configuredAction;
	
	private String reportPropertyName;
	
	public ReportActionProperty() {

	}

	public void setConfiguredAction(ConfiguredAction configuredAction) {
		this.configuredAction = configuredAction;
	}

	public ConfiguredAction getConfiguredAction() {
		return configuredAction;
	}

	public void setReportPropertyName(String reportPropertyName) {
		this.reportPropertyName = reportPropertyName;
	}

	public String getReportPropertyName() {
		return reportPropertyName;
	}
}
