package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.simpleaction.SACMISInput;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.repository.alfresco.ws.WebServiceRepositoryAccessSession;
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
import java.util.*;

import static eu.xenit.move2alf.common.Parameters.PARAM_ACL;
import static eu.xenit.move2alf.common.Parameters.PARAM_NAMESPACE;

@ClassInfo(classId = "CMISMetadataAction",
            category = ConfigurableObject.CAT_METADATA,
            description = "Metadata is copied from the source.")
public class CMISMetadataAction extends Move2AlfReceivingAction<FileInfo> {
    private static final Logger logger = LoggerFactory.getLogger(CMISMetadataAction.class);

    public final static String CMIS_OBJECT_TYPE_ID = "cmis:objectTypeId";
    public static final Map<String,String> mappingAuditables;
    static {
        mappingAuditables = new HashMap();
        mappingAuditables.put("cmis:createdBy", "creator");
        mappingAuditables.put("cmis:lastModifiedBy", "modifier");
        mappingAuditables.put("cmis:creationDate", "created");
        mappingAuditables.put("cmis:lastModificationDate","modified");
    }

    @Override
    protected void executeImpl(FileInfo fileInfo) {
        logger.debug("Entering executeImpl in CMISMetadataAction");
        Map<String, Object> headers = (Map)fileInfo.get(SACMISInput.PARAM_CAMEL_HEADER);
        String path = ((String)fileInfo.get(Parameters.PARAM_RELATIVE_PATH));
        if(!path.endsWith("/"))
            path = path.concat("/");
        path = path.concat(((File)fileInfo.get(Parameters.PARAM_FILE)).getName());

        String type = (String)headers.get(CMIS_OBJECT_TYPE_ID);
        // parse string of the form D:namespace:content type for custm Alfresco types
        int idx1 = type.indexOf(":");
        int idx2 = type.lastIndexOf(":");
        if(!(type.equals("cmis:document")) && idx1!=-1) {
            String namespace = type.substring(idx1+1,idx2);
            String newContentType = type.substring(idx2+1);
            fileInfo.put(Parameters.PARAM_CONTENTTYPE,newContentType);
            fileInfo.put(Parameters.PARAM_NAMESPACE,Parameters.mappingNamespaces.get(namespace));
        }
        fileInfo.remove(SACMISInput.PARAM_CAMEL_HEADER);

        AccessControlListImpl acli = (AccessControlListImpl)headers.get(CamelCMISConstants.CAMEL_CMIS_ACL);
        Map<String, Map<String, String>> acl = new HashMap<String, Map<String, String>>();
        HashMap<String,String> acList = new HashMap<String,String>();
        for(Ace ace : acli.getAces()) {
            // only put the "direct" permissions
            // we cant know the value of "inheritPermissions", so we don't set it at all (default=false)
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
                // set auditable properties
                if(isAuditable(property.getId())) {
                    String ns = property.getId().substring(0,property.getId().indexOf(":"));
                    String val = property.getValueAsString();
                    if(property.getValue() instanceof Calendar) {
                        val = Util.ISO8601format(((Calendar)property.getValue()).getTime());
                    }
                    props.put(mappingAuditables.get(property.getId()),val);
                } else if("cm:description".equals(property.getId())){
                    fileInfo.put(Parameters.PARAM_DESCRIPTION,property.getValueAsString());
                } else if(!property.getId().startsWith("cmis:") && !property.getLocalName().contains("nodeRef") && property.getValueAsString()!=null &&
                        !property.getValueAsString().isEmpty() && !"null".equals(property.getValueAsString())) {
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

    private boolean isAuditable(String id) {
        return mappingAuditables.keySet().contains(id);
    }
}
