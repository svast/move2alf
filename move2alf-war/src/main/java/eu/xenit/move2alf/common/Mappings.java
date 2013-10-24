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
        mappingPermissions.put("Limited Access", "{http://www.alfresco.org/model/content/1.0}cmobject.Consumer");

        mappingUsers.put("Home Members","");
        mappingUsers.put("Home Visitors","GROUP_EVERYONE");
        mappingUsers.put("Home Owners","");

        // can these be acquired automatically?
        mappingNamespaces.put("cm","{http://www.alfresco.org/model/content/1.0}");
        mappingNamespaces.put("dl","{http://www.alfresco.org/model/datalist/1.0}");
        mappingNamespaces.put("act","{http://www.alfresco.org/model/action/1.0}");
        mappingNamespaces.put("app","{http://www.alfresco.org/model/application/1.0}");
        mappingNamespaces.put("blg","{http://www.alfresco.org/model/blogintegration/1.0}");
        mappingNamespaces.put("bpm","{http://www.alfresco.org/model/bpm/1.0}");
        mappingNamespaces.put("bpm","{http://www.alfresco.org/model/rendition/1.0}");
        mappingNamespaces.put("exif","{http://www.alfresco.org/model/exif/1.0}");
        mappingNamespaces.put("audio","{http://www.alfresco.org/model/audio/1.0}");
        mappingNamespaces.put("webdav","{http://www.alfresco.org/model/webdav/1.0}");
        mappingNamespaces.put("cmis","{http://www.alfresco.org/model/cmis/1.0/cs01}");
        mappingNamespaces.put("cmisext","{http://www.alfresco.org/model/cmis/1.0/cs01ext}");
        mappingNamespaces.put("alfcmis","{http://www.alfresco.org/model/cmis/1.0/alfcmis}");
        mappingNamespaces.put("cmiscustom","{http://www.alfresco.org/model/cmis/custom}");
        mappingNamespaces.put("emailserver","{http://www.alfresco.org/model/emailserver/1.0}");
        mappingNamespaces.put("facebook","{http://www.alfresco.org/model/publishing/facebook/1.0}");
        mappingNamespaces.put("flickr","{http://www.alfresco.org/model/publishing/flickr/1.0}");
        mappingNamespaces.put("fm","{http://www.alfresco.org/model/forum/1.0}");
        mappingNamespaces.put("gd","{http://www.alfresco.org/model/googledocs/1.0}");
        mappingNamespaces.put("ia","{http://www.alfresco.org/model/calendar}");
        mappingNamespaces.put("imap","{http://www.alfresco.org/model/imap/1.0}");
        mappingNamespaces.put("imwf","{http://www.alfresco.org/model/workflow/invite/moderated/1.0}");
        mappingNamespaces.put("inwf","{http://www.alfresco.org/model/workflow/invite/nominated/1.0}");
        mappingNamespaces.put("jcr","{http://www.jcp.org/jcr/1.0}");
        mappingNamespaces.put("nt","{http://www.jcp.org/jcr/nt/1.0}");
        mappingNamespaces.put("mix","{http://www.jcp.org/jcr/mix/1.0}");
        mappingNamespaces.put("sv","{http://www.jcp.org/jcr/sv/1.0}");
        mappingNamespaces.put("xml","{http://www.w3.org/XML/1998/namespace}");
        mappingNamespaces.put("linkedin","{http://www.alfresco.org/model/publishing/linkedin/1.0}");
        mappingNamespaces.put("lnk","{http://www.alfresco.org/model/linksmodel/1.0}");
        mappingNamespaces.put("pub","{http://www.alfresco.org/model/publishing/1.0}");
        mappingNamespaces.put("pubwf","{http://www.alfresco.org/model/publishingworkflow/1.0}");
        mappingNamespaces.put("rc","{http://www.alfresco.org/model/remotecredentials/1.0}");
        mappingNamespaces.put("rule","{http://www.alfresco.org/model/rule/1.0}");
        mappingNamespaces.put("slideshare","{http://www.alfresco.org/model/publishing/slideshare/1.0}");
        mappingNamespaces.put("st","{http://www.alfresco.org/model/site/1.0}");
        mappingNamespaces.put("stcp","{http://www.alfresco.org/model/sitecustomproperty/1.0}");
        mappingNamespaces.put("sync","{http://www.alfresco.org/model/sync/1.0}");
        mappingNamespaces.put("sys","{http://www.alfresco.org/model/system/1.0}");
        mappingNamespaces.put("reg","{http://www.alfresco.org/system/registry/1.0}");
        mappingNamespaces.put("module","{http://www.alfresco.org/system/modules/1.0}");
        mappingNamespaces.put("trx","{http://www.alfresco.org/model/transfer/1.0}");
        mappingNamespaces.put("twitter","{http://www.alfresco.org/model/publishing/twitter/1.0}");
        mappingNamespaces.put("usr","{http://www.alfresco.org/model/user/1.0}");
        mappingNamespaces.put("ver2","{http://www.alfresco.org/model/versionstore/2.0}");
        mappingNamespaces.put("ver","{http://www.alfresco.org/model/versionstore/1.0}");
        mappingNamespaces.put("wca","{http://www.alfresco.org/model/wcmappmodel/1.0}");
        mappingNamespaces.put("wcm","{http://www.alfresco.org/model/wcmmodel/1.0}");
        mappingNamespaces.put("wcmwf","{http://www.alfresco.org/model/wcmworkflow/1.0}");
        mappingNamespaces.put("wf","{http://www.alfresco.org/model/workflow/1.0}");
        mappingNamespaces.put("youtube","{http://www.alfresco.org/model/publishing/youtube/1.0}");
        mappingNamespaces.put("fred","{http://www.xenit.eu/fred/example/model/0.1}");
        mappingNamespaces.put("demo","{http://www.xenit.eu/model/demo/1.0}");
        mappingNamespaces.put("sharepoint","{http://www.xenit.eu/sharepoint/example/model/0.1}");

        mappingTypes.put("0x01010B006DBEC9104B358340916826B490EC2063","sharepoint:dublin_core_columns");
    }
}
