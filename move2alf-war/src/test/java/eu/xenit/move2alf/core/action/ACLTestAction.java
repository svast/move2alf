package eu.xenit.move2alf.core.action;

import com.google.common.collect.ImmutableMap;
import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ACLTestAction extends Action {
	@Override
	protected void executeImpl(final ConfiguredAction configuredAction, final Map<String, Object> parameterMap) {
		File file = (File) parameterMap.get(Parameters.PARAM_FILE);
		String fileName = file.getName();
		Map acls = ImmutableMap.of(fileName, ImmutableMap.of("GROUP_EVERYONE", "Coordinator"));

		parameterMap.put(Parameters.PARAM_ACL, acls);
		parameterMap.put(Parameters.PARAM_INHERIT_PERMISSIONS, false);
	}

	@Override
	public String getName() {
		return "ACLTest";
	}

	@Override
	public String getDescription() {
		return "Set ACLs";
	}

	@Override
	public String getCategory() {
		return CAT_METADATA;
	}
}
