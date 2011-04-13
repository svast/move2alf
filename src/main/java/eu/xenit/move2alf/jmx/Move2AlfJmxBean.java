package eu.xenit.move2alf.jmx;

import java.util.ArrayList;
import java.util.List;

import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.logic.JobService;

public class Move2AlfJmxBean implements IMove2AlfJmxBean {
	private JobService jobService;

	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	public JobService getJobService() {
		return jobService;
	}

	public String[] getJobs() {
		List<String> jobNames = new ArrayList<String>();
		for(Job job : getJobService().getAllJobs()) {
			jobNames.add(job.getName());
		}
		return jobNames.toArray(new String[0]);
	}
}