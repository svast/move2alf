package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.simpleaction.SACMISInput;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.runtime.PropertyImpl;
import org.apache.chemistry.opencmis.client.runtime.objecttype.PolicyTypeImpl;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlListImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CmisExtensionElementImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ExtensionDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static eu.xenit.move2alf.common.Parameters.PARAM_ACL;
import static eu.xenit.move2alf.common.Parameters.PARAM_NAMESPACE;

@ClassInfo(classId = "CMISMetadataAction",
            category = ConfigurableObject.CAT_METADATA,
            description = "Metadata is copied from the source.")
public class CMISMetadataAction extends Move2AlfReceivingAction<FileInfo> {
    private static final Logger logger = LoggerFactory.getLogger(CMISMetadataAction.class);

    public final static String CREATED = "created";
    public final static String MODIFIED = "modified";
    public final static String CREATOR = "creator";
    public final static String MODIFIER = "modifier";
    public final static String TITLE = "title";
    public final static String DESCRIPTION = "description";
    public final static String LANGUAGE = "language";
    public final static String CATEGORIES = "categories";

    public final static String CMIS_OBJECT_TYPE_ID = "cmis:objectTypeId";
    public final static String CMIS_CREATOR = "cmis:createdBy";
    public final static String CMIS_MODIFIED = "cmis:lastModificationDate";
    public final static String CMIS_CREATED = "cmis:creationDate";
    public final static String CMIS_MODIFIER = "cmis:lastModifiedBy";

    public final static String CMIS_TITLE = "Title";
    public final static String CMIS_DESCRIPTION = "_Comments";
    public final static String CMIS_LANGUAGE = "Language";
    public final static String CMIS_KEYWORDS = "Keywords";

    private final static HashMap<String,String> mappingProperties = new HashMap();

    static {
  //      mappingProperties.put(CMIS_MODIFIER, MODIFIER);
//        mappingProperties.put(CMIS_CREATOR, CREATOR);
        mappingProperties.put(CMIS_TITLE, TITLE);
        mappingProperties.put(CMIS_DESCRIPTION, DESCRIPTION);
    }

    @Override
    protected void executeImpl(FileInfo fileInfo) {
        logger.debug("Entering executeImpl in CMISMetadataAction");
        Map<String, Object> headers = (Map)fileInfo.get(SACMISInput.PARAM_CAMEL_HEADER);
        String path = ((String)fileInfo.get(Parameters.PARAM_RELATIVE_PATH));
        if(!path.endsWith("/"))
            path = path.concat("/");
        path = path.concat(((File)fileInfo.get(Parameters.PARAM_FILE)).getName());

        logger.debug("path=" + path);

        for(String cmiskey : mappingProperties.keySet()) {
            String value = (String)headers.get(cmiskey);
            if(value != null) {
                value = value.replace("\\","");
                fileInfo.put(mappingProperties.get(cmiskey),value);
            }
        }

        String type = (String)headers.get(CMIS_OBJECT_TYPE_ID);
        // parse string of the form D:namespace:content type for custm Alfresco types
        int idx1 = type.indexOf(":");
        int idx2 = type.lastIndexOf(":");
        if(type.equals("cmis:document") || idx1==-1) {
            fileInfo.put(Parameters.PARAM_CONTENTTYPE,"content");
            fileInfo.put(Parameters.PARAM_NAMESPACE, "{http://www.alfresco.org/model/content/1.0}");
        } else {
            String namespace = type.substring(idx1+1,idx2);
            String newContentType = type.substring(idx2+1);
            fileInfo.put(Parameters.PARAM_CONTENTTYPE,newContentType);
            //fileInfo.put(Parameters.PARAM_NAMESPACE,namespace);
            // TO DO: how to handle this more generically??
            fileInfo.put(Parameters.PARAM_NAMESPACE,Parameters.mappingNamespaces.get(namespace));
        }
        fileInfo.remove(SACMISInput.PARAM_CAMEL_HEADER);

        AccessControlListImpl acli = (AccessControlListImpl)headers.get(CamelCMISConstants.CAMEL_CMIS_ACL);
        Map<String, Map<String, String>> acl = new HashMap<String, Map<String, String>>();
        HashMap<String,String> acList = new HashMap<String,String>();
        for(Ace ace : acli.getAces()) {
            // only put the "direct" permissions
            // we cant know the value of "inheritPErmissions", so we don't set it at all (default=false)
            if(ace.getPermissions().size()>0 && ace.isDirect()) {
                acList.put(ace.getPrincipalId(),ace.getPermissions().get(ace.getPermissions().size()-1));
            }
        }
        acl.put(path,acList);
        fileInfo.put(Parameters.PARAM_ACL,acl);

        HashMap props = new HashMap();
        if(headers.get(CamelCMISConstants.CAMEL_CMIS_PROPERTIES)!=null) {
            Collection<PropertyImpl> properties = (Collection<PropertyImpl>)headers.get(CamelCMISConstants.CAMEL_CMIS_PROPERTIES);
            for(PropertyImpl property : properties) {
                //logger.debug("property id=" + property.getId() + " and local name=" + property.getLocalName() + " and value=" + property.getValueAsString() + " and displayName " + property.getDisplayName() + " in general=" + property);
                if(!property.getId().startsWith("cmis:") && !property.getLocalName().contains("nodeRef") && property.getValueAsString()!=null && !property.getValueAsString().isEmpty()) {
                    // add the property both with and without namespace
                    props.put(property.getLocalName(),property.getValueAsString());
                    String ns = property.getId().substring(0,property.getId().indexOf(":"));
                    props.put(Parameters.mappingNamespaces.get(ns)+property.getLocalName(),property.getValueAsString());
                }
            }
        }

        fileInfo.put(Parameters.PARAM_METADATA,props);
        logger.info("*******************fileInfo=" + fileInfo);
        sendMessage(fileInfo);
    }

}
