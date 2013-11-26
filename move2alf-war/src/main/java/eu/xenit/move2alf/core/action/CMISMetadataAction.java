package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.common.Mappings;
import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.simpleaction.SACMISInput;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.chemistry.opencmis.client.runtime.PropertyImpl;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlListImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

@ClassInfo(classId = "CMISMetadataAction",
            category = ConfigurableObject.CAT_METADATA,
            description = "Metadata is copied from the source.")
public class CMISMetadataAction extends Move2AlfReceivingAction<FileInfo> {
    private static final Logger logger = LoggerFactory.getLogger(CMISMetadataAction.class);

    public final static String CMIS_OBJECT_TYPE_ID = "cmis:objectTypeId";

    @Override
    protected void executeImpl(FileInfo fileInfo) {
        Map<String, Object> headers = (Map)fileInfo.get(SACMISInput.PARAM_CAMEL_HEADER);
        String path = ((String)fileInfo.get(Parameters.PARAM_RELATIVE_PATH));
        if(!path.endsWith("/"))
            path = path.concat("/");
        path = path.concat(Parameters.PARAM_NAME);

        fileInfo.remove(SACMISInput.PARAM_CAMEL_HEADER);

        String type = (String)headers.get(CMIS_OBJECT_TYPE_ID);
        logger.debug("Setting type and namespace for " + type + " for " + fileInfo.get(Parameters.PARAM_FILE));

        String alfrescoType = Mappings.mappingTypes.get(type);
        if(alfrescoType == null)
            alfrescoType = type;
        // custom Alfresco types: D:namespace:content
        if(!(alfrescoType.equals("cmis:document"))) {
            if(alfrescoType.matches("D:([^:]+):(.+)")) {
                int idx1 = alfrescoType.indexOf(":");
                int idx2 = alfrescoType.lastIndexOf(":");
                String namespace = alfrescoType.substring(idx1+1,idx2);
                logger.debug("namespace=" + namespace + " with mapping=" + Mappings.mappingNamespaces.get(namespace));
                if(Mappings.mappingNamespaces.get(namespace)!=null)
                    fileInfo.put(Parameters.PARAM_NAMESPACE, Mappings.mappingNamespaces.get(namespace));
                String newContentType = alfrescoType.substring(idx2+1);
                fileInfo.put(Parameters.PARAM_CONTENTTYPE,newContentType);
            } else { // Sharepoint types: 0x01010B006DBEC9104B358340916826B490EC2063, mapped to sharepoint:dublin_core_columns
                int idx1 = alfrescoType.indexOf(":");
                String namespace = alfrescoType.substring(0,idx1);
                if(Mappings.mappingNamespaces.get(namespace)!=null)
                    fileInfo.put(Parameters.PARAM_NAMESPACE, Mappings.mappingNamespaces.get(namespace));
                String newContentType = alfrescoType.substring(idx1+1);
                fileInfo.put(Parameters.PARAM_CONTENTTYPE,newContentType);
            }
        }


        logger.debug("Setting properties for " + fileInfo.get(Parameters.PARAM_FILE));
        HashMap props = new HashMap();
        String contentStreamProp = "", mimeTypeProp = "", contentLengthProp = "", encodingProp = "", localeProp = "";

        if(headers.get(CamelCMISConstants.CAMEL_CMIS_PROPERTIES)!=null) {
            Collection<PropertyImpl> properties = (Collection<PropertyImpl>)headers.get(CamelCMISConstants.CAMEL_CMIS_PROPERTIES);
            for(PropertyImpl property : properties) {
                String val = property.getValueAsString();
                if(val==null)
                    continue;
                if(property.getValue() instanceof Calendar) {
                    val = Util.ISO8601format(((Calendar)property.getValue()).getTime());
                } else {  // for creator and modifier remove domain name (present in e.g. sharepoint)
                    val = normalize(val);
                }
                // set auditable properties
                if(isAuditable(property.getId())) {
                    props.put(Mappings.mappingAuditables.get(property.getId()),val);
                }
                // set parameters which are put directly in fileInfo
                else if("cm:description".equals(property.getId())) {
                    fileInfo.put(Parameters.PARAM_DESCRIPTION,val);
                } else if("cmis:contentStreamMimeType".equals(property.getId())) {
                    fileInfo.put(Parameters.PARAM_MIMETYPE,val);
                    mimeTypeProp = val;
                } else if("cmis:contentStreamId".equals(property.getId())) {
                    contentStreamProp = val;
                } else if("cmis:contentStreamLength".equals(property.getId())) {
                    contentLengthProp = val;
                }
                // set the rest of parameters
                else if(!(property.getId().startsWith("cmis:")) &&
                          !(property.getLocalName().contains("nodeRef")) &&
                          valid(val)) {
                    // add the property with the namespace, if namespace is present
                    if(property.getId().indexOf(":")!=-1) {
                        String ns = property.getId().substring(0,property.getId().indexOf(":"));
                        String fullns = Mappings.mappingNamespaces.get(ns);
                        logger.debug("fullns for " + ns + " is " + fullns);
                        if(fullns !=null)
                            props.put(fullns+property.getLocalName(),val);
                     } else {
                        props.put(property.getLocalName(),val);
                    }
                }
            }
        }

<<<<<<< HEAD

	    /*        logger.debug("Setting acls");
        AccessControlListImpl acli = (AccessControlListImpl)headers.get(CamelCMISConstants.CAMEL_CMIS_ACL);
        Map<String, Map<String, String>> acl = new HashMap<String, Map<String, String>>();
        HashMap<String,String> acList = new HashMap<String,String>();
        for(Ace ace : acli.getAces()) {
            // only put the "direct" permissions
            // we cant know the value of "inheritPermissions", so we don't set it at all (default=false)
            int s = ace.getPermissions().size();
            if(s > 0 && ace.isDirect()) {
                String permission = Mappings.mappingPermissions.get(ace.getPermissions().get(s-1));
                if(permission==null)
                    permission = ace.getPermissions().get(s-1);
                String user = Mappings.mappingUsers.get(ace.getPrincipalId());
                if(user==null)
                    user=ace.getPrincipalId();
                if(!user.isEmpty())
                    acList.put(normalize(user),permission);
            }
        }
        acl.put(path,acList);
        fileInfo.put(Parameters.PARAM_ACL,acl);*/

        fileInfo.put(Parameters.PARAM_CONTENTURL,buildContentUrl(contentStreamProp,mimeTypeProp,contentLengthProp));

=======
        fileInfo.put(Parameters.PARAM_CONTENTURL,buildContentUrl(contentStreamProp,mimeTypeProp,contentLengthProp));
>>>>>>> 1) Cleaning: remove deprecated or not used methods and tests.
        fileInfo.put(Parameters.PARAM_METADATA,props);
        logger.info("fileInfo=" + fileInfo.get(Parameters.PARAM_NAME));
        sendMessage(fileInfo);
    }

    private String buildContentUrl(String contentStreamProp, String mimeTypeProp, String contentLengthProp) {
        return "contentUrl=" + contentStreamProp + "|mimetype=" + mimeTypeProp + "|size=" + contentLengthProp + "|encoding=UTF-8";
    }

    private boolean valid(String val) {
          return (val !=null &&
                  !("null".equals(val) &&
                  !(val.isEmpty())));
    }

    // remove domain
    private String normalize(String val) {
        int idx = val.indexOf('\\');
        if(idx != -1)
          val = val.substring(idx+1);
        return val;
    }

    private boolean isAuditable(String id) {
        return Mappings.mappingAuditables.keySet().contains(id);
    }
}
