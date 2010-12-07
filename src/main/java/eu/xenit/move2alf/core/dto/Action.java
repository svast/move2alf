package eu.xenit.move2alf.core.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.action.ActionPropertyDefinition;

public abstract class Action extends IdObject {
	protected String name;
	protected String description;
	protected String className;
	private Map<String,ActionPropertyDefinition> configPropertyDefinitionMap = new HashMap<String,ActionPropertyDefinition>();
	private Map<String,ActionPropertyDefinition> inputPropertyDefinitionMap = new HashMap<String,ActionPropertyDefinition>();
	private Map<String,ActionPropertyDefinition> reportPropertyDefinitionMap = new HashMap<String, ActionPropertyDefinition>();
	
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
		configPropertyDefinitionMap.put(name, new ActionPropertyDefinition(name,type,defaultValue));
	}

	protected void setInputPropertyDefinition(String name, Class type, Object defaultValue){
		inputPropertyDefinitionMap.put(name, new ActionPropertyDefinition(name,type,defaultValue));
	}

	protected void setReportPropertyDefinition(String name, Class type, Object defaultValue){
		reportPropertyDefinitionMap.put(name, new ActionPropertyDefinition(name,type,defaultValue));
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
	
	public ActionPropertyDefinition getConfigPropertyDefinition(String name){
		return configPropertyDefinitionMap.get(name);
	}
	public ActionPropertyDefinition getInputPropertyDefinition(String name){
		return inputPropertyDefinitionMap.get(name);
	}
	public ActionPropertyDefinition getReportPropertyDefinition(String name){
		return reportPropertyDefinitionMap.get(name);
	}

	// interface
	public abstract boolean execute(Map<String, Object> configPropertyMap, Map<String, Object> inputPropertyMap);
  
}
