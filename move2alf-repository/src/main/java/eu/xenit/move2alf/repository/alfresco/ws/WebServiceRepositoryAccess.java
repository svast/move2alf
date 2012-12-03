package eu.xenit.move2alf.repository.alfresco.ws;

import java.net.URL;

import org.alfresco.webservice.authentication.AuthenticationFault;
import org.alfresco.webservice.util.AuthenticationDetails;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.WebServiceException;

import eu.xenit.move2alf.repository.RepositoryAccess;
import eu.xenit.move2alf.repository.RepositoryAccessException;

public class WebServiceRepositoryAccess extends RepositoryAccess {

	protected URL alfrescoUrl;

	protected String username;

	protected String password;

	protected String ticket=null;

	public WebServiceRepositoryAccess(URL alfrescoUrl, String username,
			String password) {
		this.alfrescoUrl = alfrescoUrl;
		this.username = username;
		this.password = password;
	}

//	public WebServiceRepositoryAccess(URL alfrescoUrl, String ticket)
//			throws MalformedURLException {
//		this.alfrescoUrl = alfrescoUrl;
//		this.ticket = ticket;
//	}

	public WebServiceRepositoryAccessSession createSession()
			throws RepositoryAccessException {
		if (ticket != null) {
			logger.info("Opening rra session with ticket {}", ticket);
			AuthenticationUtils
					.setAuthenticationDetails(new AuthenticationDetails(
							"username", ticket, "sessionid", alfrescoUrl
									.toString()));

		} else {
			logger
					.info("Opening rra session with username/password {}",
							username);
			try {
				AuthenticationUtils.startSession(username, password,
						alfrescoUrl.toString());
			} catch (AuthenticationFault e) {
				throw new RepositoryAccessException("Could not authenticate");
			} catch (WebServiceException e){
				throw new RepositoryAccessException("Could not connect");
			}
		}
		return new WebServiceRepositoryAccessSession(alfrescoUrl);
	}

}
