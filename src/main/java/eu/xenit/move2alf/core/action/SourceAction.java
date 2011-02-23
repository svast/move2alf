package eu.xenit.move2alf.core.action;

import java.util.Map;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfiguredObject;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredObjectParameter;

public class SourceAction extends Action {
	
	public static final String PARAM_PATH = "path";
	public static final String PARAM_RECURSIVE = "recursive";

	@Override
	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// TODO: filters; in separate action?
		String path = null;
		boolean recursive = false;
		for (ConfiguredObjectParameter param : configuredAction.getConfiguredObjectParameterSet()) {
			if (PARAM_PATH.equals(param.getName())) {
				path = param.getValue();
			}
			if (PARAM_RECURSIVE.equals(param.getName())) {
				recursive = "true".equals(param.getValue());
			}
		}
		ConfiguredObject sourceConfig = (ConfiguredObject) configuredAction.getConfiguredSourceSinkSet().toArray()[0];
		SourceSink source = getSourceSinkFactory().getObject(sourceConfig.getClassName());
//		source.list(sourceConfig, path, recursive);
	}

	@Override
	protected void executeImpl(ConfiguredObject configuredAction,
			Map<String, Object> parameterMap) {
		// empty; SourceAction overrides the execute method to execute the next
		// action for each file separately
	}

}
