package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.actions.context.SendingContext;
import eu.xenit.move2alf.pipeline.actions.context.StateContext;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/30/13
 * Time: 1:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class DummyStartAction extends AbstractSendingAction implements StartAware {

    @Override
    public void setSendingContext(SendingContext sendingContext) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setStateContext(StateContext stateContext) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onStart() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setId(String id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
