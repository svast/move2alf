package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ACLTestAction extends Action {
	@Override
	protected void executeImpl(final ConfiguredAction configuredAction, final Map<String, Object> parameterMap) {
		// java bs
		Map<String, Map<String, String>> acls = new HashMap<String, Map<String,String>>();
		Map<String, String> acl = new HashMap<String, String>();
		acl.put("GROUP_EVERYONE", "Coordinator");

		File file = (File) parameterMap.get(Parameters.PARAM_FILE);
		String fileName = file.getName();

		acls.put(fileName, acl);
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
