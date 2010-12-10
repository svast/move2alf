package eu.xenit.move2alf.common;

public class ParameterDefinition<T> {
	private String name;
	private Class<? extends T> clazz;
	private Object defaultValue;

	public ParameterDefinition() {
	}

	public ParameterDefinition(String name, Class<? extends T> clazz,
			Object defaultValue) {
		this.name = name;
		this.clazz = clazz;
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public Class<? extends T> getClazz() {
		return clazz;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}
    
	@SuppressWarnings("unchecked")
	public T cast(Object object) {
		return clazz.cast(object);
	}
}
