package eu.xenit.move2alf.core.dto;

public class ConfiguredSourceSinkParameter {
	private ConfiguredSourceSink configuredSourceSink;
	private String name;
	private String value;
	
	
	public ConfiguredSourceSinkParameter() {
	}

	public ConfiguredSourceSink getConfiguredSourceSink() {
		return configuredSourceSink;
	}

	public void setConfiguredSourceSink(ConfiguredSourceSink configuredSourceSink) {
		this.configuredSourceSink = configuredSourceSink;
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
