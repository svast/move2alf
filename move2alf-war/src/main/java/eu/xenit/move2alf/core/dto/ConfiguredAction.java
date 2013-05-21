package eu.xenit.move2alf.core.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.xenit.move2alf.core.ConfiguredObject;

public class ConfiguredAction extends ConfiguredObject {

    private Map<String, ConfiguredAction> receivers;
    private int nmbOfWorkers;
    private String actionId;

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
        if(receivers == null){
            receivers = new HashMap<String, ConfiguredAction>();
        }
        receivers.put(key, receiver);
    }

    public int getNmbOfWorkers(){
        return nmbOfWorkers;
    }

    public void setNmbOfWorkers(int nmbOfWorkers){
        this.nmbOfWorkers = nmbOfWorkers;
    }
}
