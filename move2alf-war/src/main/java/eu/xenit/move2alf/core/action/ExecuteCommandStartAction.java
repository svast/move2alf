package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.logic.PipelineAssemblerImpl;
import eu.xenit.move2alf.pipeline.actions.AbstractSendingAction;
import eu.xenit.move2alf.pipeline.actions.EOCAware;
import eu.xenit.move2alf.pipeline.actions.StartAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ExecuteCommandStartAction extends Move2AlfReceivingAction<Object> {

    private static final Logger logger = LoggerFactory
            .getLogger(ExecuteCommandStartAction.class);


    public static final String PARAM_COMMAND = "command";
    private String command;

    public void setCommand(String command){
        this.command = command;
    }

    @Override
    public void executeImpl(Object message) {
        Util.executeCommand(command);
        if (sendingContext.hasReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER)) {
            sendMessage(PipelineAssemblerImpl.DEFAULT_RECEIVER, "Command executed");
        }
    }
}
