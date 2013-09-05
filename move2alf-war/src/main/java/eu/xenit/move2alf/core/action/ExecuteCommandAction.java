package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.logic.PipelineAssemblerImpl;
import eu.xenit.move2alf.pipeline.actions.AbstractSendingAction;
import eu.xenit.move2alf.pipeline.actions.StartAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ExecuteCommandAction extends Move2AlfReceivingAction<Object> {

    private static final Logger logger = LoggerFactory
            .getLogger(ExecuteCommandAction.class);


    public static final String PARAM_COMMAND = "command";
    private String command;
    public void setCommand(String command){
        this.command = command;
    }

    @Override
    public void executeImpl(Object message) {
        logger.debug("Command: " + command);
        if (command != null && !"".equals(command)) {
            logger.debug("Executing command " + command);

            final ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);

            Process process;
            try {
                process = pb.start();
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }

            try {
                final InputStream is = process.getInputStream();
                final InputStreamReader isr = new InputStreamReader(is);
                final BufferedReader br = new BufferedReader(isr);
                String line;

                while ((line = br.readLine()) != null) {
                    logger.debug(line);

                }
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                process.waitFor();
            } catch (final InterruptedException ie) {
                logger.error("Problem running command");
            }

            logger.info("Command finished");
            if (sendingContext.hasReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER)) {
                sendMessage(PipelineAssemblerImpl.DEFAULT_RECEIVER, "Command executed");
            }
        }
    }
}
