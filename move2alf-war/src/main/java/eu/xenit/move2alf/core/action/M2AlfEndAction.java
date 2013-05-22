package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.ApplicationContextProvider;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.pipeline.actions.AbstractEndingAction;
import eu.xenit.move2alf.pipeline.actions.AbstractSendingAction;
import eu.xenit.move2alf.pipeline.actions.AbstractStateAction;
import eu.xenit.move2alf.pipeline.actions.EOCAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ActionInfo(classId = "M2AlfEndAction",
            description = "Action that ends the cycle")
public class M2AlfEndAction extends AbstractStateAction implements EOCAware {

    private static final Logger logger = LoggerFactory.getLogger(M2AlfEndAction.class);

    private JobService jobService = (JobService) ApplicationContextProvider.getApplicationContext().getBean("jobService");

    @Override
    public void beforeSendEOC() {
        try {
            jobService.closeCycle((Integer) getStateValue(StartCycleAction.PARAM_CYCLE));
        } catch (Exception e) {
            logger.error("Error", e);
        }
    }
}
