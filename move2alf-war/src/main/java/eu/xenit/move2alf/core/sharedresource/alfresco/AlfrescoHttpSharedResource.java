package eu.xenit.move2alf.core.sharedresource.alfresco;

import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.model.Move2AlfRequest;
import eu.xenit.move2alf.model.Move2AlfResponse;
import eu.xenit.move2alf.model.Property;
import eu.xenit.move2alf.model.Status;
import eu.xenit.move2alf.repository.UploadResult;
import eu.xenit.move2alf.repository.alfresco.ws.Document;
import eu.xenit.move2alf.services.AlfrescoService;
import eu.xenit.move2alf.services.http.CredentialData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by Stan on 08-Jan-16.
 */
public class AlfrescoHttpSharedResource extends AbstractAlfrescoSharedResource {

    @Autowired
    @Qualifier("xenit.service.alfrescoservice")
    private AlfrescoService alfrescoService;

    @Override
    public String getName() {
        return "AlfrescoHttp";
    }

    @Override
    public String getDescription() {
        return "Alfresco destination through http";
    }

    public AlfrescoHttpSharedResource() {
        super();
        init();

    }

    private void init() {
        String url = super.url;
        CredentialData credentialData;

        try{
            URI uri = new URI(url);
            credentialData = new CredentialData(uri.getHost(), uri.getPort(), super.user, super.password);
        }
        catch (Exception e){
            credentialData = null;
        }

        try {
            this.alfrescoService.init(Arrays.asList(credentialData));
        }
        catch (Exception e){
            throw new Move2AlfException(e.getMessage(), e);
        }

    }

    @Override
    public String putContent(File file, String mimeType) {
        try {

            Future<String> future = this.alfrescoService.putContent(super.url + AlfrescoService.ALFRESCO_URL_PUT_CONTENT, file, mimeType, "UTF-8");

            return future.get();
        }
        catch (Exception e){
            throw new Move2AlfException(e.getMessage(), e);
        }
    }

    @Override
    public List<UploadResult> sendBatch(WriteOption docExistsMode, List<Document> documents) {
        try {
            List<Move2AlfRequest> uploadData = new ArrayList<>();
            for (Document document : documents) {
                uploadData.add(this.documentToRequest(document));
            }

            Future<List<Move2AlfResponse>> response = this.alfrescoService.postMetadata(super.url + AlfrescoService.ALFRESCO_URL_POST_METADATA, uploadData);

            List<UploadResult> result = new ArrayList<>();
            for(Move2AlfResponse move2AlfResponse : response.get()){
                result.add(this.responseToUploadResult(move2AlfResponse));
            }

            return result;
        }
        catch (Exception e){
            throw new Move2AlfException(e.getMessage(), e);
        }

    }

    private Move2AlfRequest documentToRequest(Document document){
        Move2AlfRequest request = new Move2AlfRequest();

        request.setContentUrl(document.contentUrl);
        request.setPath(document.spacePath);

        List<Property> properties = new ArrayList<>();
        // cm:name
        properties.add(new Property("cm:name", document.name));
        // properties
        for(Map.Entry<String, String> prop : document.meta.entrySet()){
            Property property = new Property(prop.getKey(), prop.getValue());
            properties.add(property);
        }
        // multivalue properties
        for(Map.Entry<String, String> prop : document.multiValueMeta.entrySet()){
            Property property = new Property(prop.getKey(), prop.getValue());
            properties.add(property);
        }

        request.setProperties(properties);

        return request;
    }

    private UploadResult responseToUploadResult(Move2AlfResponse response){
        UploadResult result = new UploadResult();

        result.setMessage(response.getMessage());
        result.setReference(response.getNodeRef());

        if (response.getStatus().equals(Status.SUCCESS)){
            result.setStatus(UploadResult.VALUE_OK);
        }
        else{
            result.setStatus(UploadResult.VALUE_FAILED);
        }

        return result;
    }

    @Override
    public void setACL(ACL acl) {
        throw new UnsupportedOperationException("Method not yet implemented.");

    }

    @Override
    public boolean exists(String remotePath, String name) {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }

    @Override
    public void delete(String remotePath, String name, DeleteOption option) {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }

    @Override
    public boolean fileNameExists(String name) {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }

    @Override
    public boolean validate() {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }


}
