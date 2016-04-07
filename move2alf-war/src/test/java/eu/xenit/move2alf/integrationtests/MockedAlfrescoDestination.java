package eu.xenit.move2alf.integrationtests;

import eu.xenit.move2alf.core.sharedresource.alfresco.*;
import eu.xenit.move2alf.repository.UploadResult;
import eu.xenit.move2alf.repository.alfresco.ws.Document;

import java.io.File;
import java.util.List;

/**
 * Created by mhgam on 24/03/2016.
 */
public class MockedAlfrescoDestination extends AlfrescoHttpSharedResource {
    @Override
    public String putContent(File file, String mimeType) {
        return null;
    }

    @Override
    public List<UploadResult> sendBatch(WriteOption docExistsMode, List<Document> documents) {
        return null;
    }

    @Override
    public void setACL(ACL acl) {

    }

    @Override
    public boolean exists(String remotePath, String name) {
        return false;
    }

    @Override
    public void delete(String remotePath, String name, DeleteOption option) {

    }

    @Override
    public boolean fileNameExists(String name) {
        return false;
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
