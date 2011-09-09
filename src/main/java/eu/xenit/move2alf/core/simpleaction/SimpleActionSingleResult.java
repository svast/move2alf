package eu.xenit.move2alf.core.simpleaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SimpleActionSingleResult extends SimpleAction {

	@Override
	public final List<Map<String, Object>> execute(
			final Map<String, Object> parameterMap,
			final Map<String, String> config) {
		List<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
		output.add(executeSingleResult(parameterMap, config));
		return output;
	}

	public abstract Map<String, Object> executeSingleResult(
			final Map<String, Object> parameterMap,
			final Map<String, String> config);
}
