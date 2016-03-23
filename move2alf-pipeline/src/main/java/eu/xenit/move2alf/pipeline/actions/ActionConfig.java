package eu.xenit.move2alf.pipeline.actions;

import java.util.*;

/**
 * Defines a config for creating a number of identical actions, instantiated using factory
 * Actions can send (move2alf) message to other actions, defined using actionconfig's in receivers
 * All message sending between actions in move2alf is done on these receiver keys.
 * An actionconfig is a node is the move2alf message graph. Each node can only communicate to its receivers
 * User: thijs
 * Date: 4/29/13
 * Time: 3:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionConfig {

    public int getNmbOfWorkers() {
        return nmbOfWorkers;
    }

    private final int nmbOfWorkers;

    public String getId() {
        return id;
    }

    private final String id;

    public ActionFactory getActionFactory() {
        return actionFactory;
    }

    private final ActionFactory actionFactory;

    public ActionConfig(String id, ActionFactory actionFactory, int nmbOfWorkers){
        this.id = id;
        this.nmbOfWorkers = nmbOfWorkers;
        this.actionFactory = actionFactory;
    }

    private Map<String, ActionConfig> receivers = new TreeMap<String, ActionConfig>();

    /**
     *
     * @param key defines a name used as target to send messages
     * @param value
     */
    public void addReceiver(String key, ActionConfig value){
        if(receivers.containsKey(key)){
            throw new IllegalArgumentException("This config already has a receiver with key: "+key);
        }
        receivers.put(key, value);
    }


    public Map<String, ActionConfig> getReceivers(){
        return receivers;
    }

    public String getDispatcher() {
        return dispatcher;
    }

    private String dispatcher = null;
    public void setDispatcher(String dispatcher){
        this.dispatcher = dispatcher;
    }

}
