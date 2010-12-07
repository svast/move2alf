package eu.xenit.move2alf.core.dto;

import java.util.HashSet;
import java.util.Set;

import eu.xenit.move2alf.common.IdObject;

public class Action extends IdObject {
	private String name;
	private String description;
	private String className;
	private Set<ActionConfigPropertyDefinition> configPropertyDefinitionSet = new HashSet<ActionConfigPropertyDefinition>();
	private Set<ActionInputPropertyDefinition> inputPropertyDefinitionSet = new HashSet<ActionInputPropertyDefinition>();
	private Set<ActionReportPropertyDefinition> reportPropertyDefinitionSet = new HashSet<ActionReportPropertyDefinition>();
	
	public Action() {
		super();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public Set<ActionConfigPropertyDefinition> getConfigPropertyDefinitionSet() {
		return configPropertyDefinitionSet;
	}
	public void setConfigPropertyDefinitionSet(
			Set<ActionConfigPropertyDefinition> configPropertyDefinitionSet) {
		this.configPropertyDefinitionSet = configPropertyDefinitionSet;
	}
	public Set<ActionInputPropertyDefinition> getInputPropertyDefinitionSet() {
		return inputPropertyDefinitionSet;
	}
	public void setInputPropertyDefinitionSet(
			Set<ActionInputPropertyDefinition> inputPropertyDefinitionSet) {
		this.inputPropertyDefinitionSet = inputPropertyDefinitionSet;
	}
	public Set<ActionReportPropertyDefinition> getReportPropertyDefinitionSet() {
		return reportPropertyDefinitionSet;
	}
	public void setReportPropertyDefinitionSet(
			Set<ActionReportPropertyDefinition> reportPropertyDefinitionSet) {
		this.reportPropertyDefinitionSet = reportPropertyDefinitionSet;
	}
	
  
}
