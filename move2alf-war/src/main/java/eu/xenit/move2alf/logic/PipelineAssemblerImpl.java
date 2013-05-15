package eu.xenit.move2alf.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.action.ExecuteCommandAction;
import eu.xenit.move2alf.core.action.Move2AlfAction;
import eu.xenit.move2alf.core.simpleaction.*;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionWithSourceSink;
import eu.xenit.move2alf.pipeline.actions.ActionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.core.ConfigurableObject;
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
    public static final String MOVE_AFTER_ID = "MoveAfter";
    public static final String MOVE_NOT_LOADED_ID = "MoveNotLoaded";
    public static final String FILTER_ID = "Filter";
    public static final String METADATA_ACTION_ID = "MetadataAction";
    public static final String TRANSFORM_ACTION_ID = "TransformAction";
    public static final String MIME_TYPE_ID = "MimeType";
    public static final String UPLOAD_ID = "Upload";
    public static final String DELETE_ID = "Delete";
    public static final String EXISTENCE_CHECK_ID = "ExistenceCheck";
    public static final String LIST_ID = "List";
    public static final String EMAIL_ID = "Email";
    public static final String COMMAND_BEFORE_ID = "CommandBefore";
    public static final String COMMAND_AFTER_ID = "CommandAfter";

    @Autowired
	private UsageService usageService;

	private SourceSinkFactory sourceSinkFactory;

	private static final Logger logger = LoggerFactory
			.getLogger(PipelineAssemblerImpl.class);

	@Value(value = "#{'${default.batch.size}'}")
	private String defaultBatchSize;

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
		boolean moveBeforeProcessing = false;
		String moveBeforeProcessingPath = "";
		boolean moveAfterLoad = false;
		String moveAfterLoadPath = "";
		boolean moveNotLoaded = false;
		String moveNotLoadedPath = "";
		String sendNotification = "";
		String emailAddressNotification = "";
		String sendReport = "";
		String emailAddressReport = "";
		String extension = "";
		String commandBefore = "";
		String commandAfter = "";
		Map<String, String> metadataParameterMap = new HashMap<String, String>();
		Map<String, String> transformParameterMap = new HashMap<String, String>();

		while (action != null) {
			if (SOURCE_ID.equals(action
                    .getActionId())) {
				inputPath = action.getParameter(SASource.PARAM_INPUTPATHS);
			} else if (UPLOAD_ID.equals(action
                    .getActionId())) {
				destinationFolder = action.getParameter(SAUpload.PARAM_PATH);
				dest = action.getConfiguredSourceSink().getId();
				writeOption = action.getParameter(SAUpload.PARAM_WRITEOPTION);
				mode = Mode.WRITE;
			} else if (DELETE_ID
					.equals(action.getActionId())) {
				destinationFolder = action.getParameter(SADelete.PARAM_PATH);
				dest = action.getConfiguredSourceSink().getId();
				deleteOption = action.getParameter(SADelete.PARAM_DELETEOPTION);
				mode = Mode.DELETE;
			} else if (LIST_ID.equals(action
                    .getActionId())) {
				destinationFolder = action.getParameter(SAList.PARAM_PATH);
				dest = action.getConfiguredSourceSink().getId();
				ignorePath = false;
				mode = Mode.LIST;
			} else if (MOVE_BEFORE_ID
					.equals(action.getActionId())) {
				moveBeforeProcessing = true;
				moveBeforeProcessingPath = action
						.getParameter(MoveAction.PARAM_PATH);
            } else if (MOVE_AFTER_ID.equals(action.getActionId())) {
				moveAfterLoad = true;
				moveAfterLoadPath = action
						.getParameter(MoveAction.PARAM_PATH);
            } else if (MOVE_NOT_LOADED_ID.equals(action.getActionId())) {
				moveNotLoaded = true;
				moveNotLoadedPath = action
						.getParameter(MoveAction.PARAM_PATH);
			} else if (EMAIL_ID
					.equals(action.getActionId())) {
				sendNotification = action.getParameter("sendNotification");
				emailAddressNotification = action
						.getParameter("emailAddressNotification");
				sendReport = action.getParameter("sendReport");
				emailAddressReport = action.getParameter("emailAddressReport");
			} else if (FILTER_ID
					.equals(action.getActionId())) {
				extension = action.getParameter(SAFilter.PARAM_EXTENSION);
			} else if (COMMAND_BEFORE_ID
					.equals(action.getActionId())) {
					commandBefore = action.getParameter(ExecuteCommandAction.PARAM_COMMAND);
            } else if (COMMAND_AFTER_ID.equals(action.getActionId())) {
                commandAfter = action.getParameter(ExecuteCommandAction.PARAM_COMMAND);
			} else if (METADATA_ACTION_ID.equals(action.getActionId())) {

						metadataParameterMap = action.getParameters();
		    } else if (TRANSFORM_ACTION_ID.equals(action.getActionId())) {
						transformParameterMap = action.getParameters();
			}
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
		jobConfig.setCommand(commandBefore);
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
    public void assemblePipeline(JobConfig jobConfig) {
        ActionConfig pipeline = getPipeline(jobConfig);
        ConfiguredAction configuredAction = actionConfigToConfiguredAction(pipeline, new HashMap<String, ConfiguredAction>());
        Job job = getJobService().getJob(jobConfig.getId());
        job.setFirstConfiguredAction(configuredAction);
        getSessionFactory().getCurrentSession().save(job);
    }

    private ConfiguredAction actionConfigToConfiguredAction(ActionConfig actionConfig, Map<String, ConfiguredAction> configuredActions) {
        Map<String, ConfiguredAction> receivers = new HashMap<String, ConfiguredAction>();
        Map<String, ActionConfig> acReceivers = actionConfig.getReceivers();

        for(String key: acReceivers.keySet()){
            if(!configuredActions.containsKey(acReceivers.get(key).getId())){
                ConfiguredAction receiver = actionConfigToConfiguredAction(acReceivers.get(key), configuredActions);
                configuredActions.put(acReceivers.get(key).getId(), receiver);
            }
            receivers.put(key, configuredActions.get(key));
        }

        ConfiguredAction configuredAction = new ConfiguredAction();
        configuredAction.setReceivers(receivers);
        configuredAction.setClassId(actionConfig.getClazz().getCanonicalName());
        configuredAction.setNmbOfWorkers(actionConfig.getNmbOfWorkers());
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, Object> acParameters = actionConfig.getParameters();
        for(String key: acParameters.keySet()){
            Object value = acParameters.get(key);
            if(value instanceof String){
                parameters.put(key, (String)value);
            } else if (value instanceof ConfiguredSourceSink){
                configuredAction.setConfiguredSourceSink((ConfiguredSourceSink) value);
            }
        }

        return configuredAction;
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
