package eu.xenit.move2alf.web.controller.destination.model;

import eu.xenit.move2alf.web.dto.DestinationConfig;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/4/13
 * Time: 4:35 PM
 */
public class AlfrescoDestinationModel extends DestinationConfig{

    @NotEmpty
    private String destinationURL;

    @NotEmpty
    private String alfUser;

    @NotEmpty
    private String alfPswd;

    public void setDestinationURL(String destinationURL) {
        this.destinationURL = destinationURL;
    }

    public String getDestinationURL() {
        return destinationURL;
    }

    public void setAlfUser(String alfUser) {
        this.alfUser = alfUser;
    }

    public String getAlfUser() {
        return alfUser;
    }

    public void setAlfPswd(String alfPswd) {
        this.alfPswd = alfPswd;
    }

    public String getAlfPswd() {

        return alfPswd;
    }

    private int contentStoreId = -1;

    public int getContentStoreId() {
        return contentStoreId;
    }

    public void setContentStoreId(int contentStoreId) {
        this.contentStoreId = contentStoreId;
    }
}
