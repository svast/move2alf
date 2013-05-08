package eu.xenit.move2alf.core;

import java.util.Map;

import eu.xenit.move2alf.common.IdObject;

public abstract class ConfiguredObject extends IdObject {
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    private String classId;
	
	private Map<String, String> parameters;

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}
	
	public String getParameter(String name) {
		return getParameters().get(name);
	}
}
