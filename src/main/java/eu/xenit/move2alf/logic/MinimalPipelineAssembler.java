package eu.xenit.move2alf.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
				action("eu.xenit.move2alf.core.action.ThreadAction"), action(
						"eu.xenit.move2alf.core.action.SinkAction").sourceSink(
						sourceSinkById(Integer.parseInt(jobConfig.getDest()))));
	}
}
