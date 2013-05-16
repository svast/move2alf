package eu.xenit.move2alf.core.simpleaction;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.core.action.M2AlfStartAction;
import eu.xenit.move2alf.core.action.Move2AlfAction;
import eu.xenit.move2alf.core.action.messages.FileInfoMessage;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.pipeline.AbstractMessage;

import java.io.File;
import java.util.*;

public class SAReport extends Move2AlfAction<AbstractMessage> {

	public String getDescription() {
		return "Checking for errors";
	}


    public static String PARAM_JOBSERVICE = "jobService";
    private JobService jobService;
    public void setJobService(JobService jobService){
        this.jobService = jobService;
    }

    @Override
    public void executeImpl(AbstractMessage message) {
        Cycle cycle = jobService.getCycle((Integer) getStateValue(M2AlfStartAction.PARAM_CYCLE));
        if(message instanceof FileInfoMessage){
            // reporting
            FileInfoMessage fileInfoMessage = (FileInfoMessage) message;
            Set<ProcessedDocumentParameter> params = createProcessedDocumentParameterSet(
                    (Map<String, String>) fileInfoMessage.fileInfo
                            .get(Parameters.PARAM_REPORT_FIELDS),cycle
                    .getJob().getFirstConfiguredAction());
            jobService.createProcessedDocument(cycle.getId(),
                    ((File)fileInfoMessage.fileInfo.get(Parameters.PARAM_FILE)).getName(), new Date(),
                    Parameters.VALUE_OK, params, (String)fileInfoMessage.fileInfo.get(Parameters.PARAM_REFERENCE));
        }
        if (message instanceof ReportMessage) {
            ReportMessage reportMessage = (ReportMessage) message;
            jobService.createProcessedDocument(cycle.getId(),
                    reportMessage.name, reportMessage.date,
                    reportMessage.state, reportMessage.params, reportMessage.reference);
        }
    }

    Set<ProcessedDocumentParameter> createProcessedDocumentParameterSet(
            Map<String, String> reportFields, ConfiguredAction configuredAction) {
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
                procDocParameter.setConfiguredAction(configuredAction);
                procDocParameters.add(procDocParameter);
            }
        }
        return procDocParameters;
    }
}
