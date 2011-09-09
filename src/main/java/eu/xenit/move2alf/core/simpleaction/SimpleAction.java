package eu.xenit.move2alf.core.simpleaction;

import java.util.List;
import java.util.Map;

public abstract class SimpleAction {
	public abstract List<Map<String, Object>> execute(
			final Map<String, Object> parameterMap,
			final Map<String, String> config);
}
