package eu.xenit.move2alf.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.xenit.move2alf.common.ParameterDefinition;

public abstract class ConfigurableObject {
	
	public static final String CAT_DEFAULT = "default";
	public static final String CAT_METADATA = "metadata";
	public static final String CAT_TRANSFORM = "transform";
	public static final String CAT_DESTINATION = "destination";
	
	protected Map<String, ParameterDefinition> configParameterMap = new HashMap<String, ParameterDefinition>();

	public ConfigurableObject() {
		super();
	}

	protected void addConfigParameter(ParameterDefinition parameterDefinition) {
		configParameterMap.put(parameterDefinition.getName(),
				parameterDefinition);
	}

	public abstract String getName();

	public abstract String getDescription();
	
	public abstract String getCategory();

	public Collection<ParameterDefinition> getConfigParameters() {
		return configParameterMap.values();
	}

	public ParameterDefinition getConfigParameter(String parameterName) {
		return configParameterMap.get(parameterName);
	}

	public Set<String> getConfigParameterNames() {
		return configParameterMap.keySet();
	}

}