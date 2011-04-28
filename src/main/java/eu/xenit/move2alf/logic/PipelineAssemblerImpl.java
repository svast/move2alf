package eu.xenit.move2alf.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ActionFactory;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Job;
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
		List<ActionBuilder> actions = new ArrayList<ActionBuilder>();
		
		List<String> metadataParameterList = jobConfig.getParamMetadata();
		Map<String,String> metadataParameterMap = new HashMap<String,String>();
		
		if(metadataParameterList != null){
			String[] metadataParameter = new String[2];
			for(int i=0; i<metadataParameterList.size();i++){
				metadataParameter = metadataParameterList.get(i).split("\\|");
				if(metadataParameter.length==1){
					String param = metadataParameter[0];
					metadataParameter = new String[2];
					metadataParameter[0] = param;
					metadataParameter[1] ="";
				}
				logger.debug("metadata parameter name: "+metadataParameter[0]+" and value: "+metadataParameter[1]);
				metadataParameterMap.put(metadataParameter[0], metadataParameter[1]);
			}
		}
		
		List<String> inputPathList = jobConfig.getInputFolder();
		String inputPaths="";
		if(inputPathList != null){
			for(int i=0; i<inputPathList.size();i++){
				if(i==0){
					inputPaths = inputPathList.get(i);
				}else{
					inputPaths = inputPaths+"|"+inputPathList.get(i);
				}
			}
		}
	
		List<String> transformParameterList = jobConfig.getParamTransform();
		Map<String,String> transformParameterMap = new HashMap<String,String>();
		
		if(transformParameterList != null){
			String [] transformParameter = new String[2];
			for(int i=0; i<transformParameterList.size();i++){
				transformParameter = transformParameterList.get(i).split("\\|");
				if(transformParameter.length==1){
					String param = transformParameter[0];
					transformParameter = new String[2];
					transformParameter[0] = param;
					transformParameter[1] ="";
				}
				logger.debug("transform parameter name: "+transformParameter[0]+" and value: "+transformParameter[1]);
				transformParameterMap.put(transformParameter[0], transformParameter[1]);
			}
		}

		actions.add(action("eu.xenit.move2alf.core.action.ExecuteCommandAction")
				.param("command", jobConfig.getCommand())
				.param("path",inputPaths)
				.param("stage", "before"));
		
		actions.add(action("eu.xenit.move2alf.core.action.SourceAction")
				.param("path",inputPaths)
				.param("recursive", "true")
				.param("moveNotLoaded", jobConfig.getMoveNotLoad())		//true or false (String)
				.param("moveNotLoadedPath", jobConfig.getNotLoadPath())
				.sourceSink(
						sourceSink("eu.xenit.move2alf.core.sourcesink.FileSourceSink")));
		
		actions.add(action("eu.xenit.move2alf.core.action.FilterAction")
				.param("extension", jobConfig.getExtension()));

		actions.add(action("eu.xenit.move2alf.core.action.MoveDocumentsAction")
				.param("moveBeforeProcessing", jobConfig.getMoveBeforeProc()) // true
				// or
				// false
				// (String)
				.param("moveBeforeProcessingPath",
						jobConfig.getBeforeProcPath()).param("moveAfterLoad",
						"false") // true or false (String)
						.param("moveAfterLoadPath", "").param("moveNotLoaded", "false") // true
						// or
						// false
						// (String)
						.param("moveNotLoadedPath", "")
						.param("path",inputPaths)
						.param("stage", "before"));
		
		actions.add(action(jobConfig.getMetadata())
				.paramMap(metadataParameterMap));

		actions.add(action("eu.xenit.move2alf.core.action.ThreadAction"));

		actions.add(action("eu.xenit.move2alf.core.action.MimetypeAction"));
		
		if(!"No transformation".equals(jobConfig.getTransform())){

			actions.add(action(jobConfig.getTransform())
					.paramMap(transformParameterMap));
		}

		actions.add(action("eu.xenit.move2alf.core.action.EmailAction").param(
				"sendNotification", jobConfig.getSendNotification()) // true or
																		// false
																		// (String)
				.param("emailAddressNotification",
						jobConfig.getEmailAddressError()).param("sendReport",
						jobConfig.getSendReport()) // true or false (String)
				.param("emailAddressReport", jobConfig.getEmailAddressRep()));

		if (SourceSink.MODE_SKIP.equals(jobConfig.getDocExist())
				|| SourceSink.MODE_SKIP_AND_LOG.equals(jobConfig.getDocExist())
				|| SourceSink.MODE_OVERWRITE.equals(jobConfig.getDocExist())) {
			actions.add(action("eu.xenit.move2alf.core.action.SinkAction")
					.param("path", jobConfig.getDestinationFolder()).param(
							"documentExists", jobConfig.getDocExist())
					// TODO: add param: ignore / error / overwrite / version
					.sourceSink(
							sourceSinkById(Integer
									.parseInt(jobConfig.getDest()))));
		}

		if ("Delete".equals(jobConfig.getDocExist())) {
			actions.add(action("eu.xenit.move2alf.core.action.DeleteAction")
					.param("path", jobConfig.getDestinationFolder()).param(
							"documentExists", jobConfig.getDocExist())
					.sourceSink(
							sourceSinkById(Integer
									.parseInt(jobConfig.getDest()))));
		}

		if ("ListPresence".equals(jobConfig.getDocExist())) {
			actions.add(action("eu.xenit.move2alf.core.action.ListAction")
					.param("path", jobConfig.getDestinationFolder()).param(
							"documentExists", jobConfig.getDocExist())
					.sourceSink(
							sourceSinkById(Integer
									.parseInt(jobConfig.getDest()))));
		}

		actions.add(action("eu.xenit.move2alf.core.action.MoveDocumentsAction")
				.param("moveBeforeProcessing", jobConfig.getMoveBeforeProc())
				// true or false (String)
				.param("moveBeforeProcessingPath",
						jobConfig.getBeforeProcPath())
				.param("moveAfterLoad",
						jobConfig.getMoveAfterLoad())
				// true or false (String)
				.param("moveAfterLoadPath", jobConfig.getAfterLoadPath())
				.param("moveNotLoaded", jobConfig.getMoveNotLoad()) // true or
																	// false
																	// (String)
				.param("moveNotLoadedPath", jobConfig.getNotLoadPath())
				.param("path",inputPaths)
				.param("stage","after"));

		actions.add(action("eu.xenit.move2alf.core.action.ReportAction"));
		
		actions.add(action("eu.xenit.move2alf.core.action.ExecuteCommandAction")
				.param("command", jobConfig.getCommandAfter())
				.param("path",inputPaths)
				.param("moveBeforeProcessing", jobConfig.getMoveBeforeProc())
				// true or false (String)
				.param("moveBeforeProcessingPath",
						jobConfig.getBeforeProcPath())
				.param("moveAfterLoad",
						jobConfig.getMoveAfterLoad())
				// true or false (String)
				.param("moveAfterLoadPath", jobConfig.getAfterLoadPath())
				.param("moveNotLoaded", jobConfig.getMoveNotLoad()) // true or
																	// false
																	// (String)
				.param("moveNotLoadedPath", jobConfig.getNotLoadPath())
				.param("stage", "after"));

		ActionBuilder[] actionsArray = (ActionBuilder[]) actions
				.toArray(new ActionBuilder[7]);

		assemble(jobConfig, actionsArray);
	}

	@Override
	public JobConfig getJobConfigForJob(int id) {
		Job job = getJobService().getJob(id);
		JobConfig jobConfig = new JobConfig();
		jobConfig.setId(id);
		jobConfig.setName(job.getName());
		jobConfig.setDescription(job.getDescription());
		ConfiguredAction action = job.getFirstConfiguredAction();
		List<String> inputFolder = new ArrayList();
		String inputPath="";
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
		String extension="";
		String command="";
		String commandAfter="";
		Map<String,String> metadataParameterMap = new HashMap<String,String>();
		Map<String,String> transformParameterMap = new HashMap<String,String>();

		while(action != null) {
			if ("eu.xenit.move2alf.core.action.SourceAction".equals(action.getClassName())) {

				inputPath = action.getParameter("path");
			} else if ("eu.xenit.move2alf.core.action.SinkAction".equals(action
					.getClassName())) {
				destinationFolder = action.getParameter("path");
				dest = action.getConfiguredSourceSinkSet().iterator().next()
						.getIdAsString();
				documentExists = action.getParameter("documentExists");
			} else if ("eu.xenit.move2alf.core.action.DeleteAction"
					.equals(action.getClassName())) {
				destinationFolder = action.getParameter("path");
				dest = action.getConfiguredSourceSinkSet().iterator().next()
						.getIdAsString();
				documentExists = action.getParameter("documentExists");
			} else if ("eu.xenit.move2alf.core.action.ListAction".equals(action
					.getClassName())) {
				destinationFolder = action.getParameter("path");
				dest = action.getConfiguredSourceSinkSet().iterator().next()
						.getIdAsString();
				documentExists = action.getParameter("documentExists");
			} else if ("eu.xenit.move2alf.core.action.MoveDocumentsAction"
					.equals(action.getClassName())) {
				moveBeforeProcessing = action
						.getParameter("moveBeforeProcessing");
				moveBeforeProcessingPath = action
						.getParameter("moveBeforeProcessingPath");
				moveAfterLoad = action.getParameter("moveAfterLoad");
				moveAfterLoadPath = action.getParameter("moveAfterLoadPath");
				moveNotLoaded = action.getParameter("moveNotLoaded");
				moveNotLoadedPath = action.getParameter("moveNotLoadedPath");
			} else if ("eu.xenit.move2alf.core.action.EmailAction"
					.equals(action.getClassName())) {
				sendNotification = action.getParameter("sendNotification");
				emailAddressNotification = action
						.getParameter("emailAddressNotification");
				sendReport = action.getParameter("sendReport");
				emailAddressReport = action.getParameter("emailAddressReport");
			} else if ("eu.xenit.move2alf.core.action.FilterAction"
					.equals(action.getClassName())) {
				extension = action.getParameter("extension");
			} else if ("eu.xenit.move2alf.core.action.ExecuteCommandAction"
					.equals(action.getClassName())) {
				if("before".equals(action.getParameter("stage"))){
					command = action.getParameter("command");
				}else{
					commandAfter = action.getParameter("command");
				}
			} else {
				Action configurableAction = getActionFactory().getObject(
						action.getClassName());
				if (configurableAction.getCategory() == ConfigurableObject.CAT_METADATA) {
					metadata = action.getClassName();

					metadataParameterMap = action.getParameters();
				}else if(configurableAction.getCategory()==ConfigurableObject.CAT_TRANSFORM){

					transform = action.getClassName();
					transformParameterMap = action.getParameters();
				}
			}

			action = action.getAppliedConfiguredActionOnSuccess();
		}
		
		if(inputPath== null){
			inputPath="";
		}
		String[] inputPathArray = inputPath.split("\\|");
		
		inputFolder = Arrays.asList(inputPathArray);
		
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
		jobConfig.setExtension(extension);
		jobConfig.setCommand(command);
		jobConfig.setCommandAfter(commandAfter);
		
		Iterator metadataMapIterator = metadataParameterMap.entrySet().iterator();
		List<String> paramMetadata = new ArrayList<String>();
		while(metadataMapIterator.hasNext()){
			Map.Entry nameValuePair = (Map.Entry)metadataMapIterator.next();
			
			String listValue = nameValuePair.getKey()+"|"+nameValuePair.getValue();
			logger.debug("metadata list value = "+listValue);
			paramMetadata.add(listValue);
		}
		
		jobConfig.setParamMetadata(paramMetadata);
		
		Iterator transformMapIterator = transformParameterMap.entrySet().iterator();
		List<String> paramTransform = new ArrayList<String>();
		while(transformMapIterator.hasNext()){
			Map.Entry nameValuePair = (Map.Entry)transformMapIterator.next();
			String listValue = nameValuePair.getKey()+"|"+nameValuePair.getValue();
			logger.debug("transform list value = "+listValue);
			paramTransform.add(listValue);
		}
		
		jobConfig.setParamTransform(paramTransform);
		
		return jobConfig;
	}
}
