package eu.xenit.move2alf.core.simpleaction.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.simpleaction.SimpleAction;
import eu.xenit.move2alf.logic.JobExecutionServiceImpl.ErrorHandler;
import eu.xenit.move2alf.web.dto.JobConfig;

public class ThreadedExecutor implements ActionExecutor {

	@Override
	public List<Map<String, Object>> execute(List<Map<String, Object>> input,
			JobConfig jobConfig, Cycle cycle, SimpleAction action,
			Map<String, String> config, ErrorHandler errorHandler) {
		List<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> parameterMap : input) {
			try {
				List<Map<String, Object>> result = action.execute(parameterMap,
						config);
				if (result != null) {
					output.addAll(result);
				}
			} catch (Exception e) {
				errorHandler.handleError(parameterMap, jobConfig, cycle, e);
			}
		}
		return output;
	}
}
