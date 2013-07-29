package eu.xenit.move2alf.core.action.messages;

import eu.xenit.move2alf.core.simpleaction.data.Batch;
import eu.xenit.move2alf.core.sourcesink.ACL;
import eu.xenit.move2alf.repository.UploadResult;

import java.util.List;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/20/13
 * Time: 11:38 AM
 */
public class BatchACLMessage {

    public final List<UploadResult> uploadResultList;
    public final Batch batch;
    public final List<ACL> acls;

    public BatchACLMessage(List<UploadResult> uploadResultList, Batch batch, List<ACL> acls) {
        this.uploadResultList = uploadResultList;
        this.batch = batch;
        this.acls = acls;
    }
}
