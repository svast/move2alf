package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.pipeline.actions.context.SendingContext;

public interface ErrorHandler {

	public void handleError(String actionId, Object message,
			Exception e, SendingContext sendingContext);

    public void handleError(String actionId, Object message, String error, SendingContext sendingContext);
    public void handleInfo(String actionId, Object message, String info, SendingContext sendingContext);
    public void handleWarn(String actionId, Object message, String warning, SendingContext sendingContext);

}
