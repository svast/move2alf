package eu.xenit.move2alf.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Set;

public class ReportFactory {
	private static final ReportFactory instance = new ReportFactory();
	
	private Map<String, Report> reportMap = new HashMap<String, Report>();
	 
    public static ReportFactory getInstance() {
        return instance;
    }

    private ReportFactory() {
    	rescanReports();
    }
 
    public void rescanReports(){
    	//TODO scan for available report classes and store an instance ()
    }
    
    public Collection<Report> getReportCollection(){
      return reportMap.values();
    }
    
    public Set<String> getReportClassNames(){
      return reportMap.keySet();	
    }
    
    public Report getReport(String className){
    	return reportMap.get(className);
    }
    	
}
