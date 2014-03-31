package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.common.CMISHelper;
import eu.xenit.move2alf.common.Mappings;
import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.simpleaction.SACMISInput;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import org.apache.chemistry.opencmis.client.runtime.CmisBindingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@ClassInfo(classId = "CMISMetadataAction",
            category = ConfigurableObject.CAT_METADATA,
            description = "Metadata is copied from the source.")
public class CMISMetadataAction extends Move2AlfReceivingAction<FileInfo> {
    private static final Logger logger = LoggerFactory.getLogger(CMISMetadataAction.class);



    @Override
    protected void executeImpl(FileInfo fileInfo) {
        HashMap<String, Object> headers = new HashMap();
        headers.putAll((Map)(fileInfo.get(Parameters.PARAM_CMIS_PROPERTIES)));
        String path = ((String)fileInfo.get(Parameters.PARAM_RELATIVE_PATH));
        if(!path.endsWith("/"))
            path = path.concat("/");
        path = path.concat(Parameters.PARAM_NAME);

        fileInfo.remove(Parameters.PARAM_CMIS_PROPERTIES);
        String type = (String)headers.get(CMISHelper.CMIS_OBJECT_TYPE_ID);
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

        for(String property : headers.keySet()) {
            Object val = headers.get(property);
            if(val==null)
                continue;
            if(val instanceof Calendar) {
                val = Util.ISO8601format(((Calendar)val).getTime());
            } else {  // for creator and modifier remove domain name (present in e.g. sharepoint)
                val = normalize(val.toString());
            }
            // set auditable properties
            if(isAuditable(property)) {
                props.put(Mappings.mappingAuditables.get(property),val);
            }
            // set parameters which are put directly in fileInfo
            else if("cm:description".equals(property)) {
                fileInfo.put(Parameters.PARAM_DESCRIPTION,val);
            } else if("cmis:contentStreamMimeType".equals(property)) {
                fileInfo.put(Parameters.PARAM_MIMETYPE,val);
                mimeTypeProp = (String)val;
            } else if("cmis:contentStreamId".equals(property)) {
                contentStreamProp = (String)val;
            } else if("cmis:contentStreamLength".equals(property)) {
                contentLengthProp = (String)val;
            }
            // set the rest of parameters
            else if(!(property.startsWith("cmis:")) &&
                    !(property.contains("nodeRef")) &&
                    !(property.contains("breadcrumb")) &&
                    valid((String)val)) {
                // add the property with the namespace, if namespace is present
                if(property.indexOf(":")!=-1) {
                    String ns = property.substring(0,property.indexOf(":"));
                    String localProperty = property.substring(property.indexOf(":")+1);
                    String fullns = Mappings.mappingNamespaces.get(ns);
                    logger.debug("fullns for " + ns + " is " + fullns + " for property " + localProperty);
                    if(fullns !=null)
                        props.put(fullns+localProperty,val);
                } else {
                    props.put(property,val);
                }
            }
        }


        fileInfo.put(Parameters.PARAM_CONTENTURL,buildContentUrl(contentStreamProp,mimeTypeProp,contentLengthProp));
        fileInfo.put(Parameters.PARAM_METADATA,props);
        logger.info("fileInfo=" + fileInfo);
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
