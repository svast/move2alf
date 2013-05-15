package eu.xenit.move2alf.core.simpleaction;

import eu.xenit.move2alf.core.action.messages.StartMessage;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.pipeline.actions.AbstractBeginAction;
import eu.xenit.move2alf.pipeline.actions.HasStateContext;
import eu.xenit.move2alf.pipeline.actions.context.StateContext;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/8/13
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class M2AlfStartAction extends AbstractBeginAction implements HasStateContext {

    public static String PARAM_JOBSERVICE = "jobService";
    private JobService jobService;
    public void setJobService(JobService jobService){
        this.jobService = jobService;
    }

    public static String PARAM_CYCLE = "CYCLE";

    @Override
    public void execute() {
        int cycleId = jobService.openCycleForJob(Integer.parseInt(stateContext.getJobId()));
        stateContext.setStateValue(PARAM_CYCLE, cycleId);
        sendMessage(new StartMessage());
    }

    private StateContext stateContext;
    @Override
    public void setStateContext(StateContext stateContext){
        this.stateContext = stateContext;
    }

}
