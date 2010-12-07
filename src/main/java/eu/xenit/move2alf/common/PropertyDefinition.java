package eu.xenit.move2alf.common;

public class PropertyDefinition{
	private String name;
	private Class type;
	private Object defaultValue;

	public PropertyDefinition() {
	}


	public PropertyDefinition(String name, Class type,
			Object defaultValue) {
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
	}


	public String getName() {
		return name;
	}

	public Class getType() {
		return type;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

}
