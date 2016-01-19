package eu.xenit.move2alf.core.sharedresource.alfresco;

import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.model.*;
import eu.xenit.move2alf.repository.UploadResult;
import eu.xenit.move2alf.repository.alfresco.ws.Document;
import eu.xenit.move2alf.services.AlfrescoService;
import eu.xenit.move2alf.services.AlfrescoServiceImpl;
import eu.xenit.move2alf.services.http.CredentialData;
import eu.xenit.service.JsonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Created by Stan on 08-Jan-16.
 */
public class AlfrescoHttpSharedResource extends AbstractAlfrescoSharedResource {
    private final Logger logger = LoggerFactory.getLogger(AlfrescoHttpSharedResource.class);

    @Autowired
    @Qualifier("xenit.service.jsonservice")
    private JsonService jsonService;

//    @Autowired
//    @Qualifier("xenit.service.alfrescoservice")
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

    }

    private AlfrescoService getAlfrescoService(){
        if (this.alfrescoService == null){
            try {
                URI uri = new URI(url);
                CredentialData credentialData = new CredentialData(uri.getHost(), uri.getPort(), super.user, super.password);

                this.alfrescoService = new AlfrescoServiceImpl(this.jsonService, Collections.singletonList(credentialData));
            }
            catch (URISyntaxException e){
                logger.error(e.getMessage(), e);
                // error in forming uri
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
                //e.printStackTrace();
                // error in creating alfresco service
            }
        }

        return this.alfrescoService;
    }

    @Override
    public String putContent(File file, String mimeType) {
        try {

            Future<String> future = this.getAlfrescoService().putContent(super.url + AlfrescoService.ALFRESCO_URL_PUT_CONTENT, file, mimeType, "UTF-8");

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
                Move2AlfRequest request = this.documentToRequest(document);
                request.setCreatePath(true);
                request.setMode(Mode.WRITE);
                if (docExistsMode.equals(WriteOption.OVERWRITE)) {

                    request.setOverWrite(Boolean.TRUE);
                }

                uploadData.add(request);
            }

            Future<List<Move2AlfResponse>> response = this.getAlfrescoService().postMetadata(super.url + AlfrescoService.ALFRESCO_URL_POST_METADATA, uploadData);

            List<UploadResult> result = new ArrayList<>();
            List<Move2AlfResponse> get = response.get();
            for (int i = 0; i < get.size(); i++) {
                Move2AlfResponse move2AlfResponse = get.get(i);
                Document document = documents.get(i);
                result.add(this.responseToUploadResult(move2AlfResponse, document));
            }

            return result;
        }
        catch (Exception e){
            throw new Move2AlfException(e.getMessage(), e);
        }

    }

    private Move2AlfRequest documentToRequest(Document document){
        Move2AlfRequest request = new Move2AlfRequest();

        request.setId(document.name);
        request.setType(document.contentModelNamespace + document.contentModelType);
        request.setContentUrl(document.contentUrl);
        request.setPath(document.spacePath);

        List<Property> properties = new ArrayList<>();
        // cm:name
        properties.add(new Property("cm:name", document.name));
        // properties
        if (document.meta != null) {
            for (Map.Entry<String, String> prop : document.meta.entrySet()) {
                Property property = new Property(prop.getKey(), prop.getValue());
                properties.add(property);
            }
        }
        // multivalue properties
        if (document.multiValueMeta != null) {
            for (Map.Entry<String, String> prop : document.multiValueMeta.entrySet()) {
                Property property = new Property(prop.getKey(), prop.getValue());
                properties.add(property);
            }
        }

        request.setProperties(properties);

        return request;
    }

    private UploadResult responseToUploadResult(Move2AlfResponse response, Document document){
        UploadResult result = new UploadResult();

        result.setMessage(response.getMessage());
        result.setReference(response.getNodeRef());

        if (response.getStatus().equals(Status.SUCCESS)){
            result.setStatus(UploadResult.VALUE_OK);
        }
        else{
            result.setStatus(UploadResult.VALUE_FAILED);
        }

        result.setDocument(document);

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
        return this.getAlfrescoService() != null;
    }


}
