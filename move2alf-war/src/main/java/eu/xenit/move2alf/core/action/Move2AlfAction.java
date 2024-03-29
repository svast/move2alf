package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.logic.DefaultErrorHandler;
import eu.xenit.move2alf.logic.ErrorHandler;
import eu.xenit.move2alf.pipeline.actions.AbstractSendingAction;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/7/13
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Move2AlfAction extends AbstractSendingAction implements Parameterized {

    protected Map<String,String> parameters;

    public Move2AlfAction(){
        errorHandler = new DefaultErrorHandler(false);
    }

    private ErrorHandler errorHandler;
    public void setErrorHandler(ErrorHandler errorHandler){
        this.errorHandler = errorHandler;
    }

    protected void handleError(Object message, Exception e){
        errorHandler.handleError(getId(), message, e, sendingContext);
    }

    protected void handleError(Object message, String error){
        errorHandler.handleError(getId(), message, error, sendingContext);
    }

    protected void handleInfo(Object message, String info){
        errorHandler.handleInfo(getId(), message, info, sendingContext);
    }

    protected void handleWarn(Object message, String warning){
        errorHandler.handleWarn(getId(), message, warning, sendingContext);
    }



    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    protected String getParameter(String key) {
        return parameters.get(key);
    }

    protected Map<String,String> getParameters() {
        return parameters;
    }
}
