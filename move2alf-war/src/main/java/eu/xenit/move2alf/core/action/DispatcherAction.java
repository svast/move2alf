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
        description = "Sends messages to all receivers")
public class DispatcherAction extends Move2AlfReceivingAction {

    @Override
    protected void executeImpl(Object message) {
        sendingContext.broadcastPublic(message);
    }
}
