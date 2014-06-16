package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.pipeline.actions.StartAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/21/13
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Move2AlfStartAction extends Move2AlfAction implements StartAware{

    private static final Logger logger = LoggerFactory.getLogger(Move2AlfStartAction.class);

    @Override
    public void onStart() {
        try{
            onStartImpl();
        } catch (Exception e){
            logger.error("Exception in Onstart", e);
            handleError("OnStart", e);
        }
    }

    protected abstract void onStartImpl();
}
