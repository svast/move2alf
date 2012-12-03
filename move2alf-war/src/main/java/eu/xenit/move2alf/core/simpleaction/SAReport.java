package eu.xenit.move2alf.core.simpleaction;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionSingleResult;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SAReport extends SimpleActionSingleResult {

	@Override
	public String getDescription() {
		return "Generating report";
	}

	@Override
	public FileInfo executeSingleResult(final FileInfo parameterMap, final ActionConfig config) {
		final String status = (String) parameterMap.get(Parameters.PARAM_STATUS);
		if (status != null && Parameters.VALUE_FAILED.equals(status)) {
			final String errorMessage = (String) parameterMap.get(Parameters.PARAM_ERROR_MESSAGE);
			throw new Move2AlfException((errorMessage != null) ? errorMessage : "");
		} else {
			final FileInfo output = new FileInfo();
			output.putAll(parameterMap);
			return output;
		}
	}
}
