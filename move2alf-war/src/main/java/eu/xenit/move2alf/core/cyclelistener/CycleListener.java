package eu.xenit.move2alf.core.cyclelistener;

import java.util.Map;

import eu.xenit.move2alf.logic.JobService;

public abstract class CycleListener {
	private JobService jobService;
	
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}
	
	public JobService getJobService() {
		return jobService;
	}
	
	public abstract void cycleStart(int cycleId);
	
	public void cycleStart(int cycleId, Map<String, Object> parameterMap) {
		cycleStart(cycleId);
	}
	
	public abstract void cycleEnd(int cycleId);
}
