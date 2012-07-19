package eu.xenit.move2alf;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.logic.SchedulerImpl;

@Ignore
public class ImmediateTriggerTest extends IntegrationTests{
	
	private JobService jobService;
	
	@Autowired
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	public JobService getJobService() {
		return jobService;
	}

}
