package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.actions.context.StateContext;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/6/13
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractStateAction implements HasStateContext {

    protected StateContext stateContext;
    protected String id;

    @Override
    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    @Override
    public void setStateContext(StateContext stateContext) {
       this.stateContext = stateContext;
    }

    protected void setState(String key, Object value){
        stateContext.setStateValue(key, value);
    }

    protected Object getStateValue(String key){
        return stateContext.getStateValue(key);
    }
}
