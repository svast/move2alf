package eu.xenit.move2alf.core.simpleaction;

import eu.xenit.move2alf.core.action.messages.StartMessage;
import eu.xenit.move2alf.pipeline.actions.AbstractBeginAction;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/8/13
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class M2AlfStartAction extends AbstractBeginAction {
    @Override
    public void execute() {
        sendMessage(new StartMessage());
    }
}
