package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.logic.DefaultErrorHandler;
import eu.xenit.move2alf.logic.ErrorHandler;
import eu.xenit.move2alf.pipeline.actions.AbstractSendingAction;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/7/13
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Move2AlfAction extends AbstractSendingAction {

    private ErrorHandler errorHandler = new DefaultErrorHandler(false);
    public void setErrorHandler(ErrorHandler errorHandler){
        this.errorHandler = errorHandler;
    }

    protected void handleError(Object message, Exception e){
        errorHandler.handleError(getId(), message, e, sendingContext);
    }

    protected void handleError(Object message, String error){
        errorHandler.handleError(getId(), message, error, sendingContext);
    }

}
