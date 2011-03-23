package eu.xenit.move2alf.logic;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ActionFactory;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.logic.PipelineAssembler.ActionBuilder;
import eu.xenit.move2alf.web.dto.JobConfig;

@Service("pipelineAssembler")
public class PipelineAssemblerImpl extends PipelineAssembler {
	
	private ActionFactory actionFactory;

	private static final Logger logger = LoggerFactory
			.getLogger(PipelineAssemblerImpl.class);

	@Autowired
	public void setActionFactory(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}

	public ActionFactory getActionFactory() {
		return actionFactory;
	}

	@Override
	public void assemblePipeline(JobConfig jobConfig) {
		List<ActionBuilder> actions = new ArrayList();
		
		actions.add(action("eu.xenit.move2alf.core.action.SourceAction")
				.param("path", jobConfig.getInputFolder())
				.param("recursive", "true")
				.param("moveNotLoaded", jobConfig.getMoveNotLoad())		//true or false (String)
				.param("moveNotLoadedPath", jobConfig.getNotLoadPath())
				.sourceSink(
						sourceSink("eu.xenit.move2alf.core.sourcesink.FileSourceSink")));
		
		actions.add(action("eu.xenit.move2alf.core.action.ThreadAction"));
		
		actions.add(action("eu.xenit.move2alf.core.action.MoveDocumentsAction")
				.param("moveBeforeProcessing", jobConfig.getMoveBeforeProc()) //true or false (String)
				.param("moveBeforeProcessingPath", jobConfig.getBeforeProcPath())
				.param("moveAfterLoad", "false")	//true or false (String)
				.param("moveAfterLoadPath", "")
				.param("moveNotLoaded", "false")		//true or false (String)
				.param("moveNotLoadedPath", "")
				.param("path", jobConfig.getInputFolder())
				.param("stage", "before"));
		
		actions.add(action("eu.xenit.move2alf.core.action.MimetypeAction"));
		
		actions.add(action(jobConfig.getMetadata()));
		
		if(!"No transformation".equals(jobConfig.getTransform())){
			actions.add(action(jobConfig.getTransform()));
		}
		
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
		
		actions.add(action("eu.xenit.move2alf.core.action.MoveDocumentsAction")
				.param("moveBeforeProcessing", jobConfig.getMoveBeforeProc()) //true or false (String)
				.param("moveBeforeProcessingPath", jobConfig.getBeforeProcPath())
				.param("moveAfterLoad", jobConfig.getMoveAfterLoad())	//true or false (String)
				.param("moveAfterLoadPath", jobConfig.getAfterLoadPath())
				.param("moveNotLoaded", jobConfig.getMoveNotLoad())		//true or false (String)
				.param("moveNotLoadedPath", jobConfig.getNotLoadPath())
				.param("path", jobConfig.getInputFolder())
				.param("stage", "after"));
		
		actions.add(action("eu.xenit.move2alf.core.action.ReportAction"));
		
		ActionBuilder[] actionsArray = (ActionBuilder[]) actions.toArray(new ActionBuilder[7]);
		
		assemble(jobConfig,actionsArray);
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
		String documentExists = "";
		String metadata="";
		String transform="";
		String moveBeforeProcessing="";
		String moveBeforeProcessingPath="";
		String moveAfterLoad="";
		String moveAfterLoadPath="";
		String moveNotLoaded="";
		String moveNotLoadedPath="";
		String sendNotification="";
		String emailAddressNotification="";
		String sendReport="";
		String emailAddressReport="";
		while(action != null) {
			if ("eu.xenit.move2alf.core.action.SourceAction".equals(action.getClassName())) {
				inputFolder = action.getParameter("path");
			} else if ("eu.xenit.move2alf.core.action.SinkAction".equals(action.getClassName())) {
				destinationFolder = action.getParameter("path");
				dest = action.getConfiguredSourceSinkSet().iterator().next().getIdAsString();
				documentExists = action.getParameter("documentExists");
			}else if("eu.xenit.move2alf.core.action.DeleteAction".equals(action.getClassName())){
				destinationFolder = action.getParameter("path");
				dest = action.getConfiguredSourceSinkSet().iterator().next().getIdAsString();
				documentExists = action.getParameter("documentExists");
			}else if("eu.xenit.move2alf.core.action.ListAction".equals(action.getClassName())){
				destinationFolder = action.getParameter("path");
				dest = action.getConfiguredSourceSinkSet().iterator().next().getIdAsString();
				documentExists = action.getParameter("documentExists");
			}else if("eu.xenit.move2alf.core.action.MoveDocumentsAction".equals(action.getClassName())){
				moveBeforeProcessing = action.getParameter("moveBeforeProcessing");
				moveBeforeProcessingPath = action.getParameter("moveBeforeProcessingPath");
				moveAfterLoad = action.getParameter("moveAfterLoad");
				moveAfterLoadPath = action.getParameter("moveAfterLoadPath");
				moveNotLoaded = action.getParameter("moveNotLoaded");
				moveNotLoadedPath = action.getParameter("moveNotLoadedPath");
			}else if("eu.xenit.move2alf.core.action.EmailAction".equals(action.getClassName())){
				sendNotification = action.getParameter("sendNotification");
				emailAddressNotification = action.getParameter("emailAddressNotification");
				sendReport = action.getParameter("sendReport");
				emailAddressReport = action.getParameter("emailAddressReport");
			}else{
				Action configurableAction = getActionFactory().getObject(action.getClassName());
				if(configurableAction.getCategory()==ConfigurableObject.CAT_METADATA){
					metadata = action.getClassName();
				}else if(configurableAction.getCategory()==ConfigurableObject.CAT_TRANSFORM){
					transform = action.getClassName();
				}
			}
			
			action = action.getAppliedConfiguredActionOnSuccess();
		}
		jobConfig.setInputFolder(inputFolder);
		jobConfig.setDestinationFolder(destinationFolder);
		jobConfig.setDest(dest);
		jobConfig.setDocExist(documentExists);
		jobConfig.setMetadata(metadata);
		jobConfig.setTransform(transform);
		jobConfig.setMoveBeforeProc(moveBeforeProcessing);
		jobConfig.setBeforeProcPath(moveBeforeProcessingPath);
		jobConfig.setMoveAfterLoad(moveAfterLoad);
		jobConfig.setAfterLoadPath(moveAfterLoadPath);
		jobConfig.setMoveNotLoad(moveNotLoaded);
		jobConfig.setNotLoadPath(moveNotLoadedPath);
		jobConfig.setSendNotification(sendNotification);
		jobConfig.setEmailAddressError(emailAddressNotification);
		jobConfig.setSendReport(sendReport);
		jobConfig.setEmailAddressRep(emailAddressReport);
		return jobConfig;
	}
}
