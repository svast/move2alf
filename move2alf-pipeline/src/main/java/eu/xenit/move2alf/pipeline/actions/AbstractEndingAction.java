package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.AbstractMessage;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/6/13
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractEndingAction<T extends AbstractMessage> extends AbstractStateAction implements ReceivingAction<T> {
}
