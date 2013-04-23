package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.AbstractMessage;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/5/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BasicAction<T extends AbstractMessage, U extends AbstractMessage> {
    private ActionWrapper<T, U> simpleAction;

    public BasicAction(){
    }

    abstract public void executeImpl(T message);

    final protected Object getStateValue(String key){
        return simpleAction.getStateValue(key);
    }

    final protected void setStateValue(String key, Object value){
        simpleAction.setStateValue(key, value);
    }

    final protected void sendMessage(U message){
        simpleAction.sendMessage(message);
    }

    final public void setWrapper(ActionWrapper<T,U> wrapper){
        if(simpleAction == null) {
            this.simpleAction = wrapper;
        } else {
            throw new RuntimeException("The wrapper should only be set once");
        }
    }

}
