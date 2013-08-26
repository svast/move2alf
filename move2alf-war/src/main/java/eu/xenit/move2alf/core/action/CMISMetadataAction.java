package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.action.metadata.MetadataLoader;
import eu.xenit.move2alf.core.simpleaction.SACMISInput;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ClassInfo(classId = "CopyMetadataAction",
            category = ConfigurableObject.CAT_METADATA,
            description = "Metadata is copied from the source. Only valid for CMIS import.")
public class CopyMetadataAction extends Move2AlfReceivingAction<FileInfo> {
    public final String CREATED = "created";
    public final String MODIFIED = "modified";
    public final String CREATOR = "creator";
    public final String MODIFIER = "modifier";

    public final String CMIS_CREATED = "cmis:createdBy";
    public final String CMIS_MODIFIED = "cmis:lastModificationDate";
    public final String CMIS_CREATOR = "cmis:creationDate";
    public final String CMIS_MODIFIER = "cmis:lastModifiedBy";

    @Override
    protected void executeImpl(FileInfo fileInfo) {
        Map<String, Object> headers = (Map)fileInfo.get(SACMISInput.PARAM_CAMEL_HEADER);
        for(String header : headers.keySet()) {
            String key = header;
            Object value = headers.get(key);
            if(CMIS_CREATED.equals(key)) {
                Calendar cCreated = (Calendar)value;
                String dCreated = Util.ISO8601format(cCreated.getTime());
                fileInfo.put(CREATED,dCreated);
            }
            if(CMIS_MODIFIED.equals(key)) {
                Calendar cModified = (Calendar)value;
                String dModified = Util.ISO8601format(cModified.getTime());
                fileInfo.put(CREATED,dModified);
            }
            if(CMIS_MODIFIER.equals(key)) {
                fileInfo.put(MODIFIER,(String)value);
            }
            if(CMIS_CREATOR.equals(key)) {
                fileInfo.put(CREATED,(String)value);
            }
        }

        fileInfo.remove(SACMISInput.PARAM_CAMEL_HEADER);

        System.out.println("fileInfo=" + fileInfo);
        sendMessage(fileInfo);
    }
}
