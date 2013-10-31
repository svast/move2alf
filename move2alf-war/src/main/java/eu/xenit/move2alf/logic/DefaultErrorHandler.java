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
        // reporting
        Set<ProcessedDocumentParameter> params = new HashSet<ProcessedDocumentParameter>();
        ProcessedDocumentParameter msg = new ProcessedDocumentParameter();
        msg.setName(Parameters.PARAM_ERROR_MESSAGE);
        msg.setValue(error);
        params.add(msg);
        if(message instanceof FileInfo){
            FileInfo fileInfo = ((FileInfo)message);

            // TODO: handle cleaner?
            File file = (File) fileInfo.get(Parameters.PARAM_FILE);
            String name = (String) fileInfo.get(Parameters.PARAM_NAME);

            if(sendingContext.hasReceiver(PipelineAssemblerImpl.REPORTER)) {
                sendingContext.sendMessage(
                        new ReportMessage(name,
                                new Date(), Parameters.VALUE_FAILED, params, (String) fileInfo.get(Parameters.PARAM_REFERENCE)), PipelineAssemblerImpl.REPORTER);
            }

            if(moveNotLoad){
                sendingContext.sendMessage(message, "MoveNotLoad");
            }
        } else {
            if(sendingContext.hasReceiver(PipelineAssemblerImpl.REPORTER)) {
                sendingContext.sendMessage(
                        new ReportMessage("Not a file", new Date(), Parameters.VALUE_FAILED, params, null),
                        PipelineAssemblerImpl.REPORTER
                );
            }
        }
    }

}