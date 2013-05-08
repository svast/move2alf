package eu.xenit.move2alf.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import eu.xenit.move2alf.core.simpleaction.*;
import eu.xenit.move2alf.pipeline.actions.ActionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.action.Action;
import eu.xenit.move2alf.core.action.ActionFactory;
import eu.xenit.move2alf.core.action.MoveDocumentsAction;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionWrapper;
import eu.xenit.move2alf.core.sourcesink.DeleteOption;
import eu.xenit.move2alf.core.sourcesink.SourceSink;
import eu.xenit.move2alf.core.sourcesink.SourceSinkFactory;
import eu.xenit.move2alf.core.sourcesink.WriteOption;
import eu.xenit.move2alf.logic.usageservice.UsageService;
import eu.xenit.move2alf.web.dto.JobConfig;

@Service("pipelineAssembler")
public class PipelineAssemblerImpl extends PipelineAssembler {

	@Autowired
	private UsageService usageService;
	
	private ActionFactory actionFactory;
	private SourceSinkFactory sourceSinkFactory;

	private static final Logger logger = LoggerFactory
			.getLogger(PipelineAssemblerImpl.class);

	@Value(value = "#{'${default.batch.size}'}")
	private String defaultBatchSize;

	@Autowired
	public void setActionFactory(final ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}

	public ActionFactory getActionFactory() {
		return actionFactory;
	}

	@Autowired
	public void setSourceSinkFactory(final SourceSinkFactory sourceSinkFactory) {
		this.sourceSinkFactory = sourceSinkFactory;
	}

	public SourceSinkFactory getSourceSinkFactory() {
		return sourceSinkFactory;
	}

	@Override
	public void assemblePipeline(final JobConfig jobConfig) {
        final List<ActionBuilder> actions = new ArrayList<ActionBuilder>();

		final Map<String, String> metadataParameterMap = metadataParameters(jobConfig);
		final Map<String, String> transformParameterMap = transformParameters(jobConfig);

		final List<String> inputPathList = jobConfig.getInputFolders();
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
		logger.debug("Move before not load: "
				+ jobConfig.getMoveBeforeProc().toString());
		actions.add(action("eu.xenit.move2alf.core.action.ExecuteCommandAction")
				.param("command", jobConfig.getCommand())
				.param("path", inputPaths)
				.param("stage", "before")
				.param(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING,
						jobConfig.getMoveBeforeProc().toString())
				// true or false (String)
				.param(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING_PATH,
						jobConfig.getMoveBeforeProcText()));

		actions.add(action("eu.xenit.move2alf.core.action.SourceAction")
				.param("path", inputPaths)
				.param("recursive", "true")
				.param(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED,
						jobConfig.getMoveNotLoad().toString())
				// true or false (String)
				.param(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED_PATH,
						jobConfig.getMoveNotLoadText())
				.param(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING,
						jobConfig.getMoveBeforeProc().toString())
				// true
				.param(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING_PATH,
						jobConfig.getMoveBeforeProcText())
				.sourceSink(
						sourceSink("eu.xenit.move2alf.core.sourcesink.FileSourceSink")));

		actions.add(action("eu.xenit.move2alf.core.action.FilterAction").param(
				"extension", jobConfig.getExtension()));

		actions.add(action("eu.xenit.move2alf.core.action.MoveDocumentsAction")
				.param(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING,
						jobConfig.getMoveBeforeProc().toString())
				// true
				// or
				// false
				// (String)
				.param(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING_PATH,
						jobConfig.getMoveBeforeProcText())
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

		if (!"notransformation".equals(jobConfig.getTransform())) {

			actions.add(action(jobConfig.getTransform()).paramMap(
					transformParameterMap));
		}

		actions.add(action("eu.xenit.move2alf.core.action.EmailAction")
				.param("sendNotification",
						jobConfig.getSendNotification().toString())
				// true or
				// false
				// (String)
				.param("emailAddressNotification",
						jobConfig.getSendNotificationText())
				.param("sendReport", jobConfig.getSendReport().toString()) // true
																			// or
																			// false
																			// (String)
				.param("emailAddressReport", jobConfig.getSendReportText()));

		if (Mode.WRITE == jobConfig.getMode()) {
			actions.add(action("eu.xenit.move2alf.core.action.SinkAction")
					.param("path", jobConfig.getDestinationFolder())
					.param("writeOption", jobConfig.getWriteOption().toString())
					// TODO: add param: ignore / error / overwrite / version
					.sourceSink(sourceSinkById(jobConfig.getDest())));
		}

		if (Mode.DELETE == jobConfig.getMode()) {
			actions.add(action("eu.xenit.move2alf.core.action.DeleteAction")
					.param("path", jobConfig.getDestinationFolder())
					.param("deleteOption", jobConfig.getDeleteOption().toString())
					.sourceSink(sourceSinkById(jobConfig.getDest())));
		}

		if (Mode.LIST == jobConfig.getMode()) {
			actions.add(action("eu.xenit.move2alf.core.action.ListAction")
					.param("path", jobConfig.getDestinationFolder())
					.param("ignorePath", Boolean.toString(jobConfig.getListIgnorePath()))
					.sourceSink(sourceSinkById(jobConfig.getDest())));
		}

		actions.add(action("eu.xenit.move2alf.core.action.MoveDocumentsAction")
				.param(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING,
						jobConfig.getMoveBeforeProc().toString())
				// true or false (String)
				.param(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING_PATH,
						jobConfig.getMoveBeforeProcText())
				.param(MoveDocumentsAction.PARAM_MOVE_AFTER_LOAD,
						jobConfig.getMoveAfterLoad().toString())
				// true or false (String)
				.param(MoveDocumentsAction.PARAM_MOVE_AFTER_LOAD_PATH,
						jobConfig.getMoveAfterLoadText())
				.param(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED,
						jobConfig.getMoveNotLoad().toString())
				// true or
				// false
				// (String)
				.param(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED_PATH,
						jobConfig.getMoveNotLoadText())
				.param("path", inputPaths).param("stage", "after"));

		actions.add(action("eu.xenit.move2alf.core.action.ExecuteCommandAction")
				.param("command", jobConfig.getCommandAfter())
				.param("path", inputPaths)
				.param(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING,
						jobConfig.getMoveBeforeProc().toString())
				// true or false (String)
				.param(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING_PATH,
						jobConfig.getMoveBeforeProcText())
				.param(MoveDocumentsAction.PARAM_MOVE_AFTER_LOAD,
						jobConfig.getMoveAfterLoad().toString())
				// true or false (String)
				.param(MoveDocumentsAction.PARAM_MOVE_AFTER_LOAD_PATH,
						jobConfig.getMoveAfterLoadText())
				.param(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED,
						jobConfig.getMoveNotLoad().toString()) // true or
				// false
				// (String)
				.param(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED_PATH,
						jobConfig.getMoveNotLoadText()).param("stage", "after"));

		final ActionBuilder[] actionsArray = actions
				.toArray(new ActionBuilder[7]);

		assemble(jobConfig, actionsArray);
	}


	@Override
	public JobConfig getJobConfigForJob(final int id) {
		final Job job = getJobService().getJob(id);
		final JobConfig jobConfig = new JobConfig();
		jobConfig.setId(id);
		jobConfig.setName(job.getName());
		jobConfig.setDescription(job.getDescription());
		ConfiguredAction action = job.getFirstConfiguredAction();
		List<String> inputFolder = new ArrayList();
		String inputPath = "";
		String destinationFolder = "";
		int dest = 0;
		String writeOption = null;
		String deleteOption = null;
		Mode mode = null;
		boolean ignorePath = false;

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
						.getId();
				writeOption = action.getParameter("writeOption");
				mode = Mode.WRITE;
			} else if ("eu.xenit.move2alf.core.action.DeleteAction"
					.equals(action.getClassName())) {
				destinationFolder = action.getParameter("path");
				dest = action.getConfiguredSourceSinkSet().iterator().next()
						.getId();
				deleteOption = action.getParameter("deleteOption");
				mode = Mode.DELETE;
			} else if ("eu.xenit.move2alf.core.action.ListAction".equals(action
					.getClassName())) {
				destinationFolder = action.getParameter("path");
				dest = action.getConfiguredSourceSinkSet().iterator().next()
						.getId();
				String bla = action.getParameter("ignorePath");
				ignorePath = Boolean.valueOf(action.getParameter("ignorePath"));
				mode = Mode.LIST;
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
				} catch (final IllegalArgumentException e) {
					logger.error("Action class not found: "
							+ action.getClassName());
				}
			}

			action = action.getAppliedConfiguredActionOnSuccess();
		}

		if (inputPath == null) {
			inputPath = "";
		}
		final String[] inputPathArray = inputPath.split("\\|");

		inputFolder = Arrays.asList(inputPathArray);

		jobConfig.setInputFolders(inputFolder);
		jobConfig.setDestinationFolder(destinationFolder);
		jobConfig.setDest(dest);
		jobConfig.setMode(mode);
		if(writeOption != null){
			jobConfig.setWriteOption(WriteOption.valueOf(writeOption));
		}
		if(deleteOption != null){
			jobConfig.setDeleteOption(DeleteOption.valueOf(deleteOption));
		}
		jobConfig.setListIgnorePath(ignorePath);
		jobConfig.setMetadata(metadata);
		jobConfig.setTransform(transform);
		jobConfig.setMoveBeforeProc(moveBeforeProcessing);
		jobConfig.setMoveBeforeProcText(moveBeforeProcessingPath);
		jobConfig.setMoveAfterLoad(moveAfterLoad);
		jobConfig.setMoveAfterLoadText(moveAfterLoadPath);
		jobConfig.setMoveNotLoad(moveNotLoaded);
		jobConfig.setMoveNotLoadText(moveNotLoadedPath);
		jobConfig.setSendNotification(sendNotification);
		jobConfig.setSendNotificationText(emailAddressNotification);
		jobConfig.setSendReport(sendReport);
		jobConfig.setSendReportText(emailAddressReport);
		jobConfig.setExtension(extension);
		jobConfig.setCommand(command);
		jobConfig.setCommandAfter(commandAfter);
		jobConfig.setCron(getJobService().getCronjobsForJob(id));

		final Iterator metadataMapIterator = metadataParameterMap.entrySet()
				.iterator();
		final List<String> paramMetadata = new ArrayList<String>();
		while (metadataMapIterator.hasNext()) {
			final Map.Entry nameValuePair = (Map.Entry) metadataMapIterator
					.next();

			final String listValue = nameValuePair.getKey() + "|"
					+ nameValuePair.getValue();
			logger.debug("metadata list value = " + listValue);
			paramMetadata.add(listValue);
		}

		jobConfig.setParamMetadata(paramMetadata);

		final Iterator transformMapIterator = transformParameterMap.entrySet()
				.iterator();
		final List<String> paramTransform = new ArrayList<String>();
		while (transformMapIterator.hasNext()) {
			final Map.Entry nameValuePair = (Map.Entry) transformMapIterator
					.next();
			final String listValue = nameValuePair.getKey() + "|"
					+ nameValuePair.getValue();
			logger.debug("transform list value = " + listValue);
			paramTransform.add(listValue);
		}

		jobConfig.setParamTransform(paramTransform);

		return jobConfig;
	}

	@Override
	public ActionConfig getPipeline(final JobConfig jobConfig) {

        ActionConfig start = new ActionConfig("Start",M2AlfStartAction.class, 1);

        ActionConfig sourceAction = new ActionConfig("Source", SASource.class, 1);
        sourceAction.setParameter("inputPaths", jobConfig.getInputFolders());
        start.addReceiver("default", sourceAction);
        ActionConfig end = sourceAction;

        if(jobConfig.getMoveBeforeProc()){
            ActionConfig moveAction = new ActionConfig("MoveBefore", MoveAction.class, 1);
            moveAction.setParameter("path", jobConfig.getMoveBeforeProcText());
            end.addReceiver("default", moveAction);
            end = moveAction;
        }

        ActionConfig filter = new ActionConfig("Filter", SAFilter.class, 1);
        filter.setParameter("extension", jobConfig.getExtension());
        end.addReceiver("default", filter);
        end = filter;

        try {
            Class clazz = Class.forName(jobConfig.getMetadata());
            ActionConfig metadataAction = new ActionConfig("MetadataAction", clazz, 1);
            setParameters(jobConfig.getParamMetadata(), metadataAction);
            end.addReceiver("default", metadataAction);
            end = metadataAction;
        } catch (ClassNotFoundException e) {
           //TODO handle exception decently
            logger.error("Class "+jobConfig.getMetadata()+ "not found", e);
        }


		if (!("notransformation".equals(jobConfig.getTransform()) || ""
				.equals(jobConfig.getTransform()))) {
			logger.debug("getTransform() == \"{}\"", jobConfig.getTransform());

            try {
                Class clazz = Class.forName(jobConfig.getTransform());
                ActionConfig transformAction = new ActionConfig("TransformAction", clazz, 1);
                setParameters(jobConfig.getParamTransform(), transformAction);
                end.addReceiver("default", transformAction);
                end = transformAction;
            } catch (ClassNotFoundException e) {
                //TODO handle exception decently
                logger.error("Class " + jobConfig.getTransform() + "not found", e);
            }
		}

        ActionConfig mimeTypeAction = new ActionConfig("MimeType", SAMimeType.class, 1);
        end.addReceiver("default", mimeTypeAction);
        end = mimeTypeAction;


		if (Mode.WRITE == jobConfig.getMode()) {
			final ConfiguredSourceSink sinkConfig = getJobService()
					.getDestination(jobConfig.getDest());
			final SourceSink sink = getSourceSinkFactory().getObject(
					sinkConfig.getClassName());
			final ExecutorService executorService = getSourceSinkFactory()
					.getThreadPool(sinkConfig);
			final SimpleAction uploadAction = new SAUpload(sink, sinkConfig, usageService);
			final ActionConfig uploadConfig = new ActionConfig();
			uploadConfig.put(SAUpload.PARAM_PATH,
					jobConfig.getDestinationFolder());
			uploadConfig.put(SAUpload.PARAM_DOCUMENT_EXISTS,
					jobConfig.getWriteOption().toString());
			uploadConfig.put(SAUpload.PARAM_BATCH_SIZE, defaultBatchSize);
			pipeline.add(new PipelineStep(uploadAction, uploadConfig,
					null, errorHandler, new ActionExecutor(
							executorService)));
		}

		if (Mode.DELETE  == jobConfig.getMode()) {
			final ConfiguredSourceSink sinkConfig = getJobService()
					.getDestination(jobConfig.getDest());
			final SourceSink sink = getSourceSinkFactory().getObject(
					sinkConfig.getClassName());
			final ExecutorService executorService = getSourceSinkFactory()
					.getThreadPool(sinkConfig);
			final SimpleAction deleteAction = new SADelete(sink, sinkConfig);
			final ActionConfig deleteConfig = new ActionConfig();
			deleteConfig.put(SADelete.PARAM_PATH,
					jobConfig.getDestinationFolder());
			deleteConfig.put(SADelete.PARAM_DELETEOPTION,
					jobConfig.getDeleteOption().toString());
			pipeline.add(new PipelineStep(deleteAction, deleteConfig,
					null, errorHandler, new ActionExecutor(
							executorService)));
		}
		if (Mode.LIST == jobConfig.getMode()) {
			
			final ConfiguredSourceSink sinkConfig = getJobService()
					.getDestination(jobConfig.getDest());
			final SourceSink sink = getSourceSinkFactory().getObject(
					sinkConfig.getClassName());
			final ExecutorService executorService = getSourceSinkFactory()
					.getThreadPool(sinkConfig);
			final SimpleAction listAction;
			final ActionConfig listConfig = new ActionConfig();
			
			if(jobConfig.getListIgnorePath()){
				listAction = new SAExistenceCheck(sink, sinkConfig);
			}
			else{
				listAction = new SAList(sink, sinkConfig);
				listConfig.put(SAList.PARAM_PATH,
						jobConfig.getDestinationFolder());			
			}
			pipeline.add(new PipelineStep(listAction, listConfig,
					null, errorHandler, new ActionExecutor(
							executorService)));
		}

		pipeline.add(new PipelineStep(new SAReport(), null, successHandler, errorHandler));

		return pipeline;
	}

    private void setParameters(List<String> parameters, ActionConfig metadataAction) {
        for(String param: parameters){
            String[] keyValue = param.split("\\|");
            metadataAction.setParameter(keyValue[0], keyValue[1]);
        }
    }
}
