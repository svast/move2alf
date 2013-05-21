package eu.xenit.move2alf.logic;

import java.util.*;

import eu.xenit.move2alf.core.action.ActionClassService;
import eu.xenit.move2alf.core.action.ExecuteCommandAction;
import eu.xenit.move2alf.core.action.M2AlfEndAction;
import eu.xenit.move2alf.core.action.StartCycleAction;
import eu.xenit.move2alf.core.simpleaction.*;
import eu.xenit.move2alf.pipeline.actions.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionWithSourceSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    public static final String DEFAULT_RECEIVER = "default";
    public static final String REPORTER = "reporter";
    public static final String END_ACTION = "EndAction";
    public static final String START = "Start";

    @Autowired
    private ActionClassService actionClassService;

    @Autowired
	private UsageService usageService;

	private SourceSinkFactory sourceSinkFactory;

	private static final Logger logger = LoggerFactory
			.getLogger(PipelineAssemblerImpl.class);

	@Value(value = "#{'${default.batch.size}'}")
	private int defaultBatchSize;

	@Autowired
	public void setSourceSinkFactory(final SourceSinkFactory sourceSinkFactory) {
		this.sourceSinkFactory = sourceSinkFactory;
	}

	public SourceSinkFactory getSourceSinkFactory() {
		return sourceSinkFactory;
	}


    private Map<String, ConfiguredAction> getAllConfiguredActions(ConfiguredAction action, Map<String, ConfiguredAction> configuredActionMap){
        configuredActionMap.put(action.getActionId(), action);
        for(ConfiguredAction receiver: action.getReceivers().values()){
            getAllConfiguredActions(receiver, configuredActionMap);
        }
        return configuredActionMap;
    }

	@Override
	public JobConfig getJobConfigForJob(final int id) {
		final Job job = getJobService().getJob(id);
		final JobConfig jobConfig = new JobConfig();
		jobConfig.setId(id);
		jobConfig.setName(job.getName());
		jobConfig.setDescription(job.getDescription());
		ConfiguredAction startAction = job.getFirstConfiguredAction();

        Map<String, ConfiguredAction> configuredActionMap = getAllConfiguredActions(startAction, new HashMap<String, ConfiguredAction>());

		List<String> inputFolder = new ArrayList();
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
		String extension = "*";
		String commandBefore = "";
		String commandAfter = "";
		Map<String, String> metadataParameterMap = new HashMap<String, String>();
		Map<String, String> transformParameterMap = new HashMap<String, String>();

		for(ConfiguredAction action: configuredActionMap.values()) {
			if (SOURCE_ID.equals(action
                    .getActionId())) {
                String path = action.getParameter(SASource.PARAM_INPUTPATHS);
                inputFolder = Arrays.asList(path.split("\\|"));
			} else if (UPLOAD_ID.equals(action
                    .getActionId())) {
				destinationFolder = action.getParameter(SAUpload.PARAM_PATH);
				dest = Integer.parseInt(action.getParameter(SimpleActionWithSourceSink.PARAM_DESTINATION));
				writeOption = action.getParameter(SAUpload.PARAM_WRITEOPTION);
				mode = Mode.WRITE;
			} else if (DELETE_ID
					.equals(action.getActionId())) {
				destinationFolder = action.getParameter(SADelete.PARAM_PATH);
                dest = Integer.parseInt(action.getParameter(SimpleActionWithSourceSink.PARAM_DESTINATION));
				deleteOption = action.getParameter(SADelete.PARAM_DELETEOPTION);
				mode = Mode.DELETE;
			} else if (LIST_ID.equals(action
                    .getActionId())) {
				destinationFolder = action.getParameter(SAList.PARAM_PATH);
                dest = Integer.parseInt(action.getParameter(SimpleActionWithSourceSink.PARAM_DESTINATION));
				ignorePath = false;
				mode = Mode.LIST;
			} else if (EXISTENCE_CHECK_ID.equals(action.getActionId())){
                ignorePath = true;
                mode = Mode.LIST;
                dest = Integer.parseInt(action.getParameter(SimpleActionWithSourceSink.PARAM_DESTINATION));
                destinationFolder = action.getParameter(SAList.PARAM_PATH);
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
                        metadata = action.getClassId();
						metadataParameterMap = action.getParameters();
		    } else if (TRANSFORM_ACTION_ID.equals(action.getActionId())) {
                        transform = action.getClassId();
						transformParameterMap = action.getParameters();
			}
		}

		jobConfig.setInputFolder(inputFolder);
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
    public ActionConfig getActionConfig(ConfiguredAction configuredAction){
        return configuredActionToActionConfig(configuredAction, new HashMap<String, ActionConfig>());
    }

    private ActionConfig configuredActionToActionConfig(ConfiguredAction configuredAction, Map<String, ActionConfig> actionConfigMap) {
        Map<String, ConfiguredAction> configuredActionReceivers = configuredAction.getReceivers();
        ActionConfig actionConfig = new ActionConfig(configuredAction.getActionId(), actionClassService.getActionClassInfo(configuredAction.getClassId()).getClazz(), configuredAction.getNmbOfWorkers());

        for(String key: configuredActionReceivers.keySet()){
            if(!actionConfigMap.containsKey(configuredActionReceivers.get(key).getActionId())){
                ActionConfig receiver = configuredActionToActionConfig(configuredActionReceivers.get(key), actionConfigMap);
                actionConfigMap.put(configuredActionReceivers.get(key).getActionId(), receiver);
            }
            actionConfig.addReceiver(key, actionConfigMap.get(configuredActionReceivers.get(key).getActionId()));
        }

        for(Map.Entry<String, String> entry: configuredAction.getParameters().entrySet()){
            actionConfig.setParameter(entry.getKey(), entry.getValue());
        }
        return actionConfig;
    }

    @Override
	public ConfiguredAction getConfiguredAction(final JobConfig jobConfig) {

        ConfiguredAction reporter = new ConfiguredAction();
        reporter.setActionId(REPORTER);
        reporter.setNmbOfWorkers(1);
        reporter.setClassId(actionClassService.getClassId(SAReport.class));

        ConfiguredAction start = new ConfiguredAction();
        start.setActionId(START);
        start.setNmbOfWorkers(1);
        start.setClassId(actionClassService.getClassId(StartCycleAction.class));

        ConfiguredAction end = start;

        if(!jobConfig.getCommand().isEmpty()){
            ConfiguredAction executeCommandBefore = new ConfiguredAction();
            executeCommandBefore.setActionId(COMMAND_BEFORE_ID);
            executeCommandBefore.setClassId(actionClassService.getClassId(ExecuteCommandAction.class));
            executeCommandBefore.setNmbOfWorkers(1);
            executeCommandBefore.setParameter(ExecuteCommandAction.PARAM_COMMAND, jobConfig.getCommand());
            executeCommandBefore.addReceiver(REPORTER, reporter);
            end.addReceiver(DEFAULT_RECEIVER, executeCommandBefore);
            end = executeCommandBefore;
        }

        ConfiguredAction sourceAction = new ConfiguredAction();
        sourceAction.setActionId(SOURCE_ID);
        sourceAction.setClassId(actionClassService.getClassId(SASource.class));
        sourceAction.setNmbOfWorkers(1);
        sourceAction.setParameter(SASource.PARAM_INPUTPATHS, encodeStringList(jobConfig.getInputFolder()));
        sourceAction.addReceiver(REPORTER, reporter);
        end.addReceiver(DEFAULT_RECEIVER, sourceAction);
        end = sourceAction;

        if(jobConfig.getMoveBeforeProc()){
            ConfiguredAction moveAction = new ConfiguredAction();
            moveAction.setActionId(MOVE_BEFORE_ID);
            moveAction.setClassId(actionClassService.getClassId(MoveAction.class));
            moveAction.setNmbOfWorkers(1);
            moveAction.setParameter(MoveAction.PARAM_PATH, jobConfig.getMoveBeforeProcText());
            moveAction.addReceiver(REPORTER, reporter);
            end.addReceiver(DEFAULT_RECEIVER, moveAction);
            end = moveAction;
        }

        if(!jobConfig.getExtension().isEmpty() && !jobConfig.getExtension().equals("*")){
            ConfiguredAction filter = new ConfiguredAction();
            filter.setActionId(FILTER_ID);
            filter.setClassId(actionClassService.getClassId(SAFilter.class));
            filter.setNmbOfWorkers(1);
            filter.setParameter(SAFilter.PARAM_EXTENSION, jobConfig.getExtension());
            filter.addReceiver(REPORTER, reporter);
            end.addReceiver(DEFAULT_RECEIVER, filter);
            end = filter;
        }

        String classId = jobConfig.getMetadata();
        ConfiguredAction metadataAction = new ConfiguredAction();
        metadataAction.setActionId(METADATA_ACTION_ID);
        metadataAction.setClassId(classId);
        metadataAction.setNmbOfWorkers(1);
        metadataAction.addReceiver(REPORTER, reporter);
        setParameters(jobConfig.getParamMetadata(), metadataAction);
        end.addReceiver(DEFAULT_RECEIVER, metadataAction);
        end = metadataAction;


		if (!("notransformation".equals(jobConfig.getTransform()) || ""
				.equals(jobConfig.getTransform()))) {
			logger.debug("getTransform() == \"{}\"", jobConfig.getTransform());

            ConfiguredAction transformAction = new ConfiguredAction();
            transformAction.setActionId(TRANSFORM_ACTION_ID);
            transformAction.setClassId(jobConfig.getTransform());
            transformAction.setNmbOfWorkers(1);
            transformAction.addReceiver(REPORTER, reporter);
            setParameters(jobConfig.getParamTransform(), transformAction);
            end.addReceiver(DEFAULT_RECEIVER, transformAction);
            end = transformAction;
		}

        ConfiguredAction mimeTypeAction = new ConfiguredAction();
        mimeTypeAction.setActionId(MIME_TYPE_ID);
        mimeTypeAction.setClassId(actionClassService.getClassId(SAMimeType.class));
        mimeTypeAction.setNmbOfWorkers(1);
        mimeTypeAction.addReceiver(REPORTER, reporter);
        end.addReceiver(DEFAULT_RECEIVER, mimeTypeAction);
        end = mimeTypeAction;


		if (Mode.WRITE == jobConfig.getMode()) {
            final ConfiguredAction uploadAction = new ConfiguredAction();
            uploadAction.setActionId(UPLOAD_ID);
            uploadAction.setClassId(actionClassService.getClassId(SAUpload.class));
            uploadAction.setNmbOfWorkers(1);
            uploadAction.addReceiver(REPORTER, reporter);
            uploadAction.setParameter(SimpleActionWithSourceSink.PARAM_DESTINATION, String.valueOf(jobConfig.getDest()));
			uploadAction.setParameter(SAUpload.PARAM_PATH, jobConfig.getDestinationFolder());
            uploadAction.setParameter(SAUpload.PARAM_WRITEOPTION, jobConfig.getWriteOption().toString());
            uploadAction.setParameter(SAUpload.PARAM_BATCH_SIZE, String.valueOf(defaultBatchSize));
			end.addReceiver(DEFAULT_RECEIVER, uploadAction);
            end = uploadAction;
		}

		if (Mode.DELETE  == jobConfig.getMode()) {
			final ConfiguredSourceSink sinkConfig = getJobService()
					.getDestination(jobConfig.getDest());
			final SourceSink sink = getSourceSinkFactory().getObject(
					sinkConfig.getClassId());
            final ConfiguredAction deleteAction = new ConfiguredAction();
            deleteAction.setActionId(DELETE_ID);
            deleteAction.setClassId(actionClassService.getClassId(SADelete.class));
            deleteAction.setNmbOfWorkers(1);
            deleteAction.addReceiver(REPORTER, reporter);
            deleteAction.setParameter(SimpleActionWithSourceSink.PARAM_DESTINATION, String.valueOf(jobConfig.getDest()));
            deleteAction.setParameter(SADelete.PARAM_PATH, jobConfig.getDestinationFolder());
            deleteAction.setParameter(SADelete.PARAM_DELETEOPTION, jobConfig.getDeleteOption().toString());
            end.addReceiver(DEFAULT_RECEIVER, deleteAction);
            end = deleteAction;
		}
		if (Mode.LIST == jobConfig.getMode()) {
			
			final ConfiguredSourceSink sinkConfig = getJobService()
					.getDestination(jobConfig.getDest());
			final SourceSink sink = getSourceSinkFactory().getObject(
					sinkConfig.getClassId());

            ConfiguredAction listAction;
			
			if(jobConfig.getListIgnorePath()){
				listAction = new ConfiguredAction();
                listAction.setActionId(EXISTENCE_CHECK_ID);
                listAction.setClassId(actionClassService.getClassId(SAExistenceCheck.class));
			}
			else{
				listAction = new ConfiguredAction();
                listAction.setActionId(LIST_ID);
                listAction.setActionId(actionClassService.getClassId(SAList.class));
			}
            listAction.setNmbOfWorkers(1);
            listAction.setParameter(SAList.PARAM_PATH, jobConfig.getDestinationFolder());
            listAction.addReceiver(REPORTER, reporter);
			listAction.setParameter(SimpleActionWithSourceSink.PARAM_DESTINATION, String.valueOf(jobConfig.getDest()));
            end.addReceiver(DEFAULT_RECEIVER, listAction);
            end = listAction;
		}


        if(!jobConfig.getCommandAfter().isEmpty()){
            ConfiguredAction commandAfter = new ConfiguredAction();
            commandAfter.setActionId(COMMAND_AFTER_ID);
            commandAfter.setClassId(actionClassService.getClassId(ExecuteCommandAction.class));
            commandAfter.setNmbOfWorkers(1);
            commandAfter.setParameter(ExecuteCommandAction.PARAM_COMMAND, jobConfig.getCommandAfter());
            commandAfter.addReceiver(REPORTER, reporter);
            end.addReceiver(DEFAULT_RECEIVER, commandAfter);
            end = commandAfter;
        }

        end.addReceiver(DEFAULT_RECEIVER, reporter);
        end = reporter;

        ConfiguredAction endAction = new ConfiguredAction();
        endAction.setActionId(END_ACTION);
        endAction.setClassId(actionClassService.getClassId(M2AlfEndAction.class));
        endAction.setNmbOfWorkers(1);
        end.addReceiver(DEFAULT_RECEIVER, endAction);

		return start;
	}

    private void setParameters(List<String> parameters, ConfiguredAction metadataAction) {
        if(parameters == null)
            return;
        for(String param: parameters){
            String[] keyValue = param.split("\\|");
            metadataAction.setParameter(keyValue[0], keyValue[1]);
        }
    }

    private String encodeStringList(List<String> list){
        String str = "";
        for(String el: list){
            str += el +"|";
        }
        return str.substring(0, str.length()-1);
    }
}
