package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.logic.JobService;
import org.springframework.beans.factory.annotation.Autowired;

@ClassInfo(classId = "StartCycleAction",
            description = "Action that starts a cycle")
public class StartCycleAction extends Move2AlfStartAction {

    @Autowired
    private JobService jobService;

    public static String PARAM_CYCLE = "CYCLE";

    @Override
    public void onStartImpl() {
        int cycleId = jobService.openCycleForJob(stateContext.getJobId());
        setState(PARAM_CYCLE, jobService.getCycle(cycleId));
    }

}
