package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.actions.context.SendingContext;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/6/13
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSendingAction extends AbstractStateAction implements HasSendingContext {

    protected SendingContext sendingContext;
    @Override
    public void setSendingContext(SendingContext sendingContext) {
        this.sendingContext = sendingContext;
    }

    protected void sendMessage(Object message){
        sendingContext.sendMessage(message);
    }

    protected void sendMessage(String receiver, Object message){
        sendingContext.sendMessage(message, receiver);
    }
}
