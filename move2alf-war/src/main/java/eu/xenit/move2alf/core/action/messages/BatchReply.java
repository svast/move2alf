package eu.xenit.move2alf.core.action.messages;

import eu.xenit.move2alf.repository.UploadResult;

import java.util.List;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/20/13
 * Time: 11:21 AM
 */
public class BatchReply extends AlfrescoReplyMessage {

    public final List<UploadResult> uploadResults;

    public BatchReply(List<UploadResult> uploadResults) {
        this.uploadResults = uploadResults;
    }
}
