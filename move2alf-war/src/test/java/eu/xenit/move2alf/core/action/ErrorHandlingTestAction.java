package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.action.messages.FileInfoMessage;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.pipeline.AbstractMessage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ErrorHandlingTestAction extends Move2AlfAction<FileInfoMessage> {
	@Override
	protected void executeImpl(FileInfoMessage message) {
        for (int i = 0; i<3; i++) {
            Map<String, Object> newParameterMap = new HashMap<String, Object>(message.fileInfo);
            newParameterMap.put(Parameters.PARAM_FILE, new File("foo" + i + ".txt"));
            newParameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_FAILED);
            newParameterMap.put(Parameters.PARAM_ERROR_MESSAGE, "testing error handling");
        }
	}

	public String getName() {
		return "Error handling test action";
	}

	@Override
	public String getDescription() {
		return "Test error handling";
	}

	public String getCategory() {
		return "bla";
	}
}
