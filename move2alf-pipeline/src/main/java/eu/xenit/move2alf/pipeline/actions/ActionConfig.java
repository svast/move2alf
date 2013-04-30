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

    public String getClazz() {
        return clazz;
    }

    public int getNmbOfWorkers() {
        return nmbOfWorkers;
    }

    private String clazz;
    private int nmbOfWorkers;

    public String getId() {
        return id;
    }

    private String id;

    public ActionConfig(String id, String clazz, int nmbOfWorkers){
        this.id = id;
        this.clazz = clazz;
        this.nmbOfWorkers = nmbOfWorkers;
    }

    private List<ActionConfig> receivers = new ArrayList<ActionConfig>();

    public void addReceiver(ActionConfig value){
        receivers.add(value);
    }

    public List<ActionConfig> getReceivers(){
        return receivers;
    }

}
