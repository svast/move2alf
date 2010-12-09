package eu.xenit.move2alf.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Set;

public class SourceSinkFactory {
	private static final SourceSinkFactory instance = new SourceSinkFactory();
	
	private Map<String, SourceSink> sourceSinkMap = new HashMap<String, SourceSink>();
	 
    public static SourceSinkFactory getInstance() {
        return instance;
    }

    private SourceSinkFactory() {
    	rescanSourceSinks();
    }
 
    public void rescanSourceSinks(){
    	//TODO scan for available report classes and store an instance ()
    }
    
    public Collection<SourceSink> getSourceSinkCollection(){
      return sourceSinkMap.values();
    }
    
    public Set<String> getSourceSinkClassNames(){
      return sourceSinkMap.keySet();	
    }
    
    public SourceSink getSourceSink(String className){
    	return sourceSinkMap.get(className);
    }
    	
}
