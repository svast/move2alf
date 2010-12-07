package eu.xenit.move2alf.core.dto;

import eu.xenit.move2alf.common.IdObject;

public class ActionConfigPropertyDefinition extends IdObject {
	private Action action;
	private String name;
	private String type;
	private String defaultValue;

	public ActionConfigPropertyDefinition() {
		super();
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}
