package eu.xenit.move2alf.core;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.xenit.move2alf.common.ParameterDefinition;

public abstract class SourceSink {
	protected String name = "Undefined";
	protected String description = "Undefined";
	protected Map<String, ParameterDefinition> configParameterMap = new HashMap<String, ParameterDefinition>();

	protected void addConfigParameter(ParameterDefinition parameterDefinition) {
		configParameterMap.put(parameterDefinition.getName(),
				parameterDefinition);
	}

	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}

	public Collection<ParameterDefinition> getConfigParameters() {
		return configParameterMap.values();
	}

	public ParameterDefinition getConfigParameter(String parameterName) {
		return configParameterMap.get(parameterName);
	}

	public Set<String> getConfigParameterNames() {
		return configParameterMap.keySet();
	}

	public abstract void send(ConfiguredObject configuredSourceSink,
			Map<String, Object> parameterMap);

	public abstract List<File> list(ConfiguredObject sourceConfig, String path,
			boolean recursive);

}
