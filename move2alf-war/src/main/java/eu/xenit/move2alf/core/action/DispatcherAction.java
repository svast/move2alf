package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.logic.PipelineAssemblerImpl;
import eu.xenit.move2alf.pipeline.actions.EOCAware;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: rox
 * Date: 5/26/15
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
@ClassInfo(classId = "DispatcherAction",
        description = "Sends messages to multiple actors")
public class DispatcherAction extends Move2AlfReceivingAction {
    private static ArrayList<String> receivers = new ArrayList();
    static {
        receivers.add(PipelineAssemblerImpl.MOVE_AFTER_ID);
        receivers.add(PipelineAssemblerImpl.MOVE_NOT_LOADED_ID);
    }

    @Override
    protected void executeImpl(Object message) {
        for(String key : receivers) {
            if(sendingContext.hasReceiver(key)) {
                sendMessage(key,message);
            }
        }
    }
}
