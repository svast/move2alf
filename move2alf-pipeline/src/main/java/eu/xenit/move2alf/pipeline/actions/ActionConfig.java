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

    public Map<String, Object> getParameters() {
        return parameters;
    }

    private Map<String, Object> parameters = new TreeMap<String, Object>();

    public void setParameter(String key, Object value){
        parameters.put(key, value);
    }

    public Class getClazz() {
        return clazz;
    }

    public int getNmbOfWorkers() {
        return nmbOfWorkers;
    }

    private Class clazz;
    private int nmbOfWorkers;

    public String getId() {
        return id;
    }

    private String id;

    public ActionConfig(String id, Class clazz, int nmbOfWorkers){
        this.id = id;
        this.clazz = clazz;
        this.nmbOfWorkers = nmbOfWorkers;
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

}
