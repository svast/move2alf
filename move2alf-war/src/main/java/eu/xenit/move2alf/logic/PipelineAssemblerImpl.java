package eu.xenit.move2alf.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.simpleaction.*;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionWithSourceSink;
import eu.xenit.move2alf.pipeline.actions.ActionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.action.ActionFactory;
import eu.xenit.move2alf.core.action.MoveDocumentsAction;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.sourcesink.DeleteOption;
import eu.xenit.move2alf.core.sourcesink.SourceSink;
import eu.xenit.move2alf.core.sourcesink.SourceSinkFactory;
import eu.xenit.move2alf.core.sourcesink.WriteOption;
import eu.xenit.move2alf.logic.usageservice.UsageService;
import eu.xenit.move2alf.web.dto.JobConfig;

@Service("pipelineAssembler")
public class PipelineAssemblerImpl extends PipelineAssembler {

    public static final String SOURCE_ID= "Source";
    public static final String MOVE_BEFORE_ID = "MoveBefore";
    public static final String FILTER_ID = "Filter";
    public static final String METADATA_ACTION_ID = "MetadataAction";
    public static final String TRANSFORM_ACTION_ID = "TransformAction";
    public static final String MIME_TYPE_ID = "MimeType";
    public static final String UPLOAD_ID = "Upload";
    public static final String DELETE_ID = "Delete";
    public static final String EXISTENCE_CHECK_ID = "ExistenceCheck";
    public static final String LIST_ID = "List";
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
			if (SOURCE_ID.equals(action
                    .getActionId())) {

				inputPath = action.getParameter("path");
			} else if (UPLOAD_ID.equals(action
                    .getActionId())) {
				destinationFolder = action.getParameter("path");
				dest = action.getConfiguredSourceSink().getId();
				writeOption = action.getParameter("writeOption");
				mode = Mode.WRITE;
			} else if (DELETE_ID
					.equals(action.getActionId())) {
				destinationFolder = action.getParameter("path");
				dest = action.getConfiguredSourceSink().getId();
				deleteOption = action.getParameter("deleteOption");
				mode = Mode.DELETE;
			} else if (LIST_ID.equals(action
                    .getActionId())) {
				destinationFolder = action.getParameter("path");
				dest = action.getConfiguredSourceSink().getId();
				ignorePath = false;
				mode = Mode.LIST;
			} /*else if ("eu.xenit.move2alf.core.action.MoveDocumentsAction"
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
			}*/

			//action = action.getAppliedConfiguredActionOnSuccess();
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

        ActionConfig sourceAction = new ActionConfig(SOURCE_ID, SASource.class, 1);
        sourceAction.setParameter("inputPaths", jobConfig.getInputFolders());
        start.addReceiver("default", sourceAction);
        ActionConfig end = sourceAction;

        if(jobConfig.getMoveBeforeProc()){
            ActionConfig moveAction = new ActionConfig(MOVE_BEFORE_ID, MoveAction.class, 1);
            moveAction.setParameter("path", jobConfig.getMoveBeforeProcText());
            end.addReceiver("default", moveAction);
            end = moveAction;
        }

        ActionConfig filter = new ActionConfig(FILTER_ID, SAFilter.class, 1);
        filter.setParameter("extension", jobConfig.getExtension());
        end.addReceiver("default", filter);
        end = filter;

        try {
            Class clazz = Class.forName(jobConfig.getMetadata());
            ActionConfig metadataAction = new ActionConfig(METADATA_ACTION_ID, clazz, 1);
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
                ActionConfig transformAction = new ActionConfig(TRANSFORM_ACTION_ID, clazz, 1);
                setParameters(jobConfig.getParamTransform(), transformAction);
                end.addReceiver("default", transformAction);
                end = transformAction;
            } catch (ClassNotFoundException e) {
                //TODO handle exception decently
                logger.error("Class " + jobConfig.getTransform() + "not found", e);
            }
		}

        ActionConfig mimeTypeAction = new ActionConfig(MIME_TYPE_ID, SAMimeType.class, 1);
        end.addReceiver("default", mimeTypeAction);
        end = mimeTypeAction;


		if (Mode.WRITE == jobConfig.getMode()) {
			final ConfiguredSourceSink sinkConfig = getJobService()
					.getDestination(jobConfig.getDest());
			final SourceSink sink = getSourceSinkFactory().getObject(
					sinkConfig.getClassId());
            final ActionConfig uploadAction = new ActionConfig(UPLOAD_ID, SAUpload.class, 1);
            uploadAction.setParameter(SimpleActionWithSourceSink.PARAM_SINK, sink);
            uploadAction.setParameter(SimpleActionWithSourceSink.PARAM_SINKCONFIG, sinkConfig);
			uploadAction.setParameter(SAUpload.PARAM_PATH, jobConfig.getDestinationFolder());
            uploadAction.setParameter(SAUpload.PARAM_WRITEOPTION, jobConfig.getWriteOption());
            uploadAction.setParameter(SAUpload.PARAM_BATCH_SIZE, defaultBatchSize);
			end.addReceiver("default", uploadAction);
            end = uploadAction;
		}

		if (Mode.DELETE  == jobConfig.getMode()) {
			final ConfiguredSourceSink sinkConfig = getJobService()
					.getDestination(jobConfig.getDest());
			final SourceSink sink = getSourceSinkFactory().getObject(
					sinkConfig.getClassId());
            final ActionConfig deleteAction = new ActionConfig(DELETE_ID, SADelete.class, 1);
            deleteAction.setParameter(SimpleActionWithSourceSink.PARAM_SINK, sink);
            deleteAction.setParameter(SimpleActionWithSourceSink.PARAM_SINKCONFIG, sinkConfig);
            deleteAction.setParameter(SADelete.PARAM_PATH, jobConfig.getDestinationFolder());
            deleteAction.setParameter(SADelete.PARAM_DELETEOPTION, jobConfig.getDeleteOption());
            end.addReceiver("default", deleteAction);
            end = deleteAction;
		}
		if (Mode.LIST == jobConfig.getMode()) {
			
			final ConfiguredSourceSink sinkConfig = getJobService()
					.getDestination(jobConfig.getDest());
			final SourceSink sink = getSourceSinkFactory().getObject(
					sinkConfig.getClassId());

            ActionConfig listAction;
			
			if(jobConfig.getListIgnorePath()){
				listAction = new ActionConfig(EXISTENCE_CHECK_ID, SAExistenceCheck.class, 1);
			}
			else{
				listAction = new ActionConfig(LIST_ID, SAList.class, 1);
				listAction.setParameter(SAList.PARAM_PATH, jobConfig.getDestinationFolder());
			}
			listAction.setParameter(SimpleActionWithSourceSink.PARAM_SINK, sink);
            listAction.setParameter(SimpleActionWithSourceSink.PARAM_SINKCONFIG, sinkConfig);
            end.addReceiver("default", listAction);
            end = listAction;
		}

		return start;
	}

    private void setParameters(List<String> parameters, ActionConfig metadataAction) {
        for(String param: parameters){
            String[] keyValue = param.split("\\|");
            metadataAction.setParameter(keyValue[0], keyValue[1]);
        }
    }
}
