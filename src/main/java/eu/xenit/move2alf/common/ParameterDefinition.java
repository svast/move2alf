package eu.xenit.move2alf.common;

public class ParameterDefinition{
	private String name;
	private Class<?> clazz;
	private Object defaultValue;

	public ParameterDefinition() {
	}


	public ParameterDefinition(String name, Class<?> clazz,
			Object defaultValue) {
		this.name = name;
		this.clazz = clazz;
		this.defaultValue = defaultValue;
	}


	public String getName() {
		return name;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}
    
//	public T cast(Object object){
//		  return clazz.cast(object);
//	}
}
