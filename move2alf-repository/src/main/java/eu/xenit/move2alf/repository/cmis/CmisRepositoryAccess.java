package eu.xenit.move2alf.repository.cmis;

import java.net.URL;

import eu.xenit.move2alf.repository.RepositoryAccess;
import eu.xenit.move2alf.repository.RepositoryAccessException;
import eu.xenit.move2alf.repository.RepositoryAccessSession;

public class CmisRepositoryAccess extends RepositoryAccess {

	private URL url;
	private String user;
	private String password;

	public CmisRepositoryAccess(URL url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	@Override
	public RepositoryAccessSession createSession()
			throws RepositoryAccessException {
		return new CmisRepositoryAccessSession(url, user, password);
	}

}
