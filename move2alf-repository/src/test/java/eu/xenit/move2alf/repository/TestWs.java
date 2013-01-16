package eu.xenit.move2alf.repository;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.service.namespace.NamespaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.repository.alfresco.ws.Document;
import eu.xenit.move2alf.repository.alfresco.ws.WebServiceRepositoryAccess;

public class TestWs extends TestBase {
	private static Logger logger = LoggerFactory.getLogger(TestWs.class);

	protected void setUp() {
		try {
			alfrescoUrl = new URL("http://192.168.123.2:8080/alfresco/api/");
			ra = new WebServiceRepositoryAccess(alfrescoUrl, user, password);
		} catch (MalformedURLException e) {
			logger.error("", e);
			fail();
		}
	}

	private static Document doc(final String path, final String folderPath) {
		return new Document(new File(path), "text/plain", folderPath, path, "{"
				+ NamespaceService.CONTENT_MODEL_1_0_URI + "}",
				ContentModel.TYPE_CONTENT.getLocalName(),
				new HashMap<String, String>(), new HashMap<String, String>());
	}

	public void testBatch() throws RepositoryAccessException,
			RepositoryException, PartialUploadFailureException {
		RepositoryAccessSession ras = ra.createSession();
		List<Document> docs = new ArrayList<Document>();
		docs.add(doc("/tmp/m2a/01.txt", "/cm:Foo"));
		docs.add(doc("/tmp/m2a/02.txt", "/cm:Foo"));
		docs.add(doc("/tmp/m2a/03.txt", "/cm:Foo"));
		docs.add(doc("/tmp/m2a/04.txt", "/cm:Foo"));
		docs.add(doc("/tmp/m2a/05.txt", "/cm:Foo"));
		ras.storeDocsAndCreateParentSpaces(docs, false);
	}

	public void testBatchWithAuditableProperties()
			throws RepositoryAccessException, RepositoryException, PartialUploadFailureException {
		RepositoryAccessSession ras = ra.createSession();
		List<Document> docs = new ArrayList<Document>();
		Document doc1 = doc("/tmp/m2a/01.txt", "/cm:Foo");
		doc1.meta.put("modifier", "foo");
		Document doc2 = doc("/tmp/m2a/02.txt", "/cm:Foo");
		doc2.meta.put("modifier", "bar");
		docs.add(doc1);
		docs.add(doc2);
		ras.storeDocsAndCreateParentSpaces(docs, false);
	}

	public void testPerformance() throws RepositoryAccessException,
			RepositoryException, PartialUploadFailureException {
		RepositoryAccessSession ras = ra.createSession();
		Date start1 = new Date();
		List<Document> docs = new ArrayList<Document>();
		for (int i = 0; i < 100; i++) {
			docs.add(doc("/tmp/m2a/" + i + ".txt", "/cm:Foo1"));
		}
		ras.storeDocsAndCreateParentSpaces(docs, false);
		Date stop1 = new Date();

		System.out.println("Timing 1 x 100 docs / txn: "
				+ (stop1.getTime() - start1.getTime()) + "ms");
		
		Date start2 = new Date();
		for (int i = 0; i < 100; i++) {
			List<Document> docs2 = new ArrayList<Document>();
			docs2.add(doc("/tmp/m2a/" + i + ".txt", "/cm:Foo2"));
			ras.storeDocsAndCreateParentSpaces(docs2, false);
		}
		Date stop2 = new Date();

		System.out.println("Timing 100 x 1 doc / txn: "
				+ (stop2.getTime() - start2.getTime()) + "ms");
	}

}
