package eu.xenit.move2alf.logic;

import org.springframework.stereotype.Service;

import eu.xenit.move2alf.web.dto.JobConfig;

@Service("pipelineAssembler")
public class MinimalPipelineAssembler extends PipelineAssembler {

	@Override
	public void assemblePipeline(JobConfig jobConfig) {
		assemble(
				action("eu.xenit.move2alf.core.action.SourceAction")
						.param("path", jobConfig.getInputFolder())
						.param("recursive", "true")
						.sourceSink(
								sourceSink(
										"eu.xenit.move2alf.core.sourcesink.FileSourceSink")),
				action("eu.xenit.move2alf.core.action.ThreadAction"),
				action("eu.xenit.move2alf.core.action.SinkAction")
						.sourceSink(
								sourceSink(
										"eu.xenit.move2alf.core.sourcesink.AlfrescoSourceSink")
										.param("url", null)
										.param("user", null)
										.param("password", null)));
	}
}
