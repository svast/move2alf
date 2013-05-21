package eu.xenit.move2alf.core.simpleaction;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.ApplicationContextProvider;
import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.core.action.ActionInfo;
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import eu.xenit.move2alf.core.action.StartCycleAction;
import eu.xenit.move2alf.core.action.messages.FileInfoMessage;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.logic.JobService;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ActionInfo(classId = "SAReport",
            description = "Writes report messages to the database")
public class SAReport extends Move2AlfReceivingAction<Object> {

	public String getDescription() {
		return "Checking for errors";
	}

    private JobService jobService = (JobService) ApplicationContextProvider.getApplicationContext().getBean("jobService");

    @Override
    public void executeImpl(Object message) {
        Cycle cycle = jobService.getCycle((Integer) getStateValue(StartCycleAction.PARAM_CYCLE));
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
