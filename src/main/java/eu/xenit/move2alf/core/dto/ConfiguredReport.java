package eu.xenit.move2alf.core.dto;

import java.util.Set;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.Report;

public class ConfiguredReport extends IdObject {
	private String reportClassName;
		
	private Set<ConfiguredReportParameter> configuredReportParameterSet;
	
	private Set<ConfiguredSourceSink> configuredSourceSinkSet;
	
	public ConfiguredReport() {
		
	}

	public String getReportClassName() {
		return reportClassName;
	}

	public void setReportClassName(String reportClassName) {
		this.reportClassName = reportClassName;
	}

	public Set<ConfiguredReportParameter> getConfiguredReportParameterSet() {
		return configuredReportParameterSet;
	}

	public void setConfiguredReportParameterSet(
			Set<ConfiguredReportParameter> configuredReportParameterSet) {
		this.configuredReportParameterSet = configuredReportParameterSet;
	}

	public Set<ConfiguredSourceSink> getConfiguredSourceSinkSet() {
		return configuredSourceSinkSet;
	}

	public void setConfiguredSourceSinkSet(
			Set<ConfiguredSourceSink> configuredSourceSinkSet) {
		this.configuredSourceSinkSet = configuredSourceSinkSet;
	}

	
}
