package eu.xenit.move2alf.common;

public class ParameterDefinition{
	private String name;
	private Class<?> clazz;
	private Object defaultValue;
	private String className;

	public ParameterDefinition() {
	}

	public ParameterDefinition(String name, Class<?> clazz,
			Object defaultValue) {
		this.name = name;
		this.clazz = clazz;
		this.className = clazz.getName();
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getClassName() {
		return className;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}
    
}
