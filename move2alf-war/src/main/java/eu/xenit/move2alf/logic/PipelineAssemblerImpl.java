package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.core.action.*;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.simpleaction.*;
import eu.xenit.move2alf.core.sourcesink.DeleteOption;
import eu.xenit.move2alf.core.sourcesink.InputSource;
import eu.xenit.move2alf.core.sourcesink.WriteOption;
import eu.xenit.move2alf.logic.usageservice.UsageService;
import eu.xenit.move2alf.pipeline.actions.ActionConfig;
import eu.xenit.move2alf.web.dto.JobModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("pipelineAssembler")
public class PipelineAssemblerImpl extends PipelineAssembler implements ApplicationContextAware{

    public static final String SOURCE_ID= "Source";
    public static final String SOURCE_CMIS_ID = "SourceCMIS";
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
    public static final String UPLOADED_FILE_HANDLER = "UploadedFileHandler";
    public static final String PUT_CONTENT = "PutContent";
    public static final String BATCH_ACTION = "BatchAction";
    public static final String REPORT_SAVER = "reportSaver";
    public static final String PINNED_DISPATCHER = "pinned-dispatcher";
    public static final String ALFRESCO_ACL_ACTION = "AlfrescoAclAction";

    @Autowired
    private ActionClassInfoService actionClassService;

    @Autowired
	private UsageService usageService;


	private static final Logger logger = LoggerFactory
			.getLogger(PipelineAssemblerImpl.class);

	@Value(value = "#{'${default.batch.size}'}")
	private int defaultBatchSize;

    @Value(value="#{'${mail.from}'}")
    private String mailFrom;

    @Value(value="#{'${url}'}")
    private String url;


    private Map<String, ConfiguredAction> getAllConfiguredActions(ConfiguredAction action, Map<String, ConfiguredAction> configuredActionMap){
        configuredActionMap.put(action.getActionId(), action);
        for(ConfiguredAction receiver: action.getReceivers().values()){
            getAllConfiguredActions(receiver, configuredActionMap);
        }
        return configuredActionMap;
    }

	@Override
	public JobModel getJobConfigForJob(final int id) {
		final Job job = getJobService().getJob(id);
		final JobModel jobModel = new JobModel();
		jobModel.setId(id);
		jobModel.setName(job.getName());
		jobModel.setDescription(job.getDescription());
		ConfiguredAction startAction = job.getFirstConfiguredAction();

        Map<String, ConfiguredAction> configuredActionMap = getAllConfiguredActions(startAction, new HashMap<String, ConfiguredAction>());

        InputSource inputSource = InputSource.FILESYSTEM;
		List<String> inputFolder = new ArrayList();
        String cmisURL = "";
        String cmisUsername = "";
        String cmisPassword = "";
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
                inputSource = InputSource.FILESYSTEM;
                inputFolder = Arrays.asList(path.split("\\|"));
			} else if (SOURCE_CMIS_ID.equals(action.getActionId())) {
                inputSource = InputSource.CMIS;
                cmisURL = action.getParameter(SACMISInput.PARAM_CMIS_URL);
                cmisUsername = action.getParameter(SACMISInput.PARAM_CMIS_USERNAME);
                cmisPassword = action.getParameter(SACMISInput.PARAM_CMIS_PASSWORD);
            } else if (UPLOAD_ID.equals(action
                    .getActionId())) {
				destinationFolder = action.getParameter(AlfrescoUpload$.MODULE$.PARAM_PATH());
				dest = Integer.parseInt(action.getParameter(ActionWithDestination$.MODULE$.PARAM_DESTINATION()));
				writeOption = action.getParameter(AlfrescoUpload$.MODULE$.PARAM_WRITEOPTION());
				mode = Mode.WRITE;
			} else if (DELETE_ID
					.equals(action.getActionId())) {
				destinationFolder = action.getParameter(DeleteAction$.MODULE$.PARAM_PATH());
                dest = Integer.parseInt(action.getParameter(ActionWithDestination$.MODULE$.PARAM_DESTINATION()));
				deleteOption = action.getParameter(DeleteAction$.MODULE$.PARAM_DELETEOPTION());
				mode = Mode.DELETE;
			} else if (LIST_ID.equals(action
                    .getActionId())) {
				destinationFolder = action.getParameter(ListAction$.MODULE$.PARAM_PATH());
                dest = Integer.parseInt(action.getParameter(ActionWithDestination$.MODULE$.PARAM_DESTINATION()));
				ignorePath = false;
				mode = Mode.LIST;
			} else if (EXISTENCE_CHECK_ID.equals(action.getActionId())){
                ignorePath = true;
                mode = Mode.LIST;
                dest = Integer.parseInt(action.getParameter(ActionWithDestination$.MODULE$.PARAM_DESTINATION()));
                destinationFolder = action.getParameter(ListAction$.MODULE$.PARAM_PATH());
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
			} else if (END_ACTION
					.equals(action.getActionId())) {
				sendNotification = action.getParameter(M2AlfEndAction.PARAM_SENDERROR);
				emailAddressNotification = action
						.getParameter(M2AlfEndAction.PARAM_ERROR_TO);
				sendReport = action.getParameter(M2AlfEndAction.PARAM_SENDREPORT);
				emailAddressReport = action.getParameter(M2AlfEndAction.PARAM_REPORT_TO);
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

        jobModel.setInputSource(inputSource);
		jobModel.setInputFolder(inputFolder);
        jobModel.setCmisURL(cmisURL);
        jobModel.setCmisUsername(cmisUsername);
        jobModel.setCmisPassword(cmisPassword);
		jobModel.setDestinationFolder(destinationFolder);
		jobModel.setDest(dest);
		jobModel.setMode(mode);
		if(writeOption != null){
			jobModel.setWriteOption(WriteOption.valueOf(writeOption));
		}
		if(deleteOption != null){
			jobModel.setDeleteOption(DeleteOption.valueOf(deleteOption));
		}
		jobModel.setListIgnorePath(ignorePath);
		jobModel.setMetadata(metadata);
		jobModel.setTransform(transform);
		jobModel.setMoveBeforeProc(moveBeforeProcessing);
		jobModel.setMoveBeforeProcText(moveBeforeProcessingPath);
		jobModel.setMoveAfterLoad(moveAfterLoad);
		jobModel.setMoveAfterLoadText(moveAfterLoadPath);
		jobModel.setMoveNotLoad(moveNotLoaded);
		jobModel.setMoveNotLoadText(moveNotLoadedPath);
		jobModel.setSendNotification(sendNotification);
		jobModel.setSendNotificationText(emailAddressNotification);
		jobModel.setSendReport(sendReport);
		jobModel.setSendReportText(emailAddressReport);
		jobModel.setExtension(extension);
		jobModel.setCommand(commandBefore);
		jobModel.setCommandAfter(commandAfter);
		jobModel.setCron(getJobService().getCronjobsForJob(id));

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

		jobModel.setParamMetadata(paramMetadata);

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

		jobModel.setParamTransform(paramTransform);

		return jobModel;
	}


    @Override
    public ActionConfig getActionConfig(ConfiguredAction configuredAction){
        return configuredActionToActionConfig(configuredAction, new HashMap<String, ActionConfig>());
    }

    private ActionConfig configuredActionToActionConfig(ConfiguredAction configuredAction, Map<String, ActionConfig> actionConfigMap) {
        Map<String, ConfiguredAction> configuredActionReceivers = configuredAction.getReceivers();

        System.out.println("actionClassService=" + actionClassService + " and configuredAction=" + configuredAction + " and class info model=" + actionClassService.getClassInfoModel(configuredAction.getClassId()) + " for configuredAction.classId=" + configuredAction.getClassId());
        M2AActionFactory factory = new M2AActionFactory(actionClassService.getClassInfoModel(configuredAction.getClassId()).getClazz(), configuredAction.getParameters(), beanFactory);
        ActionConfig actionConfig = new ActionConfig(configuredAction.getActionId(), factory, configuredAction.getNmbOfWorkers());

        for(String key: configuredActionReceivers.keySet()){
            if(!actionConfigMap.containsKey(configuredActionReceivers.get(key).getActionId())){
                ActionConfig receiver = configuredActionToActionConfig(configuredActionReceivers.get(key), actionConfigMap);
                actionConfigMap.put(configuredActionReceivers.get(key).getActionId(), receiver);
            }
            actionConfig.addReceiver(key, actionConfigMap.get(configuredActionReceivers.get(key).getActionId()));
        }

        actionConfig.setDispatcher(configuredAction.getDispatcher());
        return actionConfig;
    }

    @Override
	public ConfiguredAction getConfiguredAction(final JobModel jobModel) {

        ConfiguredAction reportSaver = new ConfiguredAction();
        reportSaver.setActionId(REPORT_SAVER);
        reportSaver.setNmbOfWorkers(4);
        reportSaver.setClassId(actionClassService.getClassId(SAReport.class));
        reportSaver.setDispatcher(PINNED_DISPATCHER);

        ConfiguredAction reporter = new ConfiguredAction();
        reporter.setActionId(REPORTER);
        reporter.setNmbOfWorkers(1);
        reporter.setClassId(actionClassService.getClassId(BatchReportsAction.class));
        reporter.setParameter(BatchReportsAction.PARAM_BATCHSIZE, Integer.toString(50));
        reporter.addReceiver(DEFAULT_RECEIVER, reportSaver);

        ConfiguredAction start = new ConfiguredAction();
        start.setActionId(START);
        start.setNmbOfWorkers(1);
        start.setClassId(actionClassService.getClassId(StartCycleAction.class));

        ConfiguredAction end = start;

        if(!jobModel.getCommand().isEmpty()){
            ConfiguredAction executeCommandBefore = new ConfiguredAction();
            executeCommandBefore.setActionId(COMMAND_BEFORE_ID);
            executeCommandBefore.setClassId(actionClassService.getClassId(ExecuteCommandAction.class));
            executeCommandBefore.setNmbOfWorkers(1);
            executeCommandBefore.setParameter(ExecuteCommandAction.PARAM_COMMAND, jobModel.getCommand());
            executeCommandBefore.addReceiver(REPORTER, reporter);
            end.addReceiver(DEFAULT_RECEIVER, executeCommandBefore);
            end = executeCommandBefore;
        }

        ConfiguredAction sourceAction = new ConfiguredAction();
        if (jobModel.getInputSource() == null || InputSource.FILESYSTEM.equals(jobModel.getInputSource())) {
            sourceAction.setActionId(SOURCE_ID);
            sourceAction.setClassId(actionClassService.getClassId(SASource.class));
            sourceAction.setParameter(SASource.PARAM_INPUTPATHS, encodeStringList(jobModel.getInputFolder()));
        } else {
            sourceAction.setActionId(SOURCE_CMIS_ID);
            sourceAction.setClassId(actionClassService.getClassId(SACMISInput.class));
            sourceAction.setParameter(SACMISInput.PARAM_CMIS_URL, jobModel.getCmisURL());
            sourceAction.setParameter(SACMISInput.PARAM_CMIS_USERNAME, jobModel.getCmisUsername());
            sourceAction.setParameter(SACMISInput.PARAM_CMIS_PASSWORD, jobModel.getCmisPassword());
            sourceAction.setDispatcher(PINNED_DISPATCHER);

        }
        sourceAction.setNmbOfWorkers(1);
        sourceAction.addReceiver(REPORTER, reporter);
        end.addReceiver(DEFAULT_RECEIVER, sourceAction);
        end = sourceAction;

        if(jobModel.getMoveBeforeProc()){
            ConfiguredAction moveAction = new ConfiguredAction();
            moveAction.setActionId(MOVE_BEFORE_ID);
            moveAction.setClassId(actionClassService.getClassId(MoveAction.class));
            moveAction.setNmbOfWorkers(1);
            moveAction.setParameter(MoveAction.PARAM_PATH, jobModel.getMoveBeforeProcText());
            moveAction.addReceiver(REPORTER, reporter);
            end.addReceiver(DEFAULT_RECEIVER, moveAction);
            end = moveAction;
        }

        if(!jobModel.getExtension().isEmpty() && !jobModel.getExtension().equals("*")){
            ConfiguredAction filter = new ConfiguredAction();
            filter.setActionId(FILTER_ID);
            filter.setClassId(actionClassService.getClassId(SAFilter.class));
            filter.setNmbOfWorkers(1);
            filter.setParameter(SAFilter.PARAM_EXTENSION, jobModel.getExtension());
            filter.addReceiver(REPORTER, reporter);
            end.addReceiver(DEFAULT_RECEIVER, filter);
            end = filter;
        }

        String classId = jobModel.getMetadata();
        ConfiguredAction metadataAction = new ConfiguredAction();
        metadataAction.setActionId(METADATA_ACTION_ID);
        metadataAction.setClassId(classId);
        metadataAction.setNmbOfWorkers(1);
        metadataAction.addReceiver(REPORTER, reporter);
        setParameters(jobModel.getParamMetadata(), metadataAction);
        end.addReceiver(DEFAULT_RECEIVER, metadataAction);
        end = metadataAction;

		if (!("notransformation".equals(jobModel.getTransform()) || ""
				.equals(jobModel.getTransform()))) {
			logger.debug("getTransform() == \"{}\"", jobModel.getTransform());

            ConfiguredAction transformAction = new ConfiguredAction();
            transformAction.setActionId(TRANSFORM_ACTION_ID);
            transformAction.setClassId(jobModel.getTransform());
            transformAction.setNmbOfWorkers(1);
            transformAction.addReceiver(REPORTER, reporter);
            setParameters(jobModel.getParamTransform(), transformAction);
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


		if (Mode.WRITE == jobModel.getMode()) {
            final ConfiguredAction putContentAction = new ConfiguredAction();
            putContentAction.setActionId(PUT_CONTENT);
            putContentAction.setClassId(actionClassService.getClassId(PutContentAction.class));
            putContentAction.setNmbOfWorkers(1);
            putContentAction.addReceiver(REPORTER, reporter);
            putContentAction.setParameter(ActionWithDestination$.MODULE$.PARAM_DESTINATION(), String.valueOf(jobModel.getDest()));
            end.addReceiver(DEFAULT_RECEIVER, putContentAction);
            end = putContentAction;

            final ConfiguredAction batchAction = new ConfiguredAction();
            batchAction.setActionId(BATCH_ACTION);
            batchAction.setClassId(actionClassService.getClassId(BatchAction.class));
            batchAction.setNmbOfWorkers(1);
            batchAction.addReceiver(REPORTER, reporter);
            batchAction.setParameter(BatchAction.PARAM_BATCHSIZE, String.valueOf(defaultBatchSize));
            end.addReceiver(DEFAULT_RECEIVER, batchAction);
            end = batchAction;

            final ConfiguredAction uploadAction = new ConfiguredAction();
            uploadAction.setActionId(UPLOAD_ID);
            uploadAction.setClassId(actionClassService.getClassId(AlfrescoUpload.class));
            uploadAction.setNmbOfWorkers(1);
            uploadAction.addReceiver(REPORTER, reporter);
            uploadAction.setParameter(ActionWithDestination$.MODULE$.PARAM_DESTINATION(), String.valueOf(jobModel.getDest()));
			uploadAction.setParameter(AlfrescoUpload$.MODULE$.PARAM_PATH(), jobModel.getDestinationFolder());
            uploadAction.setParameter(AlfrescoUpload$.MODULE$.PARAM_WRITEOPTION(), jobModel.getWriteOption().toString());
			end.addReceiver(DEFAULT_RECEIVER, uploadAction);
            end = uploadAction;

            final ConfiguredAction aclAction = new ConfiguredAction();
            aclAction.setActionId(ALFRESCO_ACL_ACTION);
            aclAction.setClassId(actionClassService.getClassId(AlfrescoACLAction.class));
            aclAction.setNmbOfWorkers(1);
            aclAction.addReceiver(REPORTER, reporter);
            end.addReceiver(DEFAULT_RECEIVER, aclAction);
            end = aclAction;
        }

		if (Mode.DELETE  == jobModel.getMode()) {
            final ConfiguredAction deleteAction = new ConfiguredAction();
            deleteAction.setActionId(DELETE_ID);
            deleteAction.setClassId(actionClassService.getClassId(DeleteAction.class));
            deleteAction.setNmbOfWorkers(1);
            deleteAction.addReceiver(REPORTER, reporter);
            deleteAction.setParameter(ActionWithDestination$.MODULE$.PARAM_DESTINATION(), String.valueOf(jobModel.getDest()));
            deleteAction.setParameter(DeleteAction$.MODULE$.PARAM_PATH(), jobModel.getDestinationFolder());
            deleteAction.setParameter(DeleteAction$.MODULE$.PARAM_DELETEOPTION(), jobModel.getDeleteOption().toString());
            end.addReceiver(DEFAULT_RECEIVER, deleteAction);
            end = deleteAction;
		}
		if (Mode.LIST == jobModel.getMode()){

            ConfiguredAction listAction;
			
			if(jobModel.getListIgnorePath()){
				listAction = new ConfiguredAction();
                listAction.setActionId(EXISTENCE_CHECK_ID);
                listAction.setClassId(actionClassService.getClassId(ExistenceCheck.class));
			}
			else{
				listAction = new ConfiguredAction();
                listAction.setActionId(LIST_ID);
                listAction.setClassId(actionClassService.getClassId(ListAction.class));
			}
            listAction.setNmbOfWorkers(1);
            listAction.setParameter(ListAction$.MODULE$.PARAM_PATH(), jobModel.getDestinationFolder());
            listAction.addReceiver(REPORTER, reporter);
			listAction.setParameter(ActionWithDestination$.MODULE$.PARAM_DESTINATION(), String.valueOf(jobModel.getDest()));
            end.addReceiver(DEFAULT_RECEIVER, listAction);
            end = listAction;
		}

        final ConfiguredAction uploadedFileHandler = new ConfiguredAction();
        uploadedFileHandler.setClassId(actionClassService.getClassId(UploadedFileHandler.class));
        uploadedFileHandler.setActionId(UPLOADED_FILE_HANDLER);
        uploadedFileHandler.addReceiver(REPORTER, reporter);
        uploadedFileHandler.setNmbOfWorkers(1);
        end.addReceiver(DEFAULT_RECEIVER, uploadedFileHandler);
        end = uploadedFileHandler;

        if(jobModel.getMoveAfterLoad()){
            final ConfiguredAction moveAfterLoad = new ConfiguredAction();
            moveAfterLoad.setClassId(actionClassService.getClassId(MoveAction.class));
            moveAfterLoad.setActionId(MOVE_AFTER_ID);
            moveAfterLoad.setNmbOfWorkers(1);
            moveAfterLoad.setParameter(MoveAction.PARAM_PATH, jobModel.getMoveAfterLoadText());
            moveAfterLoad.addReceiver(REPORTER, reporter);
            uploadedFileHandler.addReceiver(MOVE_AFTER_ID, moveAfterLoad);
        }

        if(jobModel.getMoveNotLoad()){
            final ConfiguredAction moveNotLoaded = new ConfiguredAction();
            moveNotLoaded.setClassId(actionClassService.getClassId(MoveAction.class));
            moveNotLoaded.setActionId(MOVE_NOT_LOADED_ID);
            moveNotLoaded.setNmbOfWorkers(1);
            moveNotLoaded.setParameter(MoveAction.PARAM_PATH, jobModel.getMoveNotLoadText());
            moveNotLoaded.addReceiver(REPORTER, reporter);
            uploadedFileHandler.addReceiver(MOVE_NOT_LOADED_ID, moveNotLoaded);
        }


        if(!jobModel.getCommandAfter().isEmpty()){
            ConfiguredAction commandAfter = new ConfiguredAction();
            commandAfter.setActionId(COMMAND_AFTER_ID);
            commandAfter.setClassId(actionClassService.getClassId(ExecuteCommandAction.class));
            commandAfter.setNmbOfWorkers(1);
            commandAfter.setParameter(ExecuteCommandAction.PARAM_COMMAND, jobModel.getCommandAfter());
            commandAfter.addReceiver(REPORTER, reporter);
            end.addReceiver(DEFAULT_RECEIVER, commandAfter);
            end = commandAfter;
        }

        end.addReceiver(DEFAULT_RECEIVER, reporter);
        end = reportSaver;

        ConfiguredAction endAction = new ConfiguredAction();
        endAction.setActionId(END_ACTION);
        endAction.setClassId(actionClassService.getClassId(M2AlfEndAction.class));
        endAction.setNmbOfWorkers(1);
        endAction.setParameter(M2AlfEndAction.PARAM_SENDREPORT, Boolean.toString(jobModel.getSendReport()));
        endAction.setParameter(M2AlfEndAction.PARAM_REPORT_TO, jobModel.getSendReportText());
        endAction.setParameter(M2AlfEndAction.PARAM_SENDERROR, Boolean.toString(jobModel.getSendNotification()));
        endAction.setParameter(M2AlfEndAction.PARAM_ERROR_TO, jobModel.getSendNotificationText());
        endAction.setParameter(M2AlfEndAction.PARAM_MAILFROM, mailFrom);
        endAction.setParameter(M2AlfEndAction.PARAM_URL, url);
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

    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }
}
