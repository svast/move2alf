package eu.xenit.move2alf.core.simpleaction.execution;

import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.simpleaction.SimpleAction;
import eu.xenit.move2alf.logic.JobExecutionServiceImpl.ErrorHandler;
import eu.xenit.move2alf.web.dto.JobConfig;

public interface ActionExecutor {
	List<Map<String, Object>> execute(List<Map<String, Object>> input,
			JobConfig jobConfig, Cycle cycle, SimpleAction action,
			Map<String, String> config, ErrorHandler errorHandler);
}
