package eu.xenit.move2alf.core.simpleaction;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionSingleResult;

public class SAConvertFailsToException extends SimpleActionSingleResult {

	@Override
	public FileInfo executeSingleResult(
			FileInfo parameterMap, ActionConfig config) {
		String status = "";
		String message = "";
		try {
			status = (String) parameterMap.get(Parameters.PARAM_STATUS);
			message = (String) parameterMap
					.get(Parameters.PARAM_ERROR_MESSAGE);
		} catch (NullPointerException npe) {
			// no status or message set, assume success
		}
		if (Parameters.VALUE_FAILED.equals(status)) {
			throw new Move2AlfException(message);
		}
		return parameterMap;
	}

}
