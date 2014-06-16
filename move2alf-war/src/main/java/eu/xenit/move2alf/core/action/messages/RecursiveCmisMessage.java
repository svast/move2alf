package eu.xenit.move2alf.core.action.messages;

/**
 * Created with IntelliJ IDEA.
 * User: rox
 * Date: 2/10/14
 * Time: 11:33 AM
 */
public class RecursiveCmisMessage {
    private String objectId;
    private String type;
    private String path;
    private String query;

    public RecursiveCmisMessage(String objectId, String type, String path, String query) {
        this.objectId = objectId;
        this.type = type;
        this.path = path;
        this.query = query;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }
}
