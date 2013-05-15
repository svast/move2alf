package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.logic.ErrorHandler;
import eu.xenit.move2alf.pipeline.AbstractMessage;
import eu.xenit.move2alf.pipeline.actions.AbstractBasicAction;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/7/13
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Move2AlfAction<T extends AbstractMessage> extends AbstractBasicAction<T> {
    
    @Override
    public final void execute(T message){
        try{
            executeImpl(message);
        } catch (Exception e){
            handleError(message, e);
        }
    }

    protected abstract void executeImpl(T message);

    private ErrorHandler errorHandler;
    public void setErrorHandler(ErrorHandler errorHandler){
        this.errorHandler = errorHandler;
    }

    protected void handleError(AbstractMessage message, Exception e){
        errorHandler.handleError(message, e, sendingContext);
    }

    protected void handleError(AbstractMessage message, String error){
        errorHandler.handleError(message, error, sendingContext);
    }

    public String getDescription() {
        return this.getClass().getSimpleName();
    }
}
