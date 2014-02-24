package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.core.action.*;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.simpleaction.SACMISInput;
import eu.xenit.move2alf.core.simpleaction.SAReport;

/**
 * Created with IntelliJ IDEA.
 * User: rox
 * Date: 2/24/14
 * Time: 11:32 AM
 */
public class PipelineAssemblerImplTest {

    ConfiguredAction mockConfiguredAction() {
        ConfiguredAction reportSaver = new ConfiguredAction();
        reportSaver.setActionId(PipelineAssemblerImpl.REPORT_SAVER);

        ConfiguredAction reporter = new ConfiguredAction();
        reporter.setActionId(PipelineAssemblerImpl.REPORTER);
        reporter.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, reportSaver);

        ConfiguredAction start = new ConfiguredAction();
        start.setActionId(PipelineAssemblerImpl.START);

        ConfiguredAction end = start;

        ConfiguredAction validateAction = new ConfiguredAction();
        validateAction.setActionId(PipelineAssemblerImpl.VALIDATE_DESTINATION);
        validateAction.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        end.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, validateAction);
        end = validateAction;

        ConfiguredAction sourceAction = new ConfiguredAction();
        sourceAction.setActionId(PipelineAssemblerImpl.SOURCE_CMIS_ID);
        sourceAction.setNmbOfWorkers(1);
        sourceAction.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        end.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, sourceAction);


        ConfiguredAction cmisRecursive = new ConfiguredAction();
        cmisRecursive.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        end.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, cmisRecursive);
        end = cmisRecursive;

        ConfiguredAction cmisQuery = new ConfiguredAction();
        cmisQuery.setActionId(PipelineAssemblerImpl.CMIS_QUERY);
        cmisQuery.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        cmisQuery.addReceiver(PipelineAssemblerImpl.RECURSIVE_CMIS, cmisRecursive);
        end.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, cmisQuery);
        end = cmisQuery;

        cmisRecursive.addReceiver(PipelineAssemblerImpl.RECURSIVE_RECEIVER, cmisQuery);

        ConfiguredAction metadataAction = new ConfiguredAction();
        metadataAction.setActionId(PipelineAssemblerImpl.METADATA_ACTION_ID);
        metadataAction.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        end.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, metadataAction);
        end = metadataAction;

        C/*onfiguredAction listAction;

            listAction = new ConfiguredAction();
            listAction.setActionId(EXISTENCE_CHECK_ID);
        listAction.setNmbOfWorkers(1);
        listAction.setParameter(ListAction$.MODULE$.PARAM_PATH(), jobModel.getDestinationFolder());
        listAction.setParameter(ActionWithDestination$.MODULE$.PARAM_DESTINATION(), String.valueOf(jobModel.getDest()));
        listAction.addReceiver(REPORTER, reporter);
        end.addReceiver(DEFAULT_RECEIVER, listAction);
        end = listAction;

        ConfiguredAction endAction = new ConfiguredAction();
        endAction.setActionId(END_ACTION);
        endAction.setClassId(actionClassService.getClassId(M2AlfEndAction.class));
        endAction.setNmbOfWorkers(1);
        endAction.setParameter(M2AlfEndAction.PARAM_SENDREPORT, Boolean.toString(jobModel.getSendReport()));
        endAction.setParameter(M2AlfEndAction.PARAM_REPORT_TO, jobModel.getSendReportText());
        endAction.setParameter(M2AlfEndAction.PARAM_SENDERROR, Boolean.toString(jobModel.getSendNotification()));
        endAction.setParameter(M2AlfEndAction.PARAM_ERROR_TO, jobModel.getSendNotificationText());
        end.addReceiver(DEFAULT_RECEIVER, endAction);*/
    }

    void getActionConfigTest(){
        PipelineAssemblerImpl pipeline = new PipelineAssemblerImpl();

    }
}
