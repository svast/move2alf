package eu.xenit.move2alf.core.dto;

import eu.xenit.move2alf.common.IdObject;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/2/13
 * Time: 5:25 PM
 */
public class Resource extends IdObject {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private ConfiguredAction firstConfiguredAction;

    public ConfiguredAction getFirstConfiguredAction() {
        return firstConfiguredAction;
    }

    public void setFirstConfiguredAction(ConfiguredAction firstConfiguredAction) {
        this.firstConfiguredAction = firstConfiguredAction;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    private String classId;

    @Override
    public String toString() {
        return "Resource{" +
                "name='" + name + '\'' +
                ", firstConfiguredAction=" + firstConfiguredAction +
                ", classId='" + classId + '\'' +
                '}';
    }
}
