package eu.xenit.move2alf.repository;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.repository.cmis.CmisRepositoryAccess;

public class TestCmis extends TestBase {
	private static Logger logger = LoggerFactory.getLogger(TestCmis.class);

	protected void setUp() {
		try {
//			alfrescoUrl = new URL("http://localhost:8080/alfresco/api");
//			ra = new WebServiceRepositoryAccess(alfrescoUrl, user, password);
			alfrescoUrl = new URL("http://localhost:8080/alfresco/service/cmis");
			ra = new CmisRepositoryAccess(alfrescoUrl, user, password);

		} catch (MalformedURLException e) {
			logger.error("", e);
			fail();
		}
	}
}
