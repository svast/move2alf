package eu.xenit.move2alf.core.action;

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

    public String getDescription() {
        return this.getClass().getSimpleName();
    }
}
