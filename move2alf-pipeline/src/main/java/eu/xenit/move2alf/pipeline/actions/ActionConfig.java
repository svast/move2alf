package eu.xenit.move2alf.pipeline.actions;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
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
