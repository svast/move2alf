package eu.xenit.move2alf.core.action;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/16/13
 * Time: 11:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class ActionClassInfo {

    private final String classId;
    private final String category;
    private final Class clazz;
    private final String description;


    public String getDescription() {
        return description;
    }

    public ActionClassInfo(String classId, String category, Class clazz, String description) {
        this.classId = classId;
        this.category = category;
        this.clazz = clazz;
        this.description = description;

    }

    public String getClassId() {
        return classId;
    }

    public String getCategory() {
        return category;
    }

    public Class getClazz() {
        return clazz;
    }
}
