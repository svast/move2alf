package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.ApplicationContextProvider;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.pipeline.actions.AbstractSendingAction;
import eu.xenit.move2alf.pipeline.actions.StartAware;
import eu.xenit.move2alf.pipeline.actions.context.StateContext;

@ActionInfo(classId = "StartCycleAction",
            description = "Action that starts a cycle")
public class StartCycleAction extends Move2AlfStartAction {

    public static String PARAM_JOBSERVICE = "jobService";
    private JobService jobService = (JobService) ApplicationContextProvider.getApplicationContext().getBean("jobService");

    public static String PARAM_CYCLE = "CYCLE";

    @Override
    public void onStartImpl() {
        int cycleId = jobService.openCycleForJob(stateContext.getJobId());
        setState(PARAM_CYCLE, jobService.getCycle(cycleId));
    }

}
