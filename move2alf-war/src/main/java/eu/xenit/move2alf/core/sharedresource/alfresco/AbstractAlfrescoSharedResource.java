package eu.xenit.move2alf.core.sharedresource.alfresco;

import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.sharedresource.SharedResource;
import eu.xenit.move2alf.repository.UploadResult;
import eu.xenit.move2alf.repository.alfresco.ws.Document;

import java.io.File;
import java.util.List;

/**
 * Created by Stan on 08-Jan-16.
 */
public abstract class AbstractAlfrescoSharedResource extends SharedResource {
    public static final String PARAM_URL = "url";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_USER = "user";

    protected String user;
    protected String password;
    protected String url;

    protected AbstractAlfrescoSharedResource() {
    }

    public abstract String putContent(File file, String mimeType);

    public abstract List<UploadResult> sendBatch(
            WriteOption docExistsMode, List<Document> documents);

    public abstract void setACL(ACL acl);

    public abstract boolean exists(String remotePath, String name);

    public abstract void delete(String remotePath, String name, DeleteOption option);

    public abstract boolean fileNameExists(String name);

    public abstract boolean validate();

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        if (url.endsWith("/")) {
            this.url = url;
        } else {
            this.url = url + "/";
        }
    }

    public String getCategory() {
        return ConfigurableObject.CAT_DESTINATION;
    }

    public abstract String getDescription();

    public abstract String getName();


}
