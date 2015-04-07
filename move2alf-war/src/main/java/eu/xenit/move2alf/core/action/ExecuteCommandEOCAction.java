package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.logic.PipelineAssemblerImpl;
import eu.xenit.move2alf.pipeline.actions.EOCAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ExecuteCommandEOCAction extends Move2AlfReceivingAction<Object> implements EOCAware {

    private static final Logger logger = LoggerFactory
            .getLogger(ExecuteCommandEOCAction.class);


    public static final String PARAM_COMMAND = "command";
    private String command;

    public void setCommand(String command){
        this.command = command;
    }

    @Override
    public void executeImpl(Object message) {

    }

    @Override
    public void beforeSendEOC() {
        Util.executeCommand(command);
        if (sendingContext.hasReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER)) {
            sendMessage(PipelineAssemblerImpl.DEFAULT_RECEIVER, "Command executed");
        }
    }
}
