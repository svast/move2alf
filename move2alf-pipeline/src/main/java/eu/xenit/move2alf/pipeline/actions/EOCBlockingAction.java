package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.actions.context.EOCBlockingContext;

/**
 *
 * User: thijs
 * Date: 5/3/13
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EOCBlockingAction extends Action{

    /**
     * Prevents sending eoc
     * @param eocBlockingContext
     */
    void setEOCBlockingContext(EOCBlockingContext eocBlockingContext);
}
