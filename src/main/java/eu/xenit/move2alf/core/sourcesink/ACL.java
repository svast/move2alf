package eu.xenit.move2alf.core.sourcesink;

import java.util.Map;

public class ACL {
	public final Map<String, Map<String, String>> acls;
	public final boolean inheritsPermissions;

	public ACL(final Map<String, Map<String, String>> acls,
			final boolean inheritsPermissions) {
		this.acls = acls;
		this.inheritsPermissions = inheritsPermissions;
	}
}
