package eu.xenit.move2alf.core.simpleaction.execution;

import java.util.List;
import java.util.Map;

public interface ActionExecutor {
	List<Map<String, Object>> execute(List<Map<String, Object>> input);
}
