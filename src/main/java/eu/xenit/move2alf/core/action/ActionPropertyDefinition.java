package eu.xenit.move2alf.core.action;

public class ActionPropertyDefinition{
	private String name;
	private Class type;
	private Object defaultValue;

	public ActionPropertyDefinition() {
	}


	public ActionPropertyDefinition(String name, Class type,
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
