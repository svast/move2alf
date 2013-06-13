package eu.xenit.move2alf.core.simpleaction.helpers;

import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import eu.xenit.move2alf.logic.DestinationService;
import eu.xenit.move2alf.pipeline.actions.AcceptsReply;
import eu.xenit.move2alf.pipeline.actions.EOCBlockingAction;
import eu.xenit.move2alf.pipeline.actions.context.EOCBlockingContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public abstract class ActionWithDestination<T> extends Move2AlfReceivingAction<T> implements EOCBlockingAction, AcceptsReply {

    @Autowired
    private DestinationService destinationService;

    public static final String PARAM_DESTINATION = "destination";
	private int destination;
    public void setDestination(String dest){
        this.destination = Integer.parseInt(dest);
    }

    public int getDestination(){
        return destination;
    }

    private Map<String, Object> messages = new HashMap<String, Object>();

    protected void sendTaskToDestination(Object message){
        if(messages.size() ==  0){
            eocBlockingContext.blockEOC();
        }
        String key = Integer.toString(message.hashCode());
        destinationService.sendTaskToDestination(destination, key, message, stateContext.getActorRef());
        messages.put(key, message);
    }

    @Override
    public void acceptReply(String key, Object message) {
        if(message instanceof Exception){
            handleError(messages.get(key), (Exception) message);
        } else {
            acceptReplyImpl(messages.get(key), message);
        }
        messages.remove(key);
        if(messages.size() == 0){
            eocBlockingContext.unblockEOC();
        }
    }

    protected abstract void acceptReplyImpl(Object task, Object response);

    private EOCBlockingContext eocBlockingContext;

    @Override
    public void setEOCBlockingContext(EOCBlockingContext eocBlockingContext) {
        this.eocBlockingContext = eocBlockingContext;
    }
}