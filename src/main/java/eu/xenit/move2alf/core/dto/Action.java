package eu.xenit.move2alf.core.dto;

import java.util.HashMap;
import java.util.Map;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.action.ActionPropertyDefinition;

public abstract class Action extends IdObject {
	protected String name;
	protected String description;
	protected String className;
	protected Map<String,ActionPropertyDefinition> configPropertyDefinitionMap = new HashMap<String,ActionPropertyDefinition>();
	protected Map<String,ActionPropertyDefinition> inputPropertyDefinitionMap = new HashMap<String,ActionPropertyDefinition>();
	protected Map<String,ActionPropertyDefinition> reportPropertyDefinitionMap = new HashMap<String, ActionPropertyDefinition>();
	
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
	
	
	// interface
	public abstract boolean execute(Map<String, Object> configPropertyMap, Map<String, Object> inputPropertyMap);
  
}
