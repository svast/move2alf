package eu.xenit.move2alf.core.dto;

public class ConfiguredReportParameter {
	private ConfiguredReport configuredReport;
	private String name;
	private String value;
	
	
	public ConfiguredReportParameter() {
	}


	public ConfiguredReport getConfiguredReport() {
		return configuredReport;
	}


	public void setConfiguredReport(ConfiguredReport configuredReport) {
		this.configuredReport = configuredReport;
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
