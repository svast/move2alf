package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.actions.context.SendingContext;
import eu.xenit.move2alf.pipeline.actions.context.StateContext;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/9/13
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class JavaActionImpl<T> implements ReceivingAction<T>, HasStateContext, HasSendingContext{

    @Override
    public void setStateContext(StateContext stateContext) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private String param1;
    public void setParam1(String param1){
        this.param1 = param1;
    }

    public void setParam2(List<String> strings){

    }

    @Override
    public void execute(T message) {

    }

    @Override
    public void setSendingContext(SendingContext sendingContext) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setId(String id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
