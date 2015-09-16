/**
 * 
 */
package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.pipeline.actions.context.SendingContext;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class DefaultErrorHandler implements ErrorHandler {

    private final boolean moveNotLoad;

    public DefaultErrorHandler(boolean moveNotLoad){
        this.moveNotLoad = moveNotLoad;
    }

	/* (non-Javadoc)
	 * @see eu.xenit.move2alf.logic.ErrorHandlerInterface#handleError(eu.xenit.move2alf.core.simpleaction.data.FileInfo, eu.xenit.move2alf.web.dto.JobModel, eu.xenit.move2alf.core.dto.Cycle, java.lang.Exception)
	 */
	@Override
	public void handleError(String actionId, Object message, Exception e, SendingContext sendingContext) {
        final String errorMessage = Util.getFullErrorMessage(e);
        handleError(actionId, message, errorMessage, sendingContext);
    }

    @Override
    public void handleError(String actionId, Object message, String error, SendingContext sendingContext) {
       handle(actionId, message, error, sendingContext, Parameters.PARAM_ERROR_MESSAGE, Parameters.VALUE_FAILED);
    }

    // lolo

    @Override
    public void handleInfo(String actionId, Object message, String info, SendingContext sendingContext) {
       handle(actionId, message, info, sendingContext, Parameters.PARAM_INFO_MESSAGE, Parameters.VALUE_INFO);
    }


    @Override
    public void handleWarn(String actionId, Object message, String warning, SendingContext sendingContext) {
       handle(actionId, message, warning, sendingContext, Parameters.PARAM_WARN_MESSAGE, Parameters.VALUE_WARN);
    }


    private void handle(String actionId, Object message, String error, SendingContext sendingContext, String messageName, String reportValue){
        // reporting
        Set<ProcessedDocumentParameter> params = new HashSet<ProcessedDocumentParameter>();
        ProcessedDocumentParameter msg = new ProcessedDocumentParameter();
        msg.setName(messageName);
        msg.setValue(error);
        params.add(msg);
        if(message instanceof FileInfo){
            FileInfo fileInfo = ((FileInfo)message);

            // TODO: handle cleaner?
            File file = (File) fileInfo.get(Parameters.PARAM_FILE);
            String name = file.getAbsolutePath();
            //String name = (String) fileInfo.get(Parameters.PARAM_NAME);

            if(sendingContext.hasReceiver(PipelineAssemblerImpl.REPORTER)) {
                sendingContext.sendMessage(
                        new ReportMessage(name,
                                new Date(), reportValue, params, (String) fileInfo.get(Parameters.PARAM_REFERENCE)), PipelineAssemblerImpl.REPORTER);
            }

            if(moveNotLoad){
                sendingContext.sendMessage(message, "MoveNotLoad");
            }
        } else {
            if(sendingContext.hasReceiver(PipelineAssemblerImpl.REPORTER)) {
                sendingContext.sendMessage(
                        new ReportMessage("Not a file", new Date(), reportValue, params, null),
                        PipelineAssemblerImpl.REPORTER
                );
            }
        }
    }




}
