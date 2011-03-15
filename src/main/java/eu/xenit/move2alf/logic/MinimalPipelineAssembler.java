package eu.xenit.move2alf.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.web.dto.JobConfig;

@Service("pipelineAssembler")
public class MinimalPipelineAssembler extends PipelineAssembler {

	private static final Logger logger = LoggerFactory
			.getLogger(MinimalPipelineAssembler.class);

	@Override
	public void assemblePipeline(JobConfig jobConfig) {
		assemble(
				jobConfig,
				action("eu.xenit.move2alf.core.action.SourceAction")
						.param("path", jobConfig.getInputFolder())
						.param("recursive", "true")
						.sourceSink(
								sourceSink("eu.xenit.move2alf.core.sourcesink.FileSourceSink")),
				action("eu.xenit.move2alf.core.action.ThreadAction"),
				action("eu.xenit.move2alf.core.action.SinkAction")
						.param("path", jobConfig.getDestinationFolder())
						.sourceSink(
								sourceSinkById(Integer.parseInt(jobConfig.getDest()))));
	}
	
	@Override
	public JobConfig getJobConfigForJob(int id) {
		Job job = getJobService().getJob(id);
		JobConfig jobConfig = new JobConfig();
		jobConfig.setId(id);
		jobConfig.setName(job.getName());
		jobConfig.setDescription(job.getDescription());
		ConfiguredAction action = job.getFirstConfiguredAction();
		String inputFolder = "";
		String destinationFolder = "";
		String dest = "";
		while(action != null) {
			if ("eu.xenit.move2alf.core.action.SourceAction".equals(action.getClassName())) {
				inputFolder = action.getParameter("path");
			} else if ("eu.xenit.move2alf.core.action.SinkAction".equals(action.getClassName())) {
				destinationFolder = action.getParameter("path");
				dest = action.getConfiguredSourceSinkSet().iterator().next().getIdAsString();
			}
			action = action.getAppliedConfiguredActionOnSuccess();
		}
		jobConfig.setInputFolder(inputFolder);
		jobConfig.setDestinationFolder(destinationFolder);
		jobConfig.setDest(dest);
		return jobConfig;
	}
}
