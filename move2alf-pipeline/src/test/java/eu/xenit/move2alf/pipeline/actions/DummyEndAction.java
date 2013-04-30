package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.StringMessage;
import eu.xenit.move2alf.pipeline.actions.context.StateContext;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/30/13
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class DummyEndAction implements ReceivingAction<StringMessage>, HasStateContext{

    @Override
    public void setStateContext(StateContext stateContext) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void execute(StringMessage message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
