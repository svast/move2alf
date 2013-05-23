package eu.xenit.move2alf.core.simpleaction;

import eu.xenit.move2alf.core.ApplicationContextProvider;
import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.core.action.ActionInfo;
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import eu.xenit.move2alf.core.action.StartCycleAction;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.logic.JobService;

@ActionInfo(classId = "SAReport",
            description = "Writes report messages to the database")
public class SAReport extends Move2AlfReceivingAction<ReportMessage> {

	public String getDescription() {
		return "Checking for errors";
	}

    private JobService jobService = (JobService) ApplicationContextProvider.getApplicationContext().getBean("jobService");

    @Override
    public void executeImpl(ReportMessage reportMessage) {
        Cycle cycle = jobService.getCycle((Integer)getStateValue(StartCycleAction.PARAM_CYCLE));
        jobService.createProcessedDocument(cycle.getId(),
                reportMessage.name, reportMessage.date,
                reportMessage.state, reportMessage.params, reportMessage.reference);

    }
}
