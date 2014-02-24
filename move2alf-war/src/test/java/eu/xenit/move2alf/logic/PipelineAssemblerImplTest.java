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

        ConfiguredAction validateAction = new ConfiguredAction();
        validateAction.setActionId(PipelineAssemblerImpl.VALIDATE_DESTINATION);
        validateAction.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        start.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, validateAction);

        ConfiguredAction sourceAction = new ConfiguredAction();
        sourceAction.setActionId(PipelineAssemblerImpl.SOURCE_CMIS_ID);
        sourceAction.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        validateAction.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, sourceAction);

        // a loop from cmisRecursive to cmisQuery and back
        ConfiguredAction cmisRecursive = new ConfiguredAction();
        cmisRecursive.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        sourceAction.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, cmisRecursive);


        ConfiguredAction cmisQuery = new ConfiguredAction();
        cmisQuery.setActionId(PipelineAssemblerImpl.CMIS_QUERY);
        cmisQuery.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        cmisQuery.addReceiver(PipelineAssemblerImpl.RECURSIVE_CMIS, cmisRecursive);
        sourceAction.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, cmisQuery);

        cmisRecursive.addReceiver(PipelineAssemblerImpl.RECURSIVE_RECEIVER, cmisQuery);

        ConfiguredAction metadataAction = new ConfiguredAction();
        metadataAction.setActionId(PipelineAssemblerImpl.METADATA_ACTION_ID);
        metadataAction.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        cmisQuery.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, metadataAction);

        ConfiguredAction listAction = new ConfiguredAction();
        listAction.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        metadataAction.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, listAction);

        ConfiguredAction endAction = new ConfiguredAction();
        endAction.setActionId(PipelineAssemblerImpl.END_ACTION);
        listAction.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, endAction);

        return start;
    }

    void getActionConfigTest(){
        PipelineAssemblerImpl pipeline = new PipelineAssemblerImpl();

        ConfiguredAction start = mockConfiguredAction();

        pipeline.getActionConfig(start);
    }
}
