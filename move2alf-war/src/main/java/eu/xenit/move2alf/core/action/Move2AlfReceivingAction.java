package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.pipeline.actions.ReceivingAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/21/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Move2AlfReceivingAction<T> extends Move2AlfAction implements ReceivingAction<T>{
    private static final Logger logger = LoggerFactory.getLogger(Move2AlfReceivingAction.class);

    @Override
    public void execute(T message) {
        try {
            executeImpl(message);
        } catch (Exception e){
            logger.error("Error in executeImpl " + e + " for message " + message, e);
            handleError(message, e);
        }
    }

    protected abstract void executeImpl(T message);
}
