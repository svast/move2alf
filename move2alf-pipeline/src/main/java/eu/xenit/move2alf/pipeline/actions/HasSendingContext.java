package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.actions.context.SendingContext;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/26/13
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates.
 */
public interface HasSendingContext extends Action{
    void setSendingContext(SendingContext sendingContext);
}
