package eu.xenit.move2alf.core.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.xenit.move2alf.common.PropertyDefinition;
import eu.xenit.move2alf.common.IdObject;

public abstract class Action extends IdObject {
	protected String name;
	protected String description;
	protected String className;
	private Map<String,PropertyDefinition> configPropertyDefinitionMap = new HashMap<String,PropertyDefinition>();
	private Map<String,PropertyDefinition> inputPropertyDefinitionMap = new HashMap<String,PropertyDefinition>();
	private Map<String,PropertyDefinition> reportPropertyDefinitionMap = new HashMap<String, PropertyDefinition>();
	
	public Action() {
		super();
		name = getClass().getSimpleName();
		className = getClass().getName();
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
	
	protected void setConfigPropertyDefinition(String name, Class type, Object defaultValue){
		configPropertyDefinitionMap.put(name, new PropertyDefinition(name,type,defaultValue));
	}

	protected void setInputPropertyDefinition(String name, Class type, Object defaultValue){
		inputPropertyDefinitionMap.put(name, new PropertyDefinition(name,type,defaultValue));
	}

	protected void setReportPropertyDefinition(String name, Class type, Object defaultValue){
		reportPropertyDefinitionMap.put(name, new PropertyDefinition(name,type,defaultValue));
	}
	
	public Set<String> getConfigPropertyNames(){
		return configPropertyDefinitionMap.keySet();
	}
	
	public Set<String> getInputPropertyNames(){
		return inputPropertyDefinitionMap.keySet();
	}
	
	public Set<String> getReportPropertyNames(){
		return reportPropertyDefinitionMap.keySet();
	}
	
	public PropertyDefinition getConfigPropertyDefinition(String name){
		return configPropertyDefinitionMap.get(name);
	}
	public PropertyDefinition getInputPropertyDefinition(String name){
		return inputPropertyDefinitionMap.get(name);
	}
	public PropertyDefinition getReportPropertyDefinition(String name){
		return reportPropertyDefinitionMap.get(name);
	}

	// interface
	public abstract boolean execute(Map<String, Object> configPropertyMap, Map<String, Object> inputPropertyMap);
  
}
