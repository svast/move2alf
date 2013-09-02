package eu.xenit.move2alf.common;

import java.util.HashMap;
import java.util.Map;

/* Useful for CMIS import */
public class Mappings {
    public static final Map<String,String> mappingNamespaces = new HashMap();
    public static final Map<String,String> mappingAuditables = new HashMap();
    public static final Map<String,String> mappingPermissions = new HashMap();
    public static final Map<String,String> mappingUsers=new HashMap();
    public static final Map<String,String> mappingTypes=new HashMap();

    static {
        mappingAuditables.put("cmis:createdBy", "creator");
        mappingAuditables.put("cmis:lastModifiedBy", "modifier");
        mappingAuditables.put("cmis:creationDate", "created");
        mappingAuditables.put("cmis:lastModificationDate","modified");

        mappingPermissions.put("cmis:all", "{http://www.alfresco.org/model/content/1.0}cmobject.Coordinator");
        mappingPermissions.put("cmis:write", "{http://www.alfresco.org/model/content/1.0}cmobject.Contributor");
        mappingPermissions.put("cmis:read", "{http://www.alfresco.org/model/content/1.0}cmobject.Consumer");
        mappingPermissions.put("Read", "{http://www.alfresco.org/model/content/1.0}cmobject.Consumer");

        mappingUsers.put("Home Members","");
        mappingUsers.put("Home Visitors","GROUP_EVERYONE");
        mappingUsers.put("Home Owners","");

        mappingNamespaces.put("cm","{http://www.alfresco.org/model/content/1.0}");
        mappingNamespaces.put("fred","{http://www.xenit.eu/fred/example/model/0.1}");
        mappingNamespaces.put("sharepoint","{http://www.xenit.eu/sharepoint/example/model/0.1}");

        mappingTypes.put("0x01010B006DBEC9104B358340916826B490EC2063","sharepoint:dublin_core_columns");
    }
}
