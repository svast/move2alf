package eu.xenit.move2alf.core.sourcesink;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.repository.RepositoryAccessException;
import eu.xenit.move2alf.repository.RepositoryAccessSession;
import eu.xenit.move2alf.repository.RepositoryException;
import eu.xenit.move2alf.repository.RepositoryFatalException;
import eu.xenit.move2alf.repository.alfresco.ws.WebServiceRepositoryAccess;

public class AlfrescoSourceSink extends SourceSink {

	private static final Logger logger = LoggerFactory
			.getLogger(AlfrescoSourceSink.class);

	@Override
	public List<File> list(ConfiguredSourceSink sourceConfig, String path,
			boolean recursive) {
		return null;
	}

	@Override
	public void send(ConfiguredSourceSink configuredSourceSink,
			Map<String, Object> parameterMap) {
		String user = configuredSourceSink.getParameter("user");
		String password = configuredSourceSink.getParameter("password");
		String url = configuredSourceSink.getParameter("url");
		if (url.endsWith("/")) {
			url = url + "api/";
		} else {
			url = url + "/api/";
		}

		// if (AuthenticationUtils.getAuthenticationDetails() == null) {
		// try {
		// logger.info("Starting Alfresco session for user " + user);
		// AuthenticationUtils.startSession(user, password, url);
		// } catch (AuthenticationFault e) {
		// logger.error("Authentication failed");
		// e.printStackTrace();
		// }
		// } else {
		// logger.debug("Already authenticated");
		// }

		WebServiceRepositoryAccess ra = null;
		try {
			ra = new WebServiceRepositoryAccess(new URL(url), user, password);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		RepositoryAccessSession ras = ra.createSessionAndRetry();
		try {
			// run(ras);
			File document = (File) parameterMap.get("file");
			ras.storeDocAndCreateParentSpaces(document, "text/plain",
					"/cm:company_home", user,
					"{http://www.alfresco.org/model/content/1.0}", "content",
					null, null);
		} catch (RepositoryAccessException e) {
			// we end up here if there is a communication error during a session
			logger.error(e.getMessage(), e);
		} catch (RepositoryException e) {
			// we end up here if the request could not be handled by the
			// repository
			logger.error(e.getMessage(), e);
		} catch (RepositoryFatalException e) {
			logger.error("Fatal Exception", e);
			System.exit(1);
		}
	}
}