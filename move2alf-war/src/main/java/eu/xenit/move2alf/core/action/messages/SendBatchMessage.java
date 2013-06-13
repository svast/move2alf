package eu.xenit.move2alf.core.action.messages;

import eu.xenit.move2alf.core.sourcesink.WriteOption;
import eu.xenit.move2alf.repository.alfresco.ws.Document;

import java.util.List;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 5:11 PM
 */
public class SendBatchMessage extends AlfrescoMessage {

    public final WriteOption writeOption;
    public final List<Document> documentsToUpload;

    public SendBatchMessage(WriteOption writeOption, List<Document> documentsToUpload){
        this.writeOption = writeOption;
        this.documentsToUpload = documentsToUpload;
    }
}
