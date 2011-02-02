package eu.xenit.move2alf.core.dto;

import java.util.Set;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.SourceSink;

public class ConfiguredSourceSink extends IdObject {
	private int id;
	private String sourceSinkClassName;
	private Set<ConfiguredSourceSinkParameter> configuredSourceSinkParameterSet;
	private Set<ConfiguredAction> configuredActionSet;
	private Set<ConfiguredReport> configuredReportSet;

	public ConfiguredSourceSink() {

	}

	public void setId(int id){
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setSourceSinkClassName(String sourceSinkClassName) {
		this.sourceSinkClassName = sourceSinkClassName;
	}

	public String getSourceSinkClassName() {
		return sourceSinkClassName;
	}

	public void setConfiguredSourceSinkParameterSet(
			Set<ConfiguredSourceSinkParameter> configuredSourceSinkParameterSet) {
		this.configuredSourceSinkParameterSet = configuredSourceSinkParameterSet;
	}

	public Set<ConfiguredSourceSinkParameter> getConfiguredSourceSinkParameterSet() {
		return configuredSourceSinkParameterSet;
	}

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
