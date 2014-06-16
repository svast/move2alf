package eu.xenit.move2alf.core.action.messages;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/2/13
 * Time: 9:54 AM
 */
public class ListMessage {
    public final String remotePath;
    public final String name;

    public ListMessage(String remotePath, String name) {
        this.remotePath = remotePath;
        this.name = name;
    }
}
