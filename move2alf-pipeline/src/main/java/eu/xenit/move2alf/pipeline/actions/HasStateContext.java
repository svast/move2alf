package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.actions.context.StateContext;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/26/13
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HasStateContext {
    public void setStateContext(StateContext stateContext);
}
