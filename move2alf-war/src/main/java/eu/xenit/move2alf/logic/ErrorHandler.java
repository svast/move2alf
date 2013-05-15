package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.pipeline.AbstractMessage;
import eu.xenit.move2alf.pipeline.actions.context.SendingContext;

public interface ErrorHandler {

	public void handleError(AbstractMessage message,
			Exception e, SendingContext sendingContext);

    public void handleError(AbstractMessage message, String error, SendingContext sendingContext);
}