package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.core.action.*;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.sharedresource.alfresco.DeleteOption;
import eu.xenit.move2alf.core.sharedresource.alfresco.InputSource;
import eu.xenit.move2alf.core.sharedresource.alfresco.WriteOption;
import eu.xenit.move2alf.core.simpleaction.*;
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
    public static final String MOVE_WITH_COUNTER = "MoveWithCounter";
    public static final String MOVE_NOT_LOADED_ID = "MoveNotLoaded";
    public static final String FILTER_ID = "Filter";
    public static final String METADATA_ACTION_ID = "MetadataAction";
    public static final String TRANSFORM_ACTION_ID = "TransformAction";
    public static final String MIME_TYPE_ID = "MimeType";
    public static final String UPLOAD_ID = "Upload";
    public static final String DELETE_ID = "Delete";
    public static final String EXISTENCE_CHECK_ID = "ExistenceCheck";
    public static final String LIST_ID = "List";
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
    public static final String VALIDATE_DESTINATION = "ValidateDestination";
    public static final String RECURSIVE_CMIS = "RecursiveCmis" ;
    public static final String CMIS_QUERY = "CmisQuery" ;

    // name/values for metadata and transform parameters are kept in a single string using this separator
    public static final String SEPARATOR = "@@@";

    public void setActionClassService(ActionClassInfoService actionClassService) {
        this.actionClassService = actionClassService;
    }

    @Autowired
    private ActionClassInfoService actionClassService;

	private static final Logger logger = LoggerFactory.getLogger(PipelineAssemblerImpl.class);

	@Value(value = "#{'${default.batch.size}'}")
	private int defaultBatchSize;

    @Value(value="#{'${mail.from}'}")
    private String mailFrom;

    @Value(value="#{'${url}'}")
    private String url;


    private Map<String, ConfiguredAction> getAllConfiguredActions(ConfiguredAction action, Map<String, ConfiguredAction> configuredActionMap){
        configuredActionMap.put(action.getActionId(), action);
        for(ConfiguredAction receiver: action.getReceivers().values()){
            if(!configuredActionMap.containsKey(receiver.getActionId()))
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
        String cmisQuery = "";
        boolean cmisRecursive = false;
		String destinationFolder = "";
		int dest = 0;
        int contentStoreId = -1;
		String writeOption = null;
		String deleteOption = null;
		Mode mode = null;
		boolean ignorePath = false;

        boolean skipContentUpload = false;
		String metadata = "";
		String transform = "";
		boolean moveBeforeProcessing = false;
		String moveBeforeProcessingPath = "";
		boolean moveAfterLoad = false;
		String moveAfterLoadPath = "";
		boolean moveNotLoaded = false;
		String moveNotLoadedPath = "";
		String extension = "*";
		String commandBefore = "";
		String commandAfter = "";
		Map<String, String> metadataParameterMap = new HashMap<String, String>();
		Map<String, String> transformParameterMap = new HashMap<String, String>();

		for(ConfiguredAction action: configuredActionMap.values())
            if (SOURCE_ID.equals(action
                    .getActionId())) {
                String path = action.getParameter(SASource.PARAM_INPUTPATHS);
                inputSource = InputSource.FILESYSTEM;
                inputFolder = Arrays.asList(path.split("\\|"));
                skipContentUpload = Boolean.valueOf(action.getParameter(SACMISInput.PARAM_SKIP_CONTENT_UPLOAD));
            } else if (SOURCE_CMIS_ID.equals(action.getActionId())) {
                inputSource = InputSource.CMIS;
                cmisURL = action.getParameter(SACMISInput.PARAM_CMIS_URL);
                cmisUsername = action.getParameter(SACMISInput.PARAM_CMIS_USERNAME);
                cmisPassword = action.getParameter(SACMISInput.PARAM_CMIS_PASSWORD);
                cmisQuery = action.getParameter(SACMISInput.PARAM_CMIS_QUERY);
                skipContentUpload = Boolean.valueOf(action.getParameter(SACMISInput.PARAM_SKIP_CONTENT_UPLOAD));
                cmisRecursive = Boolean.valueOf(action.getParameter(SACMISInput.PARAM_RECURSIVE));
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
            } else if (EXISTENCE_CHECK_ID.equals(action.getActionId())) {
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
                moveAfterLoadPath = action.getParameter(MoveAction.PARAM_PATH);
            } else if (MOVE_NOT_LOADED_ID.equals(action.getActionId())) {
                moveNotLoaded = true;
                moveNotLoadedPath = action.getParameter(MoveAction.PARAM_PATH);
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
            } else if (PUT_CONTENT.equals(action.getActionId())) {
                contentStoreId = Integer.parseInt(action.getParameter(PutContentAction.PARAM_DESTINATION()));
            }

        jobModel.setSkipContentUpload(skipContentUpload);
        jobModel.setInputSource(inputSource);
		jobModel.setInputFolder(inputFolder);
        jobModel.setCmisURL(cmisURL);
        jobModel.setCmisUsername(cmisUsername);
        jobModel.setCmisPassword(cmisPassword);
        jobModel.setCmisQuery(cmisQuery);
        jobModel.setCmisRecursive(cmisRecursive);
		jobModel.setDestinationFolder(destinationFolder);
		jobModel.setDest(dest);
        jobModel.setContentStoreId(contentStoreId);
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
		jobModel.setSendNotification(job.isSendErrorReport());
		jobModel.setSendNotificationText(job.getSendErrorReportTo());
		jobModel.setSendReport(job.isSendReport());
        jobModel.setSendReportText(job.getSendReportTo());
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

			final String listValue = nameValuePair.getKey() + SEPARATOR
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
			final String listValue = nameValuePair.getKey() + SEPARATOR
					+ nameValuePair.getValue();
			logger.debug("transform list value = " + listValue);
			paramTransform.add(listValue);
		}

		jobModel.setParamTransform(paramTransform);

		return jobModel;
	}

    @Override
    public ActionConfig getActionConfig(ConfiguredAction configuredAction){
        HashMap<String, ActionConfig> map = new HashMap<String, ActionConfig>();
        M2AActionFactory factory = new M2AActionFactory(actionClassService.getClassInfoModel(configuredAction.getClassId()).getClazz(), configuredAction.getParameters(), beanFactory);
        ActionConfig actionConfig = new ActionConfig(configuredAction.getActionId(), factory, configuredAction.getNmbOfWorkers());
        map.put(configuredAction.getActionId(), actionConfig);
        configuredActionToActionConfig(configuredAction, map);
        return actionConfig;
    }

    private ActionConfig configuredActionToActionConfig(ConfiguredAction configuredAction, Map<String, ActionConfig> actionConfigMap) {
        Map<String, ConfiguredAction> configuredActionReceivers = configuredAction.getReceivers();
        ActionConfig actionConfig = actionConfigMap.get(configuredAction.getActionId());

        if(configuredActionReceivers != null) {
            for(String key: configuredActionReceivers.keySet()){
                ConfiguredAction configuredActionReceiver = configuredActionReceivers.get(key);
                if(!actionConfigMap.containsKey(configuredActionReceiver.getActionId())){
                    M2AActionFactory factory = new M2AActionFactory(actionClassService.getClassInfoModel(configuredActionReceiver.getClassId()).getClazz(), configuredActionReceiver.getParameters(), beanFactory);
                    ActionConfig receiver = new ActionConfig(configuredActionReceiver.getActionId(), factory, configuredActionReceiver.getNmbOfWorkers());
                    actionConfigMap.put(configuredActionReceivers.get(key).getActionId(), receiver);
                    configuredActionToActionConfig(configuredActionReceivers.get(key), actionConfigMap);
                }
                actionConfig.addReceiver(key, actionConfigMap.get(configuredActionReceiver.getActionId()));
            }
        }

        actionConfig.setDispatcher(configuredAction.getDispatcher());
        return actionConfig;
    }

    @Autowired
    private DestinationService destinationService;

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

        ConfiguredAction validateAction = new ConfiguredAction();
        validateAction.setActionId(VALIDATE_DESTINATION);
        validateAction.setClassId(actionClassService.getClassId(ValidateAction.class));
        validateAction.setNmbOfWorkers(1);
        validateAction.addReceiver(REPORTER, reporter);
        validateAction.setParameter(ActionWithDestination$.MODULE$.PARAM_DESTINATION(), String.valueOf(jobModel.getDest()));
        end.addReceiver(DEFAULT_RECEIVER, validateAction);
        end = validateAction;

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

        boolean filesystemSource = false;
        ConfiguredAction sourceAction = new ConfiguredAction();
        if (jobModel.getInputSource() == null || InputSource.FILESYSTEM.equals(jobModel.getInputSource())) {
            filesystemSource = true;
            sourceAction.setActionId(SOURCE_ID);
            sourceAction.setClassId(actionClassService.getClassId(SASource.class));
            sourceAction.setParameter(SASource.PARAM_INPUTPATHS, encodeStringList(jobModel.getInputFolder()));
        } else {
            sourceAction.setActionId(SOURCE_CMIS_ID);
            sourceAction.setClassId(actionClassService.getClassId(SACMISInput.class));
            sourceAction.setParameter(SACMISInput.PARAM_CMIS_URL, jobModel.getCmisURL());
            sourceAction.setParameter(SACMISInput.PARAM_CMIS_USERNAME, jobModel.getCmisUsername());
            sourceAction.setParameter(SACMISInput.PARAM_CMIS_PASSWORD, jobModel.getCmisPassword());
            sourceAction.setParameter(SACMISInput.PARAM_CMIS_QUERY, jobModel.getCmisQuery());
            sourceAction.setParameter(SACMISInput.PARAM_RECURSIVE, String.valueOf(jobModel.getCmisRecursive()));
            sourceAction.setDispatcher(PINNED_DISPATCHER);
        }
        sourceAction.setParameter(SACMISInput.PARAM_SKIP_CONTENT_UPLOAD, String.valueOf(jobModel.getSkipContentUpload()));
        sourceAction.setNmbOfWorkers(1);
        sourceAction.addReceiver(REPORTER, reporter);
        end.addReceiver(DEFAULT_RECEIVER, sourceAction);
        end = sourceAction;

        if(jobModel.getCmisRecursive()) {
            ConfiguredAction cmisRecursive = new ConfiguredAction();
            cmisRecursive.setActionId(RECURSIVE_CMIS);
            cmisRecursive.setClassId(actionClassService.getClassId(RecursiveCmis.class));
            cmisRecursive.setParameter(SACMISInput.PARAM_CMIS_QUERY, jobModel.getCmisQuery());
            cmisRecursive.setNmbOfWorkers(1);
            cmisRecursive.addReceiver(REPORTER, reporter);
            end.addReceiver(DEFAULT_RECEIVER, cmisRecursive);
            end = cmisRecursive;

            ConfiguredAction cmisQuery = new ConfiguredAction();
            cmisQuery.setActionId(CMIS_QUERY);
            cmisQuery.setClassId(actionClassService.getClassId(CmisQuery.class));
            cmisQuery.setParameter(SACMISInput.PARAM_CMIS_URL, jobModel.getCmisURL());
            cmisQuery.setParameter(SACMISInput.PARAM_CMIS_USERNAME, jobModel.getCmisUsername());
            cmisQuery.setParameter(SACMISInput.PARAM_CMIS_PASSWORD, jobModel.getCmisPassword());
            cmisQuery.setParameter(SACMISInput.PARAM_SKIP_CONTENT_UPLOAD, String.valueOf(jobModel.getSkipContentUpload()));
            cmisQuery.setNmbOfWorkers(1);
            cmisQuery.addReceiver(REPORTER, reporter);
            cmisQuery.addReceiver(RECURSIVE_CMIS, cmisRecursive);
            end.addReceiver(DEFAULT_RECEIVER, cmisQuery);
            end = cmisQuery;

        }

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
        setParameters(jobModel.getParamMetadata(), metadataAction);
        metadataAction.addReceiver(REPORTER, reporter);
        end.addReceiver(DEFAULT_RECEIVER, metadataAction);
        end = metadataAction;

        ConfiguredAction moveWithCounter = new ConfiguredAction();
        moveWithCounter.setClassId(actionClassService.getClassId(MoveWithCounterAction.class));
        moveWithCounter.setActionId(MOVE_AFTER_ID);
        moveWithCounter.setNmbOfWorkers(1);
        moveWithCounter.setParameter(MoveAction.PARAM_PATH, jobModel.getMoveAfterLoadText());
        moveWithCounter.addReceiver(REPORTER, reporter);

        Class metadataClass = actionClassService.getClassInfoModel(jobModel.getMetadata()).getClazz();
        if(FileWithMetadataAction.class.isAssignableFrom(metadataClass)) {
            metadataAction.addReceiver(MOVE_WITH_COUNTER, moveWithCounter);
        }



        if (!("notransformation".equals(jobModel.getTransform()) || ""
				.equals(jobModel.getTransform()))) {
			logger.debug("getTransform() == \"{}\"", jobModel.getTransform());

            ConfiguredAction transformAction = new ConfiguredAction();
            transformAction.setActionId(TRANSFORM_ACTION_ID);
            transformAction.setClassId(jobModel.getTransform());
            transformAction.setNmbOfWorkers(1);
            setParameters(jobModel.getParamTransform(), transformAction);
            transformAction.addReceiver(REPORTER, reporter);
            end.addReceiver(DEFAULT_RECEIVER, transformAction);
            end = transformAction;
		}

        if(!jobModel.getSkipContentUpload() && filesystemSource) {
            ConfiguredAction mimeTypeAction = new ConfiguredAction();
            mimeTypeAction.setActionId(MIME_TYPE_ID);
            mimeTypeAction.setClassId(actionClassService.getClassId(SAMimeType.class));
            mimeTypeAction.setNmbOfWorkers(1);
            mimeTypeAction.addReceiver(REPORTER, reporter);
            end.addReceiver(DEFAULT_RECEIVER, mimeTypeAction);
            end = mimeTypeAction;
        }


		if (Mode.WRITE == jobModel.getMode()) {
            if(!jobModel.getSkipContentUpload()) {
                final ConfiguredAction putContentAction = new ConfiguredAction();
                putContentAction.setActionId(PUT_CONTENT);
                putContentAction.setClassId(actionClassService.getClassId(PutContentAction.class));
                putContentAction.setNmbOfWorkers(1);
                putContentAction.addReceiver(REPORTER, reporter);
                int destinationId = jobModel.getDest();
                if(jobModel.getContentStoreId() != -1){
                    destinationId = jobModel.getContentStoreId();
                }
                putContentAction.setParameter(ActionWithDestination$.MODULE$.PARAM_DESTINATION(), String.valueOf(destinationId));
                end.addReceiver(DEFAULT_RECEIVER, putContentAction);
                end = putContentAction;
            }

            final ConfiguredAction batchAction = new ConfiguredAction();
            batchAction.setActionId(BATCH_ACTION);
            batchAction.setClassId(actionClassService.getClassId(BatchAction.class));
            batchAction.setNmbOfWorkers(1);
            batchAction.setParameter(BatchAction.PARAM_BATCHSIZE, String.valueOf(defaultBatchSize));
            batchAction.addReceiver(REPORTER, reporter);
            end.addReceiver(DEFAULT_RECEIVER, batchAction);
            end = batchAction;

            final ConfiguredAction uploadAction = new ConfiguredAction();
            uploadAction.setActionId(UPLOAD_ID);
            uploadAction.setClassId(actionClassService.getClassId(AlfrescoUpload.class));
            uploadAction.setNmbOfWorkers(1);
            uploadAction.setParameter(ActionWithDestination$.MODULE$.PARAM_DESTINATION(), String.valueOf(jobModel.getDest()));
            uploadAction.setParameter(AlfrescoUpload$.MODULE$.PARAM_PATH(), jobModel.getDestinationFolder());
            uploadAction.setParameter(AlfrescoUpload$.MODULE$.PARAM_WRITEOPTION(), jobModel.getWriteOption().toString());
            uploadAction.addReceiver(REPORTER, reporter);
			end.addReceiver(DEFAULT_RECEIVER, uploadAction);
            end = uploadAction;

            final ConfiguredAction aclAction = new ConfiguredAction();
            aclAction.setActionId(ALFRESCO_ACL_ACTION);
            aclAction.setClassId(actionClassService.getClassId(AlfrescoACLAction.class));
            aclAction.setNmbOfWorkers(1);
            aclAction.setParameter(ActionWithDestination$.MODULE$.PARAM_DESTINATION(), String.valueOf(jobModel.getDest()));
            aclAction.addReceiver(REPORTER, reporter);
            end.addReceiver(DEFAULT_RECEIVER, aclAction);
            end = aclAction;
        }

		if (Mode.DELETE  == jobModel.getMode()) {
            final ConfiguredAction deleteAction = new ConfiguredAction();
            deleteAction.setActionId(DELETE_ID);
            deleteAction.setClassId(actionClassService.getClassId(DeleteAction.class));
            deleteAction.setNmbOfWorkers(1);
            deleteAction.setParameter(ActionWithDestination$.MODULE$.PARAM_DESTINATION(), String.valueOf(jobModel.getDest()));
            deleteAction.setParameter(DeleteAction$.MODULE$.PARAM_PATH(), jobModel.getDestinationFolder());
            deleteAction.setParameter(DeleteAction$.MODULE$.PARAM_DELETEOPTION(), jobModel.getDeleteOption().toString());
            deleteAction.addReceiver(REPORTER, reporter);
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
            listAction.setParameter(ActionWithDestination$.MODULE$.PARAM_DESTINATION(), String.valueOf(jobModel.getDest()));
            listAction.addReceiver(REPORTER, reporter);
            end.addReceiver(DEFAULT_RECEIVER, listAction);
            end = listAction;
		}

        final ConfiguredAction uploadedFileHandler = new ConfiguredAction();
        uploadedFileHandler.setClassId(actionClassService.getClassId(UploadedFileHandler.class));
        uploadedFileHandler.setActionId(UPLOADED_FILE_HANDLER);
        uploadedFileHandler.setNmbOfWorkers(1);
        uploadedFileHandler.addReceiver(REPORTER, reporter);
        end.addReceiver(DEFAULT_RECEIVER, uploadedFileHandler);
        end = uploadedFileHandler;

        if(jobModel.getMoveAfterLoad()){
            if(FileWithMetadataAction.class.isAssignableFrom(metadataClass)) {
                end.addReceiver(MOVE_AFTER_ID,moveWithCounter);
            } else {
                final ConfiguredAction moveAfterLoad = new ConfiguredAction();
                moveAfterLoad.setClassId(actionClassService.getClassId(MoveAction.class));
                moveAfterLoad.setActionId(MOVE_AFTER_ID);
                moveAfterLoad.setNmbOfWorkers(1);
                moveAfterLoad.setParameter(MoveAction.PARAM_PATH, jobModel.getMoveAfterLoadText());
                moveAfterLoad.addReceiver(REPORTER, reporter);
                end.addReceiver(MOVE_AFTER_ID, moveAfterLoad);
            }
        }

        if(jobModel.getMoveNotLoad()){
            final ConfiguredAction moveNotLoaded = new ConfiguredAction();
            moveNotLoaded.setClassId(actionClassService.getClassId(MoveAction.class));
            moveNotLoaded.setActionId(MOVE_NOT_LOADED_ID);
            moveNotLoaded.setNmbOfWorkers(1);
            moveNotLoaded.setParameter(MoveAction.PARAM_PATH, jobModel.getMoveNotLoadText());
            moveNotLoaded.addReceiver(REPORTER, reporter);
            end.addReceiver(MOVE_NOT_LOADED_ID, moveNotLoaded);
        }


        if(!jobModel.getCommandAfter().isEmpty()){
            ConfiguredAction commandAfter = new ConfiguredAction();
            commandAfter.setActionId(COMMAND_AFTER_ID);
            commandAfter.setClassId(actionClassService.getClassId(ExecuteCommandAction.class));
            commandAfter.setNmbOfWorkers(1);
            commandAfter.setParameter(ExecuteCommandAction.PARAM_COMMAND, jobModel.getCommandAfter());
            commandAfter.addReceiver(REPORTER, reporter);
            end.addReceiver(COMMAND_AFTER_ID, commandAfter);
        }

        end.addReceiver(DEFAULT_RECEIVER, reporter);
        end = reportSaver;

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
            logger.debug("param=" + param);
            String[] keyValue = param.split(SEPARATOR);
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
