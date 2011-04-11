package eu.xenit.move2alf.core.action;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class TestAction extends Action {
	private static final Logger logger = LoggerFactory
			.getLogger(TestAction.class);

	@Override
	public void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		Map<String, String> reportFields = (Map<String, String>) parameterMap
				.get(Parameters.PARAM_REPORT_FIELDS);
		if (reportFields == null) {
			reportFields = new HashMap<String, String>();
			parameterMap.put(Parameters.PARAM_REPORT_FIELDS, reportFields);
		}
		reportFields.put("Test", "Testwaarde");
		for (String key : configuredAction.getParameters().keySet()) {
			reportFields.put(key, configuredAction.getParameter(key));
		}
		//throw new RuntimeException("test exception handling");
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_DEFAULT;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Test";
	}

}
