package eu.xenit.move2alf.core.simpleaction.execution;

import java.util.List;
import java.util.Map;

public class SingleThreadExecutor implements ActionExecutor {

	@Override
	public List<Map<String, Object>> execute(List<Map<String, Object>> input) {
//		List<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
//		for (Map<String, Object> parameterMap : input) {
//			File file = (File) parameterMap.get(Parameters.PARAM_FILE);
//			try {
//				List<Map<String, Object>> result = action.execute(parameterMap,
//						config);
//				if (result != null) {
//					output.addAll(result);
//				}
//			} catch (Exception e) {
//				handleError(parameterMap, jobConfig, cycle, e);
//			}
//		}
//		return null;
		// TODO: move execution from jobexecutionservice to here
		return null;
	}

}
