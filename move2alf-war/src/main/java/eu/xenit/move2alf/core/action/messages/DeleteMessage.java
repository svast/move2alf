package eu.xenit.move2alf.core.action.messages;

import eu.xenit.move2alf.core.sharedresource.alfresco.DeleteOption;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/2/13
 * Time: 9:28 AM
 */
public class DeleteMessage {
    public final String remotePath;
    public final String name;
    public final DeleteOption deleteOption;

    public DeleteMessage(String remotePath, String name, DeleteOption deleteOption) {
        this.remotePath = remotePath;
        this.name = name;
        this.deleteOption = deleteOption;
    }
}
