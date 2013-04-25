package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.AbstractMessage;
import eu.xenit.move2alf.pipeline.actions.context.SendingContext;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/5/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BasicAction<T extends AbstractMessage, U extends AbstractMessage>{

    public void executeImpl(T message, SendingContext<U> context);

}
