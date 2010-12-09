package eu.xenit.move2alf.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Set;

public class ActionFactory {
	private static final ActionFactory instance = new ActionFactory();
	
	private Map<String, Action> actionMap = new HashMap<String, Action>();
	 
    public static ActionFactory getInstance() {
        return instance;
    }

    private ActionFactory() {
    	rescanActions();
    }
 
    public void rescanActions(){
    	//TODO scan for available action classes and store an instance ()
    }
    
    public Collection<Action> getActionCollection(){
      return actionMap.values();
    }
    
    public Set<String> getActionClassNames(){
      return actionMap.keySet();	
    }
    
    public Action getAction(String className){
    	return actionMap.get(className);
    }
    	
}
