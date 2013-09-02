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
        logger.debug("Entering executeImpl in CMISMetadataAction");
        Map<String, Object> headers = (Map)fileInfo.get(SACMISInput.PARAM_CAMEL_HEADER);
        String path = ((String)fileInfo.get(Parameters.PARAM_RELATIVE_PATH));
        if(!path.endsWith("/"))
            path = path.concat("/");
        path = path.concat(((File)fileInfo.get(Parameters.PARAM_FILE)).getName());

        fileInfo.remove(SACMISInput.PARAM_CAMEL_HEADER);

        logger.debug("Setting type and namespace");
        String type = (String)headers.get(CMIS_OBJECT_TYPE_ID);
        String alfrescoType = Mappings.mappingTypes.get(type);
        if(alfrescoType == null)
            alfrescoType = type;
        // custom Alfresco types: D:namespace:content
        if(!(alfrescoType.equals("cmis:document"))) {
            if(alfrescoType.matches("D:([^:]+):(.+)")) {
                int idx1 = alfrescoType.indexOf(":");
                int idx2 = alfrescoType.lastIndexOf(":");
                String namespace = alfrescoType.substring(idx1+1,idx2);
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

        logger.debug("Setting acls");
        AccessControlListImpl acli = (AccessControlListImpl)headers.get(CamelCMISConstants.CAMEL_CMIS_ACL);
        Map<String, Map<String, String>> acl = new HashMap<String, Map<String, String>>();
        HashMap<String,String> acList = new HashMap<String,String>();
        for(Ace ace : acli.getAces()) {
            // only put the "direct" permissions
            // we cant know the value of "inheritPermissions", so we don't set it at all (default=false)
            if(ace.getPermissions().size()>0 && ace.isDirect()) {
                String permission = Mappings.mappingPermissions.get(ace.getPermissions().get(ace.getPermissions().size()-1));
                if(permission==null)
                    permission = ace.getPermissions().get(ace.getPermissions().size() - 1);
                String user = Mappings.mappingUsers.get(ace.getPrincipalId());
                if(user==null)
                    user=ace.getPrincipalId();
                if(!user.isEmpty())
                    acList.put(user,permission);
            }
        }
        acl.put(path,acList);
        fileInfo.put(Parameters.PARAM_ACL,acl);

        logger.debug("Setting properties");
        HashMap props = new HashMap();
        if(headers.get(CamelCMISConstants.CAMEL_CMIS_PROPERTIES)!=null) {
            Collection<PropertyImpl> properties = (Collection<PropertyImpl>)headers.get(CamelCMISConstants.CAMEL_CMIS_PROPERTIES);
            for(PropertyImpl property : properties) {
                // set auditable properties
                if(isAuditable(property.getId())) {
                    String ns = property.getId().substring(0,property.getId().indexOf(":"));
                    String val = property.getValueAsString();
                    if(property.getValue() instanceof Calendar) {
                        val = Util.ISO8601format(((Calendar)property.getValue()).getTime());
                    }
                    props.put(Mappings.mappingAuditables.get(property.getId()),val);
                } else if("cm:description".equals(property.getId())){
                    fileInfo.put(Parameters.PARAM_DESCRIPTION,property.getValueAsString());
                } else if(!property.getId().startsWith("cmis:") && !property.getLocalName().contains("nodeRef") && property.getValueAsString()!=null &&
                        !property.getValueAsString().isEmpty() && !"null".equals(property.getValueAsString())) {
                    // add the property with the namespace, if namespace is present
                    if(property.getId().indexOf(":")!=-1) {
                        String ns = property.getId().substring(0,property.getId().indexOf(":"));
                        String fullns = Mappings.mappingNamespaces.get(ns);
                        if(fullns !=null)
                            props.put(Mappings.mappingNamespaces.get(ns)+property.getLocalName(),property.getValueAsString());
                    } else {
                        props.put(property.getLocalName(),property.getValueAsString());
                    }
                }
            }
        }


        fileInfo.put(Parameters.PARAM_METADATA,props);
        logger.info("*******************fileInfo=" + fileInfo);
        sendMessage(fileInfo);
    }

    private boolean isAuditable(String id) {
        return Mappings.mappingAuditables.keySet().contains(id);
    }
}
