package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.web.dto.JobConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class PipelineAssembler extends AbstractHibernateService {

	private static final Logger logger = LoggerFactory
			.getLogger(PipelineAssembler.class);

	private JobService jobService;

	public abstract eu.xenit.move2alf.pipeline.actions.ActionConfig getPipeline(JobConfig jobConfig);


	@Autowired
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	public JobService getJobService() {
		return jobService;
	}

	public abstract JobConfig getJobConfigForJob(int id);

    public abstract void assemblePipeline(JobConfig jobConfig);
}
