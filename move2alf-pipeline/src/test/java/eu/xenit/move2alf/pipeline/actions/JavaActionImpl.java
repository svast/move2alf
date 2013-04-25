package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.AbstractMessage;
import eu.xenit.move2alf.pipeline.StringMessage;
import eu.xenit.move2alf.pipeline.actions.context.SendingActionContext;
import eu.xenit.move2alf.pipeline.actions.context.SendingContext;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/9/13
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class JavaActionImpl<T extends AbstractMessage, U extends AbstractMessage> implements BasicAction<T, U> {

    private String param1;
    public void setParam1(String param1){
        this.param1 = param1;
    }

    @Override
    public void executeImpl(T message, SendingContext<U> context) {

    }
}
