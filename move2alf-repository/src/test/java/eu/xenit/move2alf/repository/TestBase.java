package eu.xenit.move2alf.repository;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.parser.SourceParser;
import eu.xenit.move2alf.repository.RepositoryAccess;
import eu.xenit.move2alf.repository.RepositoryAccessSession;
import eu.xenit.move2alf.repository.RepositoryException;

public class TestBase extends TestCase {
	private static Logger logger = LoggerFactory.getLogger(TestBase.class);

	protected RepositoryAccess ra;
	protected URL alfrescoUrl = null;
	protected String user = "admin";
	protected String password = "admin";
	protected String pathOffset = "src/eu/xenit/move2alf/repository/";

	private static final String modelNamespace = "{http://www.alfresco.org/model/content/1.0}";
	private static final String modelType = "content";
	
	public void testStoreDoc() {
		try {
			RepositoryAccessSession ras = ra.createSessionAndRetry();
			Map<String, String> meta = new HashMap<String, String>();
			meta.put("author", "junit");
			Map<String, String> multiValueMeta = new HashMap<String, String>();
			String spacePath = "";
			String description = "description test1";

			assertFalse(ras.doesDocExist("test1.txt", spacePath, true));
			File document = new File(pathOffset + "test1.txt");
			ras.storeDocAndCreateParentSpaces(document, SourceParser
					.determineMimeType(document.getName()), spacePath,
					description, modelNamespace, modelType, meta,
					multiValueMeta);
			assertTrue(ras.doesDocExist("test1.txt", spacePath, true));

			assertFalse(ras.doesDocExist("test1.pdf", spacePath, true));
			document = new File(pathOffset + "test1.pdf");
			ras.storeDocAndCreateParentSpaces(document, SourceParser
					.determineMimeType(document.getName()), spacePath,
					description, modelNamespace, modelType, meta,
					multiValueMeta);
			assertTrue(ras.doesDocExist("test1.pdf", spacePath, true));

			assertFalse(ras.doesDocExist("test1.doc", spacePath, true));
			document = new File(pathOffset + "test1.doc");
			ras.storeDocAndCreateParentSpaces(document, SourceParser
					.determineMimeType(document.getName()), spacePath,
					description, modelNamespace, modelType, meta,
					multiValueMeta);
			assertTrue(ras.doesDocExist("test1.doc", spacePath, true));

			ras.closeSession();
		} catch (Exception e) {
			logger.error("", e);
			fail();
		}
	}

	public void testDeleteDoc() {
		try {
			RepositoryAccessSession ras = ra.createSessionAndRetry();
			String spacePath = "";

			assertTrue(ras.doesDocExist("test1.txt", spacePath, true));
			assertTrue(ras.doesDocExist("test1.pdf", spacePath, true));
			assertTrue(ras.doesDocExist("test1.doc", spacePath, true));

			ras.deleteByDocNameAndSpace(spacePath, "test1.txt");
			ras.deleteByDocNameAndSpace(spacePath, "test1.doc");
			ras.deleteByDocNameAndSpace(spacePath, "test1.pdf");

			assertFalse(ras.doesDocExist("test1.txt", spacePath, true));
			assertFalse(ras.doesDocExist("test1.pdf", spacePath, true));
			assertFalse(ras.doesDocExist("test1.doc", spacePath, true));

			ras.closeSession();
		} catch (Exception e) {
			logger.error("", e);
			fail();
		}
	}

	public void testStoreExistingDoc() {
		try {
			RepositoryAccessSession ras = ra.createSessionAndRetry();
			Map<String, String> meta = new HashMap<String, String>();
			meta.put("author", "junit");
			Map<String, String> multiValueMeta = new HashMap<String, String>();
			String spacePath = "";
			String description = "description test1";

			File document = new File(pathOffset + "test1.txt");
			ras.storeDocAndCreateParentSpaces(document, SourceParser
					.determineMimeType(document.getName()), spacePath,
					description, modelNamespace, modelType, meta,
					multiValueMeta);

			try {
				document = new File(pathOffset + "test1.txt");
				ras.storeDocAndCreateParentSpaces(document, SourceParser
						.determineMimeType(document.getName()), spacePath,
						description, modelNamespace, modelType, meta,
						multiValueMeta);

				fail("RespositoryException should have been thrown");

			} catch (RepositoryException e) {
				// ok, RepositoryException is expected, pass test
			} finally {
				ras.deleteByDocNameAndSpace(spacePath, "test1.txt");
				ras.closeSession();
			}
		} catch (Exception e) {
			logger.error("", e);
			fail();
		}
	}

	public void testDeleteNonExistingDoc() {
		try {
			RepositoryAccessSession ras = ra.createSessionAndRetry();
			String spacePath = "";
			try {
				ras.deleteByDocNameAndSpace(spacePath, "test1.txt");

				fail("RespositoryException should have been thrown");
			} catch (RepositoryException e) {
				logger.info("",e);
				// ok, RepositoryException is expected, pass test
			} finally {
				ras.closeSession();
			}
		} catch (Exception e) {
			logger.error("", e);
			fail();
		}
	}

	public void testStoreDocAndCreateSpace() {
		try {
			RepositoryAccessSession ras = ra.createSessionAndRetry();
			Map<String, String> meta = new HashMap<String, String>();
			meta.put("author", "junit");
			Map<String, String> multiValueMeta = new HashMap<String, String>();
			String spacePath = "/cm:test";
			String description = "description test1";

			assertFalse(ras.doesDocExist("test1.txt", spacePath, true));

			File document = new File(pathOffset + "test1.txt");
			ras.storeDocAndCreateParentSpaces(document, SourceParser
					.determineMimeType(document.getName()), spacePath,
					description, modelNamespace, modelType, meta,
					multiValueMeta);

			assertTrue(ras.doesDocExist("test1.txt", spacePath, true));

			ras.closeSession();
		} catch (Exception e) {
			logger.error("", e);
			fail();
		}
	}

	public void testDeleteDocInSpace() {
		try {
			RepositoryAccessSession ras = ra.createSessionAndRetry();
			String spacePath = "/cm:test";

			assertTrue(ras.doesDocExist("test1.txt", spacePath, true));

			ras.deleteByDocNameAndSpace(spacePath, "test1.txt");
			ras.deleteSpace(spacePath, true);

			assertFalse(ras.doesDocExist("test1.txt", spacePath, true));

			ras.closeSession();
		} catch (Exception e) {
			logger.error("", e);
			fail();
		}
	}

	public void testDeleteNonEmptySpace() {
		try {
			RepositoryAccessSession ras = ra.createSessionAndRetry();
			Map<String, String> meta = new HashMap<String, String>();
			meta.put("author", "junit");
			Map<String, String> multiValueMeta = new HashMap<String, String>();
			String spacePath = "/cm:test";
			String description = "description test1";

			File document = new File(pathOffset + "test1.txt");
			ras.storeDocAndCreateParentSpaces(document, SourceParser
					.determineMimeType(document.getName()), spacePath,
					description, modelNamespace, modelType, meta,
					multiValueMeta);

			try {
				ras.deleteSpace(spacePath, true);
				fail("RespositoryException should have been thrown");
			} catch (RepositoryException e) {
				// ok, RepositoryException is expected, pass test
			} finally {
				ras.deleteSpace(spacePath, false);
				ras.closeSession();
			}
		} catch (Exception e) {
			logger.error("", e);
			fail();
		}
	}

	public void testDoesDocExist() {
		try {
			RepositoryAccessSession ras = ra.createSessionAndRetry();
			Map<String, String> meta = new HashMap<String, String>();
			meta.put("author", "junit");
			Map<String, String> multiValueMeta = new HashMap<String, String>();
			String spacePath = "";
			String description = "description test1";

			File document = new File(pathOffset + "test1.txt");
			ras.storeDocAndCreateParentSpaces(document, SourceParser
					.determineMimeType(document.getName()), spacePath,
					description, modelNamespace, modelType, meta,
					multiValueMeta);

			assertTrue(ras.doesDocExist(document.getName(), spacePath, true));

			ras.deleteByDocNameAndSpace(spacePath, document.getName());

			assertFalse(ras.doesDocExist(document.getName(), spacePath, true));

			ras.closeSession();
		} catch (Exception e) {
			logger.error("", e);
			fail();
		}
	}

	public void testUpdateContentByDocNameAndPath() {
		try {
			RepositoryAccessSession ras = ra.createSessionAndRetry();
			Map<String, String> meta = new HashMap<String, String>();
			meta.put("author", "junit");
			Map<String, String> multiValueMeta = new HashMap<String, String>();
			String spacePath = "";
			String description = "description test1";

			File document = new File(pathOffset + "test1.txt");
			ras.storeDocAndCreateParentSpaces(document, SourceParser
					.determineMimeType(document.getName()), spacePath,
					description, modelNamespace, modelType, meta,
					multiValueMeta);

			File documentNewContent = new File(pathOffset + "test2.txt");
			ras.updateContentByDocNameAndPath(spacePath, document.getName(),
					documentNewContent, SourceParser
							.determineMimeType(documentNewContent.getName()),
					false);
			// test1.txt in alfresco should now contain the content of test2.txt
			// "content of test2.txt"
			// TODO check content change automatically

			ras.deleteByDocNameAndSpace(spacePath, document.getName());

			ras.closeSession();
		} catch (Exception e) {
			logger.error("", e);
			fail();
		}
	}
	
	public void testMultiThreaded() {
		for (int i=0;i<100;i++){
			MySession mySession = new MySession(i);
			mySession.start();
		}
	}

	public void testStore1000Docs() {
		try {
			Date startDate = new Date();
			RepositoryAccessSession ras = ra.createSessionAndRetry();
			Map<String, String> meta = new HashMap<String, String>();
			meta.put("author", "loadTest");
			Map<String, String> multiValueMeta = new HashMap<String, String>();
			
			// inserting docs
			for(int i=0;i<1000;i++){
				// create space/doc
				String spacePath = "/cm:f"+i;
			String description = "description test"+i;

			File document = new File(pathOffset + "test1.txt");
			ras.storeDocAndCreateParentSpaces(document, SourceParser
					.determineMimeType(document.getName()), spacePath,
					description, modelNamespace, modelType, meta,
					multiValueMeta);
			}
			ras.closeSession();
			Date endDate = new Date();
			logger.info("Storing 1000 docs with space in {} milliseconds", endDate.getTime()-startDate.getTime());

		} catch (Exception e) {
			logger.error("", e);
			fail();
		}
	}

	public void testDelete1000Docs() {
		try {
			Date startDate = new Date();
			RepositoryAccessSession ras = ra.createSessionAndRetry();
			// removing docs and spaces
			for(int i=0;i<1000;i++){
				String spacePath = "/cm:f"+i;
				ras.deleteByDocNameAndSpace(spacePath, "test1.txt");
				ras.deleteSpace(spacePath, true);
			}			
			ras.closeSession();
			Date endDate = new Date();
			logger.info("Deleting 1000 docs with space in {} milliseconds", endDate.getTime()-startDate.getTime());
		} catch (Exception e) {
			logger.error("", e);
			fail();
		}
	}
	
	
	class MySession extends Thread {

		int t;
		public MySession(int t) {
			this.t=t;
		}

		public void run() {
			RepositoryAccessSession ras = ra.createSessionAndRetry();
			Map<String, String> meta = new HashMap<String, String>();
			meta.put("author", "loadTest");
			Map<String, String> multiValueMeta = new HashMap<String, String>();
			File document = new File(pathOffset + "test1.txt");

			try{
				String spacePath= "/cm:m1/cm:m2/cm:m3/cm:m4/cm:f"+t;
			ras.storeDocAndCreateParentSpaces(document, SourceParser
					.determineMimeType(document.getName()), spacePath, "", TestBase.modelNamespace, TestBase.modelType, meta, multiValueMeta);
			ras.deleteByDocNameAndSpace(spacePath, "test1.txt");
			ras.deleteSpace(spacePath, true);
			ras.closeSession();
			} catch (Exception e) {
				logger.error("", e);
				fail();
			}

		}
	}
}
