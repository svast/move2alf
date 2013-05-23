package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.logic.PipelineAssemblerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ActionInfo(classId = "UploadedFileHandler",
            description = "Constructs a ReportMessage and routes to the reporter. Also routes to move-not-loaded and moveloaded")
public class UploadedFileHandler extends Move2AlfReceivingAction<FileInfo> {
    
    private static final Logger logger = LoggerFactory.getLogger(UploadedFileHandler.class);

    @Override
    protected void executeImpl(FileInfo fileInfo) {
        String status = (String)fileInfo.get(Parameters.PARAM_STATUS);
        Set<ProcessedDocumentParameter> params = createProcessedDocumentParameterSet(
                (Map<String, String>) fileInfo
                        .get(Parameters.PARAM_REPORT_FIELDS));
        if(fileInfo.get(Parameters.PARAM_STATUS).equals(Parameters.VALUE_FAILED)){
            ProcessedDocumentParameter parameter = new ProcessedDocumentParameter();
            parameter.setName(Parameters.PARAM_ERROR_MESSAGE);
            parameter.setValue((String) fileInfo.get(Parameters.PARAM_ERROR_MESSAGE));
            params.add(parameter);
        }

        logger.debug("Number of params: "+params.size());

        ReportMessage reportMessage = new ReportMessage(((File)fileInfo.get(Parameters.PARAM_FILE)).getName(),
                new Date(),
                status,
                params,
                (String)fileInfo.get(Parameters.PARAM_REFERENCE));
        sendMessage(PipelineAssemblerImpl.REPORTER, reportMessage);

        if(sendingContext.hasReceiver(PipelineAssemblerImpl.MOVE_NOT_LOADED_ID) && status == Parameters.VALUE_FAILED){
            sendMessage(PipelineAssemblerImpl.MOVE_NOT_LOADED_ID, fileInfo);
        }

        if(sendingContext.hasReceiver(PipelineAssemblerImpl.MOVE_AFTER_ID) && status == Parameters.VALUE_OK){
            sendMessage(PipelineAssemblerImpl.MOVE_AFTER_ID, fileInfo);
        }

    }

    Set<ProcessedDocumentParameter> createProcessedDocumentParameterSet(
            Map<String, String> reportFields) {
        Set<ProcessedDocumentParameter> procDocParameters = new HashSet<ProcessedDocumentParameter>();
        if (reportFields != null) {
            for (String key : reportFields.keySet()) {
                ProcessedDocumentParameter procDocParameter = new ProcessedDocumentParameter();
                procDocParameter.setName(key);
                String value = reportFields.get(key);
                if (value.length() > 255) {
                    value = value.substring(0, 255);
                }
                procDocParameter.setValue(value);
                procDocParameters.add(procDocParameter);
            }
        }
        return procDocParameters;
    }
}
