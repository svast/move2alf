package eu.xenit.move2alf.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.PropertyData;

public final class CMISHelper {


    public static final String CMIS_FOLDER = "cmis:folder";
    public static final String CMIS_DOCUMENT = "cmis:document";
    public final static String CMIS_OBJECT_TYPE_ID = "cmis:objectTypeId";

    private CMISHelper() {
    }

    public static Map<String, Object> filterCMISProperties(Map<String, Object> properties) {
        Map<String, Object> result = new HashMap<String, Object>(properties.size());
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (entry.getKey().startsWith("cmis:")) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static Map<String, Object> objectProperties(CmisObject cmisObject) {
        List<Property<?>> propertyList = cmisObject.getProperties();
        return propertyDataToMap(propertyList);
    }

    public static Map<String, Object> propertyDataToMap(List<? extends PropertyData<?>> properties) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (PropertyData<?> propertyData : properties) {
            result.put(propertyData.getId(), propertyData.getFirstValue());
        }
        return result;
    }

    public static boolean isFolder(CmisObject cmisObject) {
        return CMIS_FOLDER.equals(getObjectTypeId(cmisObject));
    }

    public static boolean isDocument(CmisObject cmisObject) {
        return CMIS_DOCUMENT.equals(getBaseTypeId(cmisObject));
    }

    public static Object getObjectTypeId(CmisObject child) {
        return child.getPropertyValue(PropertyIds.OBJECT_TYPE_ID); //BASE_TYPE_ID?
    }

    public static Object getBaseTypeId(CmisObject child) {
        return child.getPropertyValue(PropertyIds.BASE_TYPE_ID);
    }

}