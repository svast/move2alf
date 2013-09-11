package eu.xenit.move2alf.core.action.messages;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/2/13
 * Time: 8:41 AM
 */
public class CheckExistenceMessage {

    public final String fileName;

    public CheckExistenceMessage(String fileName) {
        this.fileName = fileName;
    }
}
