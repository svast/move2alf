package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.actions.context.StateContext;

/**
 * Each job has a global statecontext which can be accessed by any actor implementing hasstatecontext
 * User: thijs
 * Date: 4/26/13
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HasStateContext extends Action{
    void setStateContext(StateContext stateContext);
}
