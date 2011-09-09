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
import eu.xenit.move2alf.core.action.MoveDocumentsAction;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.simpleaction.SAConvertFailsToException;
import eu.xenit.move2alf.core.simpleaction.SADelete;
import eu.xenit.move2alf.core.simpleaction.SAFilter;
import eu.xenit.move2alf.core.simpleaction.SAList;
import eu.xenit.move2alf.core.simpleaction.SAMimeType;
import eu.xenit.move2alf.core.simpleaction.SAMoveBeforeProcessing;
import eu.xenit.move2alf.core.simpleaction.SASource;
import eu.xenit.move2alf.core.simpleaction.SAUpload;
import eu.xenit.move2alf.core.simpleaction.SimpleAction;
import eu.xenit.move2alf.core.simpleaction.SimpleActionWrapper;
import eu.xenit.move2alf.core.sourcesink.AlfrescoSourceSink;
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

		Map<String, String> metadataParameterMap = metadataParameters(jobConfig);
		Map<String, String> transformParameterMap = transformParameters(jobConfig);

		List<String> inputPathList = jobConfig.getInputFolder();
		String inputPaths = "";
		if (inputPathList != null) {
			for (int i = 0; i < inputPathList.size(); i++) {
				if (i == 0) {
					inputPaths = inputPathList.get(i);
				} else {
					inputPaths = inputPaths + "|" + inputPathList.get(i);
				}
			}
		}

		actions
				.add(action(
						"eu.xenit.move2alf.core.action.ExecuteCommandAction")
						.param("command", jobConfig.getCommand())
						.param("path", inputPaths)
						.param("stage", "before")
						.param(
								MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING,
								jobConfig.getMoveBeforeProc())
						// true or false (String)
						.param(
								MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING_PATH,
								jobConfig.getBeforeProcPath()));

		actions
				.add(action("eu.xenit.move2alf.core.action.SourceAction")
						.param("path", inputPaths)
						.param("recursive", "true")
						.param(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED,
								jobConfig.getMoveNotLoad())
						// true or false (String)
						.param(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED_PATH,
								jobConfig.getNotLoadPath())
						.param(
								MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING,
								jobConfig.getMoveBeforeProc())
						// true
						.param(
								MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING_PATH,
								jobConfig.getBeforeProcPath())
						.sourceSink(
								sourceSink("eu.xenit.move2alf.core.sourcesink.FileSourceSink")));

		actions.add(action("eu.xenit.move2alf.core.action.FilterAction").param(
				"extension", jobConfig.getExtension()));

		actions.add(action("eu.xenit.move2alf.core.action.MoveDocumentsAction")
				.param(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING,
						jobConfig.getMoveBeforeProc())
				// true
				// or
				// false
				// (String)
				.param(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING_PATH,
						jobConfig.getBeforeProcPath())
				.param(MoveDocumentsAction.PARAM_MOVE_AFTER_LOAD, "false")
				// true or false (String)
				.param(MoveDocumentsAction.PARAM_MOVE_AFTER_LOAD_PATH, "")
				.param(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED, "false")
				// true
				// or
				// false
				// (String)
				.param(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED_PATH, "")
				.param("path", inputPaths).param("stage", "before"));

		actions.add(action(jobConfig.getMetadata()).paramMap(
				metadataParameterMap));

		actions.add(action("eu.xenit.move2alf.core.action.ThreadAction"));

		actions.add(action("eu.xenit.move2alf.core.action.MimetypeAction"));

		if (!"No transformation".equals(jobConfig.getTransform())) {

			actions.add(action(jobConfig.getTransform()).paramMap(
					transformParameterMap));
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
				.param(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING,
						jobConfig.getMoveBeforeProc())
				// true or false (String)
				.param(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING_PATH,
						jobConfig.getBeforeProcPath())
				.param(MoveDocumentsAction.PARAM_MOVE_AFTER_LOAD,
						jobConfig.getMoveAfterLoad())
				// true or false (String)
				.param(MoveDocumentsAction.PARAM_MOVE_AFTER_LOAD_PATH,
						jobConfig.getAfterLoadPath())
				.param(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED,
						jobConfig.getMoveNotLoad())
				// true or
				// false
				// (String)
				.param(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED_PATH,
						jobConfig.getNotLoadPath()).param("path", inputPaths)
				.param("stage", "after"));

		actions.add(action("eu.xenit.move2alf.core.action.ReportAction"));

		actions
				.add(action(
						"eu.xenit.move2alf.core.action.ExecuteCommandAction")
						.param("command", jobConfig.getCommandAfter())
						.param("path", inputPaths)
						.param(
								MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING,
								jobConfig.getMoveBeforeProc())
						// true or false (String)
						.param(
								MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING_PATH,
								jobConfig.getBeforeProcPath()).param(
								MoveDocumentsAction.PARAM_MOVE_AFTER_LOAD,
								jobConfig.getMoveAfterLoad())
						// true or false (String)
						.param(MoveDocumentsAction.PARAM_MOVE_AFTER_LOAD_PATH,
								jobConfig.getAfterLoadPath()).param(
								MoveDocumentsAction.PARAM_MOVE_NOT_LOADED,
								jobConfig.getMoveNotLoad()) // true or
						// false
						// (String)
						.param(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED_PATH,
								jobConfig.getNotLoadPath()).param("stage",
								"after"));

		ActionBuilder[] actionsArray = (ActionBuilder[]) actions
				.toArray(new ActionBuilder[7]);

		assemble(jobConfig, actionsArray);
	}

	private Map<String, String> transformParameters(JobConfig jobConfig) {
		List<String> transformParameterList = jobConfig.getParamTransform();
		Map<String, String> transformParameterMap = new HashMap<String, String>();

		if (transformParameterList != null) {
			String[] transformParameter = new String[2];
			for (int i = 0; i < transformParameterList.size(); i++) {
				transformParameter = transformParameterList.get(i).split("\\|");
				if (transformParameter.length == 1) {
					String param = transformParameter[0];
					transformParameter = new String[2];
					transformParameter[0] = param;
					transformParameter[1] = "";
				}
				logger.debug("transform parameter name: "
						+ transformParameter[0] + " and value: "
						+ transformParameter[1]);
				transformParameterMap.put(transformParameter[0],
						transformParameter[1]);
			}
		}
		return transformParameterMap;
	}

	private Map<String, String> metadataParameters(JobConfig jobConfig) {
		List<String> metadataParameterList = jobConfig.getParamMetadata();
		Map<String, String> metadataParameterMap = new HashMap<String, String>();

		if (metadataParameterList != null) {
			String[] metadataParameter = new String[2];
			for (int i = 0; i < metadataParameterList.size(); i++) {
				metadataParameter = metadataParameterList.get(i).split("\\|");
				if (metadataParameter.length == 1) {
					String param = metadataParameter[0];
					metadataParameter = new String[2];
					metadataParameter[0] = param;
					metadataParameter[1] = "";
				}
				logger.debug("metadata parameter name: " + metadataParameter[0]
						+ " and value: " + metadataParameter[1]);
				metadataParameterMap.put(metadataParameter[0],
						metadataParameter[1]);
			}
		}
		return metadataParameterMap;
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
		String inputPath = "";
		String destinationFolder = "";
		String dest = "";
		String documentExists = "";

		String metadata = "";
		String transform = "";
		String moveBeforeProcessing = "";
		String moveBeforeProcessingPath = "";
		String moveAfterLoad = "";
		String moveAfterLoadPath = "";
		String moveNotLoaded = "";
		String moveNotLoadedPath = "";
		String sendNotification = "";
		String emailAddressNotification = "";
		String sendReport = "";
		String emailAddressReport = "";
		String extension = "";
		String command = "";
		String commandAfter = "";
		Map<String, String> metadataParameterMap = new HashMap<String, String>();
		Map<String, String> transformParameterMap = new HashMap<String, String>();

		while (action != null) {
			if ("eu.xenit.move2alf.core.action.SourceAction".equals(action
					.getClassName())) {

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
						.getParameter(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING);
				moveBeforeProcessingPath = action
						.getParameter(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING_PATH);
				moveAfterLoad = action
						.getParameter(MoveDocumentsAction.PARAM_MOVE_AFTER_LOAD);
				moveAfterLoadPath = action
						.getParameter(MoveDocumentsAction.PARAM_MOVE_AFTER_LOAD_PATH);
				moveNotLoaded = action
						.getParameter(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED);
				moveNotLoadedPath = action
						.getParameter(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED_PATH);
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
				if ("before".equals(action.getParameter("stage"))) {
					command = action.getParameter("command");
				} else {
					commandAfter = action.getParameter("command");
				}
			} else {
				Action configurableAction = null;
				try {
					configurableAction = getActionFactory().getObject(
							action.getClassName());
					if (configurableAction.getCategory() == ConfigurableObject.CAT_METADATA) {
						metadata = action.getClassName();

						metadataParameterMap = action.getParameters();
					} else if (configurableAction.getCategory() == ConfigurableObject.CAT_TRANSFORM) {

						transform = action.getClassName();
						transformParameterMap = action.getParameters();
					}
				} catch (IllegalArgumentException e) {
					logger.error("Action class not found: "
							+ action.getClassName());
				}
			}

			action = action.getAppliedConfiguredActionOnSuccess();
		}

		if (inputPath == null) {
			inputPath = "";
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

		Iterator metadataMapIterator = metadataParameterMap.entrySet()
				.iterator();
		List<String> paramMetadata = new ArrayList<String>();
		while (metadataMapIterator.hasNext()) {
			Map.Entry nameValuePair = (Map.Entry) metadataMapIterator.next();

			String listValue = nameValuePair.getKey() + "|"
					+ nameValuePair.getValue();
			logger.debug("metadata list value = " + listValue);
			paramMetadata.add(listValue);
		}

		jobConfig.setParamMetadata(paramMetadata);

		Iterator transformMapIterator = transformParameterMap.entrySet()
				.iterator();
		List<String> paramTransform = new ArrayList<String>();
		while (transformMapIterator.hasNext()) {
			Map.Entry nameValuePair = (Map.Entry) transformMapIterator.next();
			String listValue = nameValuePair.getKey() + "|"
					+ nameValuePair.getValue();
			logger.debug("transform list value = " + listValue);
			paramTransform.add(listValue);
		}

		jobConfig.setParamTransform(paramTransform);

		return jobConfig;
	}

	@Override
	public List<PipelineStep> getPipeline(JobConfig jobConfig) {
		List<PipelineStep> pipeline = new ArrayList<PipelineStep>();
		pipeline.add(new PipelineStep(new SASource()));

		if ("true".equals(jobConfig.getMoveBeforeProc())) {
			Map<String, String> moveBeforeConfig = new HashMap<String, String>();
			moveBeforeConfig.put(
					SAMoveBeforeProcessing.PARAM_MOVE_BEFORE_PROCESSING_PATH,
					jobConfig.getBeforeProcPath());
			pipeline.add(new PipelineStep(new SAMoveBeforeProcessing(),
					moveBeforeConfig));
		}

		Map<String, String> filterConfig = new HashMap<String, String>();
		filterConfig.put(SAFilter.PARAM_EXTENSION, jobConfig.getExtension());
		pipeline.add(new PipelineStep(new SAFilter(), filterConfig));
		pipeline.add(new PipelineStep(new SAMimeType()));

		SimpleAction metadataAction = new SimpleActionWrapper(
				getActionFactory().getObject(jobConfig.getMetadata()));
		Map<String, String> metadataConfig = metadataParameters(jobConfig);
		pipeline.add(new PipelineStep(metadataAction, metadataConfig));

		if (!"No transformation".equals(jobConfig.getTransform())) {
			SimpleAction transformAction = new SimpleActionWrapper(
					getActionFactory().getObject(jobConfig.getMetadata()));
			Map<String, String> transformConfig = transformParameters(jobConfig);
			pipeline.add(new PipelineStep(transformAction, transformConfig));
		}

		if (SourceSink.MODE_SKIP.equals(jobConfig.getDocExist())
				|| SourceSink.MODE_SKIP_AND_LOG.equals(jobConfig.getDocExist())
				|| SourceSink.MODE_OVERWRITE.equals(jobConfig.getDocExist())) {
			ConfiguredSourceSink sinkConfig = getJobService().getDestination(
					Integer.parseInt(jobConfig.getDest()));
			SourceSink sink = new AlfrescoSourceSink();
			SimpleAction uploadAction = new SAUpload(sink, sinkConfig);
			Map<String, String> uploadConfig = new HashMap<String, String>();
			uploadConfig.put(SAUpload.PARAM_PATH, jobConfig.getDestinationFolder());
			uploadConfig.put(SAUpload.PARAM_DOCUMENT_EXISTS, jobConfig.getDocExist());
			pipeline.add(new PipelineStep(uploadAction, uploadConfig));
		}
		if ("Delete".equals(jobConfig.getDocExist())) {
			ConfiguredSourceSink sinkConfig = getJobService().getDestination(
					Integer.parseInt(jobConfig.getDest()));
			SourceSink sink = new AlfrescoSourceSink();
			SimpleAction uploadAction = new SADelete(sink, sinkConfig);
			Map<String, String> uploadConfig = new HashMap<String, String>();
			uploadConfig.put(SAUpload.PARAM_PATH, jobConfig.getDestinationFolder());
			uploadConfig.put(SAUpload.PARAM_DOCUMENT_EXISTS, jobConfig.getDocExist());
			pipeline.add(new PipelineStep(uploadAction, uploadConfig));
		}
		if ("ListPresence".equals(jobConfig.getDocExist())) {
			ConfiguredSourceSink sinkConfig = getJobService().getDestination(
					Integer.parseInt(jobConfig.getDest()));
			SourceSink sink = new AlfrescoSourceSink();
			SimpleAction uploadAction = new SAList(sink, sinkConfig);
			Map<String, String> uploadConfig = new HashMap<String, String>();
			uploadConfig.put(SAUpload.PARAM_PATH, jobConfig.getDestinationFolder());
			uploadConfig.put(SAUpload.PARAM_DOCUMENT_EXISTS, jobConfig.getDocExist());
			pipeline.add(new PipelineStep(uploadAction, uploadConfig));
		}
		
		pipeline.add(new PipelineStep(new SAConvertFailsToException()));
		

		return pipeline;
	}
}
