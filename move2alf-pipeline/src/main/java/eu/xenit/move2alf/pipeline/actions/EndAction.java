package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.AbstractMessage;
import eu.xenit.move2alf.pipeline.actions.context.EndActionContext;
import eu.xenit.move2alf.pipeline.actions.context.StateContext;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/25/13
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EndAction<T extends AbstractMessage>{

    public void executeImpl(T message, StateContext context);

}
