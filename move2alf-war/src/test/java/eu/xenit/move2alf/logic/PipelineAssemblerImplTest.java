package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.core.action.ActionClassInfoService;
import eu.xenit.move2alf.core.action.M2AlfEndAction;
import eu.xenit.move2alf.core.action.StartCycleAction;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.pipeline.actions.ActionConfig;
import org.hibernate.SessionFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: rox
 * Date: 2/24/14
 * Time: 11:32 AM
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( { "file:src/main/webapp/WEB-INF/applicationContext.xml",
        "file:src/main/webapp/WEB-INF/applicationContext-security.xml" })
public class PipelineAssemblerImplTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ActionClassInfoService actionClassService;

    ConfiguredAction mockConfiguredAction() {
        Map mockParameters = new HashMap();

        ConfiguredAction reportSaver = new ConfiguredAction(PipelineAssemblerImpl.REPORT_SAVER, "", 1, "SAReport", mockParameters);
        reportSaver.setReceivers(new HashMap());

        ConfiguredAction reporter = new ConfiguredAction(PipelineAssemblerImpl.REPORTER, "", 1, "eu.xenit.move2alf.core.action.BatchReportsAction", mockParameters);
        reporter.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, reportSaver);

        ConfiguredAction start = new ConfiguredAction(PipelineAssemblerImpl.START, "", 1, "StartCycleAction", mockParameters);

        ConfiguredAction validateAction = new ConfiguredAction(PipelineAssemblerImpl.VALIDATE_DESTINATION, "", 1, "eu.xenit.move2alf.core.action.ValidateAction", mockParameters);
        validateAction.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        start.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, validateAction);

        ConfiguredAction sourceAction = new ConfiguredAction(PipelineAssemblerImpl.SOURCE_CMIS_ID, "", 1, "eu.xenit.move2alf.core.simpleaction.SACMISInput", mockParameters);
        sourceAction.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        validateAction.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, sourceAction);

        // a loop from cmisRecursive to cmisQuery and back
        ConfiguredAction cmisRecursive = new ConfiguredAction(PipelineAssemblerImpl.RECURSIVE_CMIS, "", 1, "eu.xenit.move2alf.core.action.RecursiveCmis", mockParameters);
        cmisRecursive.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        sourceAction.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, cmisRecursive);

        ConfiguredAction cmisQuery = new ConfiguredAction(PipelineAssemblerImpl.CMIS_QUERY, "", 1, "eu.xenit.move2alf.core.action.CmisQuery", mockParameters);
        cmisQuery.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        cmisQuery.addReceiver(PipelineAssemblerImpl.RECURSIVE_CMIS, cmisRecursive);
        cmisRecursive.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, cmisQuery);

        ConfiguredAction metadataAction = new ConfiguredAction(PipelineAssemblerImpl.METADATA_ACTION_ID, "", 1, "CMISMetadataAction", mockParameters);
        metadataAction.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        cmisQuery.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, metadataAction);

        ConfiguredAction listAction = new ConfiguredAction(PipelineAssemblerImpl.LIST_ID, "", 1, "eu.xenit.move2alf.core.action.ListAction", mockParameters);
        listAction.addReceiver(PipelineAssemblerImpl.REPORTER, reporter);
        metadataAction.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, listAction);

        ConfiguredAction endAction = new ConfiguredAction(PipelineAssemblerImpl.END_ACTION, "", 1, "M2AlfEndAction", mockParameters);
        endAction.setReceivers(new HashMap());
        listAction.addReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER, endAction);

        return start;
    }

    @Test
    public void getActionConfigTest(){
        PipelineAssemblerImpl pipeline = new PipelineAssemblerImpl();
        pipeline.setApplicationContext(applicationContext);
        pipeline.setActionClassService(actionClassService);

        ConfiguredAction start = mockConfiguredAction();

        ActionConfig actionConfig = pipeline.getActionConfig(start);

        assertEquals(actionConfig.getReceivers().entrySet().size(), 1);

        ActionConfig validateAction = actionConfig.getReceivers().get(PipelineAssemblerImpl.DEFAULT_RECEIVER);
        assertNotNull(validateAction);
        assertEquals(PipelineAssemblerImpl.VALIDATE_DESTINATION,validateAction.getId());
        assertEquals(validateAction.getReceivers().entrySet().size(), 2);

        ActionConfig sourceAction = validateAction.getReceivers().get(PipelineAssemblerImpl.DEFAULT_RECEIVER);
        assertNotNull(sourceAction);
        assertEquals(PipelineAssemblerImpl.SOURCE_CMIS_ID,sourceAction.getId());
        assertEquals(sourceAction.getReceivers().entrySet().size(), 2);

        ActionConfig cmisRecursive = sourceAction.getReceivers().get(PipelineAssemblerImpl.DEFAULT_RECEIVER);
        assertNotNull(cmisRecursive);
        assertEquals(PipelineAssemblerImpl.RECURSIVE_CMIS,cmisRecursive.getId());
        assertEquals(cmisRecursive.getReceivers().entrySet().size(), 2);

        ActionConfig cmisQuery = cmisRecursive.getReceivers().get(PipelineAssemblerImpl.DEFAULT_RECEIVER);
        assertNotNull(cmisQuery);
        assertEquals(PipelineAssemblerImpl.CMIS_QUERY,cmisQuery.getId());
        assertEquals(cmisQuery.getReceivers().entrySet().size(), 3);
    }

}
