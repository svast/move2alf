package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.AbstractMessage;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/6/13
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractBasicAction<T extends AbstractMessage> extends AbstractSendingAction implements ReceivingAction<T> {

}
