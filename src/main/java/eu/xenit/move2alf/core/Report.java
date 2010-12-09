package eu.xenit.move2alf.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.xenit.move2alf.common.ParameterDefinition;
import eu.xenit.move2alf.core.dto.ConfiguredReport;

public abstract class Report{
	protected String name = "Undefined";
	protected String description = "Undefined";
	protected Map<String, ParameterDefinition> configParameterMap = new HashMap<String, ParameterDefinition>();

	protected void addConfigParameter(ParameterDefinition parameterDefinition){
		configParameterMap.put(parameterDefinition.getName(),parameterDefinition);
	}
	
	public String getName(){return name;};
	public String getDescription(){return description;};
	
	public Collection<ParameterDefinition> getConfigParameters(){
		return configParameterMap.values();
	}
	public ParameterDefinition getConfigParameter(String parameterName){
		return configParameterMap.get(parameterName);
	}
	public Set<String> getConfigParameterNames(){
		return configParameterMap.keySet();
	}

	public abstract void send(ConfiguredReport configuredReport, Map<String, Object> parameterMap);  

}
