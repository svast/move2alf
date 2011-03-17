package eu.xenit.move2alf.logic;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.logic.PipelineAssembler.ActionBuilder;
import eu.xenit.move2alf.web.dto.JobConfig;

@Service("pipelineAssembler")
public class MinimalPipelineAssembler extends PipelineAssembler {

	private static final Logger logger = LoggerFactory
			.getLogger(MinimalPipelineAssembler.class);

	@Override
	public void assemblePipeline(JobConfig jobConfig) {
		List<ActionBuilder> actions = new ArrayList();
		
		actions.add(action("eu.xenit.move2alf.core.action.SourceAction")
				.param("path", jobConfig.getInputFolder())
				.param("recursive", "true")
				.sourceSink(
						sourceSink("eu.xenit.move2alf.core.sourcesink.FileSourceSink")));
		
		actions.add(action("eu.xenit.move2alf.core.action.ThreadAction"));
		
		actions.add(action("eu.xenit.move2alf.core.action.MetadataAction")
				.param("metadata", jobConfig.getMetadata()));
		
		actions.add(action("eu.xenit.move2alf.core.action.TransformAction")
				.param("metadata", jobConfig.getTransform()));
		
		actions.add(action("eu.xenit.move2alf.core.action.MoveDocumentsAction")
				.param("moveBeforeProcessing", jobConfig.getMoveBeforeProc()) //true or false (String)
				.param("moveBeforeProcessingPath", jobConfig.getBeforeProcPath())
				.param("moveAfterLoad", jobConfig.getMoveAfterLoad())	//true or false (String)
				.param("moveAfterLoadPath", jobConfig.getAfterLoadPath())
				.param("moveNotLoaded", jobConfig.getMoveNotLoad())		//true or false (String)
				.param("moveNotLoadedPath", jobConfig.getNotLoadPath()));
		
		actions.add(action("eu.xenit.move2alf.core.action.EmailAction")
				.param("sendNotification", jobConfig.getSendNotification())	//true or false (String)
				.param("emailAddressNotification", jobConfig.getEmailAddressError())
				.param("sendReport", jobConfig.getSendReport())		//true or false (String)
				.param("emailAddressReport", jobConfig.getEmailAddressRep()));
		
		if("SkipAndLog".equals(jobConfig.getDocExist()) 
					|| "Skip".equals(jobConfig.getDocExist()) 
							|| "Overwrite".equals(jobConfig.getDocExist())){
			actions.add(action("eu.xenit.move2alf.core.action.SinkAction")
					.param("path", jobConfig.getDestinationFolder())
					.param("documentExists", jobConfig.getDocExist())
					// TODO: add param: ignore / error / overwrite / version 
					.sourceSink(
							sourceSinkById(Integer.parseInt(jobConfig.getDest()))));
		}
		
		if("Delete".equals(jobConfig.getDocExist())){
			actions.add(action("eu.xenit.move2alf.core.action.DeleteAction")
					.param("path", jobConfig.getDestinationFolder())
					.param("documentExists", jobConfig.getDocExist())
					.sourceSink(
							sourceSinkById(Integer.parseInt(jobConfig.getDest()))));
		}
		
		if("ListPresence".equals(jobConfig.getDocExist())){
			actions.add(action("eu.xenit.move2alf.core.action.ListAction")
					.param("path", jobConfig.getDestinationFolder())
					.param("documentExists", jobConfig.getDocExist())
					.sourceSink(
							sourceSinkById(Integer.parseInt(jobConfig.getDest()))));
		}
		
		ActionBuilder[] actionsArray = (ActionBuilder[]) actions.toArray(new ActionBuilder[7]);
		
		assemble(
				jobConfig,actionsArray
				/*action("eu.xenit.move2alf.core.action.SourceAction")
						.param("path", jobConfig.getInputFolder())
						.param("recursive", "true")
						.sourceSink(
								sourceSink("eu.xenit.move2alf.core.sourcesink.FileSourceSink")),
				action("eu.xenit.move2alf.core.action.ThreadAction"),
				action("eu.xenit.move2alf.core.action.MetadataAction")
						.param("metadata", jobConfig.getMetadata()),
				action("eu.xenit.move2alf.core.action.TransformAction")
						.param("metadata", jobConfig.getTransform()),
				action("eu.xenit.move2alf.core.action.MoveDocumentsAction")
						.param("moveBeforeProcessing", jobConfig.getMoveBeforeProc()) //true or false (String)
						.param("moveBeforeProcessingPath", jobConfig.getBeforeProcPath())
						.param("moveAfterLoad", jobConfig.getMoveAfterLoad())	//true or false (String)
						.param("moveAfterLoadPath", jobConfig.getAfterLoadPath())
						.param("moveNotLoaded", jobConfig.getMoveNotLoad())		//true or false (String)
						.param("moveNotLoadedPath", jobConfig.getNotLoadPath()),
				action("eu.xenit.move2alf.core.action.EmailAction")
						.param("sendNotification", jobConfig.getSendNotification())	//true or false (String)
						.param("emailAddressNotification", jobConfig.getEmailAddressError())
						.param("sendReport", jobConfig.getSendReport())		//true or false (String)
						.param("emailAddressReport", jobConfig.getEmailAddressRep()),
				action("eu.xenit.move2alf.core.action.SinkAction")
						.param("path", jobConfig.getDestinationFolder())
						.param("documentExists", jobConfig.getDocExist())
						// TODO: add param: ignore / error / overwrite / version 
						.sourceSink(
								sourceSinkById(Integer.parseInt(jobConfig.getDest()))),
				action("eu.xenit.move2alf.core.action.DeleteAction")
						.param("path", jobConfig.getDestinationFolder())
						.param("documentExists", jobConfig.getDocExist())
						.sourceSink(
								sourceSinkById(Integer.parseInt(jobConfig.getDest()))),
				action("eu.xenit.move2alf.core.action.ListAction")
						.param("path", jobConfig.getDestinationFolder())
						.param("documentExists", jobConfig.getDocExist())
						.sourceSink(
								sourceSinkById(Integer.parseInt(jobConfig.getDest())))*/);
				// TODO: Reporting
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
			}else if("eu.xenit.move2alf.core.action.DeleteAction".equals(action.getClassName())){
				
			}else if("eu.xenit.move2alf.core.action.ListAction".equals(action.getClassName())){
				
			}else if("eu.xenit.move2alf.core.action.MetadataAction".equals(action.getClassName())){
				
			}else if("eu.xenit.move2alf.core.action.TransformAction".equals(action.getClassName())){
				
			}else if("eu.xenit.move2alf.core.action.MoveDocumentsAction".equals(action.getClassName())){
				
			}else if("eu.xenit.move2alf.core.action.EmailActionAction".equals(action.getClassName())){
				
			}
			action = action.getAppliedConfiguredActionOnSuccess();
		}
		jobConfig.setInputFolder(inputFolder);
		jobConfig.setDestinationFolder(destinationFolder);
		jobConfig.setDest(dest);
		return jobConfig;
	}
}
