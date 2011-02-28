package eu.xenit.move2alf.core.sourcesink;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.alfresco.webservice.authentication.AuthenticationFault;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;

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
		if (AuthenticationUtils.getAuthenticationDetails() == null) {
			try {
				String user = configuredSourceSink.getParameter("user");
				String pass = configuredSourceSink.getParameter("password");
				String endpoint = configuredSourceSink.getParameter("url");
				logger.info("Starting Alfresco session for user " + user);
				AuthenticationUtils.startSession(user, pass, endpoint);
			} catch (AuthenticationFault e) {
				logger.error("Authentication failed");
				e.printStackTrace();
			}
		} else {
			logger.debug("Already authenticated");
		}
	}
}
