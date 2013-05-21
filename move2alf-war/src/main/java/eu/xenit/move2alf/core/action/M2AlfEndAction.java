package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.ApplicationContextProvider;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.pipeline.actions.AbstractEndingAction;
import eu.xenit.move2alf.pipeline.actions.AbstractSendingAction;
import eu.xenit.move2alf.pipeline.actions.AbstractStateAction;
import eu.xenit.move2alf.pipeline.actions.EOCAware;

@ActionInfo(classId = "M2AlfEndAction",
            description = "Action that ends the cycle")
public class M2AlfEndAction extends AbstractStateAction implements EOCAware {

    private JobService jobService = (JobService) ApplicationContextProvider.getApplicationContext().getBean("jobService");

    @Override
    public void beforeSendEOC() {
        jobService.closeCycle((Integer) getStateValue(StartCycleAction.PARAM_CYCLE));
    }
}
