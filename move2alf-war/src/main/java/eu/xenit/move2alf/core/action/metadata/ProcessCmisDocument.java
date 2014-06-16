package eu.xenit.move2alf.core.action.metadata;

import com.google.common.io.ByteStreams;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.logic.PipelineAssemblerImpl;
import eu.xenit.move2alf.pipeline.actions.AbstractSendingAction;
import eu.xenit.move2alf.pipeline.actions.context.SendingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static eu.xenit.move2alf.common.Parameters.*;
import static eu.xenit.move2alf.common.Parameters.PARAM_CMIS_PROPERTIES;
import static eu.xenit.move2alf.common.Parameters.PARAM_NAME;

/**
 * Created with IntelliJ IDEA.
 * User: rox
 * Date: 2/10/14
 * Time: 3:19 PM
 */
public class ProcessCmisDocument {
     private static final Logger logger = LoggerFactory.getLogger(ProcessCmisDocument.class);

    public static void processDocument(String cmisObjectId, String cmisName, String folderPath, File tempFolder, InputStream in, Map properties, boolean skipContentUpload, SendingContext context) {
        boolean failed = false;

        String uuid = extractUuid(cmisObjectId);
        logger.debug("Processing document " + uuid + " skipContentUpload=" + skipContentUpload);

        File file = null;
        if(!skipContentUpload) {
            file = new File(tempFolder, uuid);
            try {
                ByteStreams.copy(in, new FileOutputStream(file));
                in.close();
            } catch (FileNotFoundException e) {
                throw new Move2AlfException(e);
            } catch (IOException e) {
                Set<ProcessedDocumentParameter> params = new HashSet<ProcessedDocumentParameter>();
                ProcessedDocumentParameter parameter = new ProcessedDocumentParameter();
                parameter.setName(PARAM_ERROR_MESSAGE);
                parameter.setValue(e.toString());
                params.add(parameter);
                ReportMessage reportMessage = new ReportMessage(file.getName(),
                        new Date(),
                        VALUE_FAILED,
                        params,
                        null);
                context.sendMessage(reportMessage, PipelineAssemblerImpl.REPORTER);
                failed = true;
            } catch (NullPointerException e) {
                logger.debug("Empty message body (no content), will continue anyway!");
            }
        }

        if (!failed) {
            final FileInfo fileInfo = new FileInfo();
            fileInfo.put(PARAM_RELATIVE_PATH, folderPath);
            fileInfo.put(PARAM_FILE, file);
            fileInfo.put(PARAM_NAME, cmisName);
            fileInfo.put(PARAM_CMIS_PROPERTIES, properties);
            context.sendMessage(fileInfo);
        }
    }

    private static String extractUuid(String cmisObjectId) {
        // for Alfresco, ids contain the space e.g. workspace/SpacesStore; remove it
        int idx = cmisObjectId.lastIndexOf("/");
        if(idx != -1)
            return cmisObjectId.substring(cmisObjectId.lastIndexOf("/"));
        else
            return cmisObjectId;
    }
}
