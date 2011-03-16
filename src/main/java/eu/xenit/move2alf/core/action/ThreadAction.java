package eu.xenit.move2alf.core.action;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class ThreadAction extends Action {
	
	@Override
	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		ExecutorService threadPool = (ExecutorService) parameterMap.get("threadpool");
		ConfiguredAction nextAction	= configuredAction.getAppliedConfiguredActionOnSuccess();
		threadPool.execute(new ActionRunner(nextAction, parameterMap));
	}

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// empty
	}
	
	public class ActionRunner extends Thread {
		private ConfiguredAction configuredAction;
		private Map<String, Object> parameterMap;
		
		public ActionRunner(ConfiguredAction configuredAction, Map<String, Object> parameterMap) {
			this.configuredAction = configuredAction;
			this.parameterMap = parameterMap;
		}
		
		public void run() {
			parameterMap.put("thread", Thread.currentThread().toString());
			getActionFactory().execute(configuredAction, parameterMap);
		}
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
		return "Thread";
	}

}
