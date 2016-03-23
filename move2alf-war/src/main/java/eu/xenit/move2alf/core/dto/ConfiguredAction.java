package eu.xenit.move2alf.core.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.xenit.move2alf.core.ConfiguredObject;

public class ConfiguredAction extends ConfiguredObject {

    private Map<String, ConfiguredAction> receivers =new HashMap<String, ConfiguredAction>();
    private int nmbOfWorkers;
    private String actionId;
    private String dispatcher;

    public ConfiguredAction() {
    }

    public ConfiguredAction(String actionId, String dispatcher, int nmbOfWorkers, String classId, Map parameters) {
        this.actionId = actionId;
        this.dispatcher = dispatcher;
        this.nmbOfWorkers = nmbOfWorkers;
        setClassId(classId);
        setParameters(parameters);
    }

    public String getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(String dispatcher) {
        this.dispatcher = dispatcher;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public Map<String, ConfiguredAction> getReceivers() {
        return receivers;
    }

    public void setReceivers(Map<String, ConfiguredAction> receivers) {
        this.receivers = receivers;
    }

    public void addReceiver(String key, ConfiguredAction receiver){
        /*if(receivers == null){
            receivers = new HashMap<String, ConfiguredAction>();
        }*/
        receivers.put(key, receiver);
    }

    public int getNmbOfWorkers(){
        return nmbOfWorkers;
    }

    public void setNmbOfWorkers(int nmbOfWorkers){
        this.nmbOfWorkers = nmbOfWorkers;
    }
}
