package eu.xenit.move2alf.repository.alfresco.ws;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;

import javax.management.RuntimeErrorException;

import org.alfresco.repo.search.impl.lucene.LuceneQueryParser;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.alfresco.webservice.accesscontrol.ACE;
import org.alfresco.webservice.accesscontrol.AccessControlServiceSoapBindingStub;
import org.alfresco.webservice.accesscontrol.AccessStatus;
import org.alfresco.webservice.content.Content;
import org.alfresco.webservice.content.ContentFault;
import org.alfresco.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.webservice.dictionary.DictionaryServiceSoapBindingStub;
import org.alfresco.webservice.repository.QueryResult;
import org.alfresco.webservice.repository.RepositoryFault;
import org.alfresco.webservice.repository.RepositoryServiceLocator;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLDelete;
import org.alfresco.webservice.types.CMLUpdate;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Query;
import org.alfresco.webservice.types.QueryConfiguration;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.ResultSet;
import org.alfresco.webservice.types.ResultSetRow;
import org.alfresco.webservice.types.ResultSetRowNode;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.ActionUtils;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ContentUtils;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceException;
import org.alfresco.webservice.util.WebServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.repository.DocumentNotFoundException;
import eu.xenit.move2alf.repository.IllegalDocumentException;
import eu.xenit.move2alf.repository.IllegalDuplicateException;
import eu.xenit.move2alf.repository.RepositoryAccessException;
import eu.xenit.move2alf.repository.RepositoryAccessSession;
import eu.xenit.move2alf.repository.RepositoryException;
import eu.xenit.move2alf.repository.RepositoryFatalException;
import eu.xenit.move2alf.repository.UploadResult;

public class WebServiceRepositoryAccessSession implements RepositoryAccessSession {

	// FIELDS
	private static int MAX_LUCENE_RESULTS = 999;
	private static Logger logger = LoggerFactory
			.getLogger(WebServiceRepositoryAccessSession.class);

	// the only store that is used, so make it final static (dixit Jonas)
	// if this would ever change it would be better to make it a session
	// property than
	// to provide the store in every method call
	protected static final Store store = new Store(Constants.WORKSPACE_STORE,
			"SpacesStore");

	private static final int recoverySleepTime = 10000; // 10 seconds

	private ContentServiceSoapBindingStub contentService;

	private DictionaryServiceSoapBindingStub dictionaryService;

	private RepositoryServiceSoapBindingStub repositoryService;

	private AccessControlServiceSoapBindingStub accessControlService;

	private static final int wsStubTimeout = 600000; // 10 minutes

	public static final String companyHomePath = "/app:company_home";

	protected static final Set<String> auditablePropertyNameSet = new HashSet<String>(
			Arrays.asList(new String[] {"created", "creator", "modified",
					"modifier", "accessed"}));

	// to reduce the number of queries to alfresco (lucene queries cause the
	// threads in alfresco to block eachother)
	// paths are lower case
	private HashMap<String, Reference> referenceCache = new HashMap<String, Reference>();

	private static final int maxSizeReferenceCache = 1000;

	// to optimize the method removeZeroSizedFromTree
	private Set<String> processedDirSet = new HashSet<String>();

	private String host;
	private int port;
	private String webapp;
	private boolean enableLuceneFallback;

	public static final String protocol = "http://";
	public static final String pathDocumentDetails = "/n/showDocDetails/workspace/SpacesStore/";

	protected static Map<String, Semaphore> folderCreationLocks = new HashMap<String, Semaphore>();
	private static final boolean USE_FOLDER_CREATION_LOCKS = true;
	private static final String STATEMENT_UPDATE = "update";
	private static final String STATEMENT_CREATE = "create";

	// CONSTRUCTOR

	public WebServiceRepositoryAccessSession(URL alfrescoUrl, boolean enableLuceneFallback) {
		super();
		this.enableLuceneFallback = enableLuceneFallback;
		logger.debug("TICKET {}", AuthenticationUtils.getTicket());
		repositoryService = WebServiceFactory.getRepositoryService(alfrescoUrl
				.toString());
		contentService = WebServiceFactory.getContentService(alfrescoUrl
				.toString());
		dictionaryService = WebServiceFactory.getDictionaryService(alfrescoUrl
				.toString());
		accessControlService = WebServiceFactory
				.getAccessControlService(alfrescoUrl.toString());

		WebServiceFactory.setEndpointAddress(alfrescoUrl.toString());

		repositoryService.setTimeout(wsStubTimeout);// milliseconds
		contentService.setTimeout(wsStubTimeout);// milliseconds
		dictionaryService.setTimeout(wsStubTimeout);// milliseconds
		accessControlService.setTimeout(wsStubTimeout);// milliseconds

		host = alfrescoUrl.getHost();
		port = alfrescoUrl.getPort();
		webapp = alfrescoUrl.getPath().substring(1,
				alfrescoUrl.getPath().length() - 5);
		logger.debug("Host: " + host);
		logger.debug("Port: " + port);
		logger.debug("Path: " + webapp);
		if (port == -1) {
			port = 80;
		}
	}

	// PUBLIC METHODS

	public void closeSession() {
		logger.info("Closing rra session");
		try {
			AuthenticationUtils.endSession();
		} catch (WebServiceException e) {
			// sometimes we have an exception when closing a session (on test
			// machine). I have the
			// impression the session is properly closed, so let's log it, but
			// don't throw an exception.
			logger.warn("Problem closing session", e);
		}
	}

	/**
	 * @throws IllegalDocumentException
	 * @deprecated as of Move2Alf 1.2, replaced by {@see
	 *             void storeDocAndCreateParentSpaces(Document)}
	 */
	@Override
	public void storeDocAndCreateParentSpaces(File file, String mimeType,
											  String spacePath, String description, String contentModelNamespace,
											  String contentModelType, Map<String, String> meta,
											  Map<String, String> multiValueMeta)
			throws RepositoryAccessException, RepositoryException, IllegalDocumentException {
		Document document = new Document(file, mimeType, spacePath,
				description, contentModelNamespace, contentModelType, meta,
				multiValueMeta);
		storeDocAndCreateParentSpaces(document);
	}

	@Override
	public List<UploadResult> storeDocAndCreateParentSpaces(Document document)
			throws RepositoryAccessException, RepositoryException, IllegalDocumentException {
		List<Document> documents = new ArrayList<Document>();
		documents.add(document);
		return storeDocsAndCreateParentSpaces(documents, false);
	}

	@Override
	public List<UploadResult> storeDocsAndCreateParentSpaces(
			List<Document> documents, boolean allowOverwrite, boolean optimistic)
			throws RepositoryAccessException, RepositoryException {
		return storeDocsAndCreateParentSpaces(documents, allowOverwrite, optimistic, false);
	}


	@Override
	public List<UploadResult> storeDocsAndCreateParentSpaces(List<Document> documents, boolean allowOverwrite,
															 boolean optimistic, boolean acceptDuplicates)
			throws RepositoryAccessException, RepositoryException {

		List<CMLDocument> cmlDocs = new ArrayList<CMLDocument>();
		for (int i = 0; i < documents.size(); i++) {
			cmlDocs.add(new CMLDocument(this, documents.get(i), Integer.toString(i)));
		}

		RepositoryResult repositoryResult = updateRepositoryAndHandleErrors(allowOverwrite, cmlDocs, optimistic,
				acceptDuplicates);

		setAuditableProperties(documents, repositoryResult.getUpdateResults());

		return repositoryResult.getAllResults();
	}

	private String constructReferenceLink(String uuid) {
		return this.protocol + this.host + ":" + this.port + "/" + this.webapp + this.pathDocumentDetails + uuid;
	}

	class RepositoryResult {
		List<UploadResult> allResults;
		UpdateResult[] updateResults; // needed for setting auditable properties

		public UpdateResult[] getUpdateResults() {
			return updateResults;
		}

		public void setUpdateResults(UpdateResult[] updateResults) {
			this.updateResults = updateResults;
		}

		public List<UploadResult> getAllResults() {
			return allResults;
		}

		public void setAllResults(List<UploadResult> allResults) {
			this.allResults = allResults;
		}
	}

	private RepositoryResult updateRepositoryAndHandleErrors(
			boolean allowOverwrite, List<CMLDocument> cmlDocs,
			boolean optimistic, boolean acceptDuplicates) throws RepositoryAccessException,
			RepositoryException {
		RepositoryResult result = null;

		try {
			logger.debug("Trying to upload, optimistic=" + optimistic);
			result = updateRepository(cmlDocs, allowOverwrite, optimistic, acceptDuplicates);
		} catch (RepositoryFault e1) {
			if (isDuplicateChildFault(e1)) {
				logger.debug("Caught duplicate exception");
				if (!optimistic) {
					logger.error("Duplicatefault in pessimistic upload!", e1);
					throw new RepositoryException("Another process could be interfering with the same nodes on Alfresco");
				} else {
					result = updateRepositoryAndHandleErrors(allowOverwrite, cmlDocs, false, acceptDuplicates);
				}
			} else {
				throw new RepositoryException(e1.getMessage(), e1);
			}
		} catch (RemoteException e1) {
			throw new RepositoryAccessException(e1.getMessage(), e1);
		}

		return result;
	}


	private RepositoryResult updateRepository(List<CMLDocument> cmlDocs, boolean allowOverwrite, boolean optimistic,
											  boolean acceptDuplicates)
			throws RepositoryFault, RemoteException, RepositoryAccessException, RepositoryException {
		RepositoryResult result = new RepositoryResult();
		List<UploadResult> duplicates = new ArrayList();
		Map<String, Document> uuids = new HashMap(); // keep a map from existing uuids to Document objects, to be able to construct UploadResult later

		List<CMLUpdate> updates = new ArrayList<CMLUpdate>();
		List<CMLCreate> creates = new ArrayList<CMLCreate>();
		for (CMLDocument doc : cmlDocs) {
			// Check if the document exists
			if (!optimistic) {
				try {
					Reference ref = pessimisticCML(allowOverwrite, updates, creates, doc);
					if (ref != null) {
						uuids.put(ref.getUuid(), doc.getDocument());
					}
				} catch (IllegalDuplicateException e) {
					uuids.put(e.getRef().getUuid(), doc.getDocument());
					final UploadResult duplicate = new UploadResult();
					duplicate.setDocument(e.getDocument());
					duplicate.setReference(constructReferenceLink(e.getRef().getUuid()));
					duplicate.setMessage("File already exists in the repository");
					if (acceptDuplicates) {
						duplicate.setStatus(UploadResult.VALUE_OK);
					} else {
						duplicate.setStatus(UploadResult.VALUE_FAILED);
					}
					duplicates.add(duplicate);
				}
			} else {
				// First try to upload
				creates.add(doc.toCMLCreate());
			}
		}

		CML cml = new CML();
		cml.setCreate(creates.toArray(new CMLCreate[0]));
		cml.setUpdate(updates.toArray(new CMLUpdate[0]));

		UpdateResult[] updateResults = repositoryService.update(cml);

		if (updateResults == null) {
			updateResults = new UpdateResult[0];
		}

		List<UploadResult> newDocuments = new ArrayList();
		for (UpdateResult updateResult : updateResults) {
			UploadResult newDocument = new UploadResult();
			newDocument.setMessage(updateResult.getStatement());
			newDocument.setStatus(UploadResult.VALUE_OK);
			newDocument.setReference(constructReferenceLink(updateResult.getDestination().getUuid()));
			if (STATEMENT_UPDATE.equals(updateResult.getStatement())) {
				newDocument.setDocument(uuids.get(updateResult.getDestination().getUuid()));
			} else if (STATEMENT_CREATE.equals(updateResult.getStatement())) {
				newDocument.setDocument(cmlDocs.get(Integer.parseInt(updateResult.getSourceId())).getDocument());
			} else {
				throw new RepositoryException("unknown statement: " + updateResult.getStatement());
			}

			newDocuments.add(newDocument);
		}
		List<UploadResult> allResults = new ArrayList();
		allResults.addAll(newDocuments);
		allResults.addAll(duplicates);

		result.setAllResults(allResults);
		result.setUpdateResults(updateResults);

		return result;
	}

	private Reference pessimisticCML(boolean allowOverwrite,
									 List<CMLUpdate> updates, List<CMLCreate> creates, CMLDocument doc)
			throws IllegalDuplicateException, RepositoryAccessException, RepositoryException {
		Reference ref = getReference(doc.getPath(), false, true);
		if (ref != null) {
			if (allowOverwrite) {
				updates.add(doc.toCMLUpdate(ref));
			} else {
				throw new IllegalDuplicateException(doc.getDocument(), "File exists: " + doc.getXpath(), ref);
			}
		} else {
			creates.add(doc.toCMLCreate());
		}

		return ref;
	}


	@Override
	public List<UploadResult> storeDocsAndCreateParentSpaces(List<Document> documents, boolean allowOverwrite)
			throws RepositoryAccessException, RepositoryException {
		return storeDocsAndCreateParentSpaces(documents, allowOverwrite, true);
	}


	private void setAuditableProperties(List<Document> documents,
										UpdateResult[] results) {
		Iterator<Document> documentsIterator = documents.iterator();
		try {
			for (UpdateResult result : results) {
				if ("addAspect".equals(result.getStatement())) {
					continue;
				}

				Reference content = result.getDestination();
				//logger.debug("Reference:  {} {}", content.getStore().getAddress(), content.getPath());

				if (documentsIterator.hasNext()) {
					Document doc = documentsIterator.next();
					Map<String, String> auditablePropertyMap = new HashMap<String, String>();
					if (doc.meta != null) {
						for (String key : doc.meta.keySet()) {
							String value = doc.meta.get(key);
							if (auditablePropertyNameSet.contains(key)) {
								auditablePropertyMap.put(key, value);
							}
						}
					}
					if (auditablePropertyMap.size() > 0) {
						String actionResult = ActionUtils.executeAction(content,
								"edit-auditable-aspect", auditablePropertyMap);
					}
				}
			}
		} catch (WebServiceException e) {
			if ("Unable to execute action".equals(e.getMessage())) {
				logger.warn("Trying to set auditable properties but edit-auditable-aspect action not found. "
						+ "Please install move2alf-amp on the Alfresco server and make sure that the dates are in the correct format.");
			} else {
				throw e;
			}
		}
	}

	private boolean isDuplicateChildFault(RepositoryFault e) {
		Throwable cause = e.getCause();
		if (cause == null) {
			cause = e;
		}
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		cause.printStackTrace(printWriter);
		final String stackTrace = result.toString();
		return stackTrace.contains("org.alfresco.service.cmr.repository.DuplicateChildNodeNameException");
	}

	@Override
	public void clearCaches() {
		this.referenceCache.clear();
	}

	public boolean doesDocExist(String docName, String spacePath, boolean useCache)
			throws RepositoryAccessException {
		if (locateByFileNameAndPath(spacePath, docName, useCache) != null) {
			return true;
		} else {
			return false;
		}
	}

	public void updateContentByDocNameAndPath(String spacePath, String docName,
											  File docNewContent, String mimeType, boolean checkSize)
			throws RepositoryAccessException, RepositoryException {
		Reference pathRef = new Reference(store, null, companyHomePath
				+ getXPathEscape(spacePath));
		updateContentByDocNameAndSpace(pathRef, docName, docNewContent,
				mimeType, checkSize);
	}

	public void updateMetaDataByDocNameAndPath(String spacePath,
											   String docName, String nameSpace, Map<String, String> meta)
			throws RepositoryAccessException, RepositoryException {
		Reference ref = locateByFileNameAndPath(spacePath, docName, true);
		updateMetaData(ref, nameSpace, meta);
	}

	public void deleteByDocNameAndSpace(String spacePath, String docName)
			throws RepositoryAccessException, RepositoryException, DocumentNotFoundException {
		Reference pathRef = new Reference(store, null, companyHomePath
				+ getXPathEscape(spacePath));
		deleteByDocNameAndSpace(pathRef, docName);
	}

	public void deleteSpace(String spacePath, boolean onlyIfEmpty)
			throws RepositoryAccessException, RepositoryException {
		Reference spaceRef = locateSpaceByPath(spacePath);
		if (spaceRef == null) {
			logger.warn("Space {} does not exist", spacePath);
			throw new RepositoryException("Space " + spacePath
					+ " does not exist");
		} else {
			try {
				if (onlyIfEmpty) {
					// check whether there are any children

					QueryResult queryResult = repositoryService
							.queryChildren(spaceRef);
					// Display the results
					ResultSet resultSet = queryResult.getResultSet();
					if (resultSet.getRows() != null
							&& resultSet.getRows().length > 0) {
						logger.warn("Space {} not empty", spacePath);
						throw new RepositoryException("Space " + spacePath
								+ " not empty");
					}
				}
				logger.info("Reference {}", spaceRef.getPath());
				Predicate p = new Predicate(new Reference[] {spaceRef},
						store, null);

				CMLDelete delete = new CMLDelete(p);

				CML cml = new CML();
				cml.setDelete(new CMLDelete[] {delete});

				UpdateResult[] results;
				results = repositoryService.update(cml);
				for (UpdateResult result : results) {
					logger.info("DELETE..., {} ", result.getStatement());
				}
			} catch (RepositoryFault e) {
				logger.warn("Could not delete space {}", spacePath);
				throw new RepositoryException(e.getMessage(), e);
			} catch (RemoteException e) {
				throw new RepositoryAccessException(e.getMessage(), e);
			}
		}
	}

	public void setAccessControlList(String path, boolean inheritPermissions,
									 Map<String, String> accessControl)
			throws RepositoryAccessException, RepositoryException {

		Reference ref = new Reference(store, null, (new StringBuilder(
				companyHomePath)).append(getXPathEscape(path)).toString());

		setAccessControlList(ref, inheritPermissions, accessControl);
	}

	public long removeZeroSizedFromTree(String spacePath)
			throws RepositoryAccessException, RepositoryException {
		// skip if this spacepath is already handled
		if (processedDirSet.contains(spacePath)) {
			// skip
			logger.info("Space {} already handled", spacePath);
			return 0;
		} else {
			processedDirSet.add(spacePath);

			try {
				Reference reference = new Reference(store, null,
						companyHomePath + getXPathEscape(spacePath));
				// Execute the query
				QueryResult queryResult = repositoryService
						.queryChildren(reference);

				// Display the results
				ResultSet resultSet = queryResult.getResultSet();
				ResultSetRow[] rows = resultSet.getRows();

				if (rows == null) {
					logger.info("No children... ");
					return 0;
				}

				long count = 0;

				for (int i = 0; i < rows.length; i++) {
					ResultSetRowNode node = rows[i].getNode();

					NamedValue[] properties = rows[i].getColumns();
					String name = getNamedValue(properties, "name");

					if (node.getType().endsWith("folder")) {
						if (!node.getType().endsWith("systemfolder")) {
							count = count
									+ removeZeroSizedFromTree(spacePath
									+ "/cm:" + name);
						}
					} else {

						String id = node.getId();
						Reference ref = new Reference(store, id, null);
						Predicate predicate = new Predicate(
								new Reference[] {ref}, store, null);
						Content[] theContents = contentService.read(predicate,
								Constants.PROP_CONTENT);

						if (theContents != null) {
							long size = theContents[0].getLength();
							if (size == 0) {
								// delete it now
								CMLDelete delete = new CMLDelete(predicate);
								CML cml = new CML();
								cml.setDelete(new CMLDelete[] {delete});
								UpdateResult[] results = repositoryService
										.update(cml);
								for (UpdateResult result : results) {
									logger.info(result.getStatement());
								}

								StringBuffer announce = new StringBuffer();
								announce.append(spacePath);
								announce.append("  :  ");
								announce.append(name);
								announce.append(" has ZERO size: removed ");
								logger.info(announce.toString());
								count++;
							}
						} else {
							logger.warn("Could not read node id {}", id);
							throw new RepositoryException(
									"Could not read node id " + id);
						}
					}
				}
				return count;
			} catch (ContentFault e) {
				logger.warn("ContentFault", e);
				throw new RepositoryException(e.getMessage(), e);
			} catch (RepositoryFault e) {
				logger.warn("RepositoryFault", e);
				throw new RepositoryException(e.getMessage(), e);
			} catch (RemoteException e) {
				throw new RepositoryAccessException(e.getMessage(), e);
			}
		}
	}

	// PRIVATE METHODS

	// Exception handling: we differentiate between connectivity error and
	// others
	// => connectivity errors should be propagated to caller (via
	// RepositoryAccessException),
	// probably/possibly resulting in an error message in caller

	/**
	 * Method that returns a reference to an alfresco space. If the space does
	 * not exist, it will try to create the space (and the required parent
	 * spaces).
	 * <p/>
	 * To optimize performance it builds and uses a space cache.
	 * <p/>
	 * This method contains a mechanism to handle the case that another thread
	 * has created a space while this thread was attempting to create the same
	 * space (sleep and try again).
	 * <p/>
	 * It can return null if: - invalid space name - above mechanism does not
	 * work after 1 sleep period - other RepositoryFaults
	 * <p/>
	 * It will force an exit when an unexpected condition occurs (to allow that
	 * this condition can be investigated).
	 *
	 * @param path : path below 'Company Home', formatted as
	 *             /cm:Space1/cm:Space2/cm:Space3
	 * @throws RepositoryException
	 */

	protected Reference createSpaceIfNotExists(String path)
			throws RepositoryAccessException, RepositoryException {
		Semaphore lock = null;
		if (USE_FOLDER_CREATION_LOCKS) {
			synchronized (folderCreationLocks) {
				lock = folderCreationLocks.get(path);
				if (lock == null) {
					logger.debug("Creating folder creation lock for " + path);
					lock = new Semaphore(1);
					folderCreationLocks.put(path, lock);
				}
			}

			try {
				logger.debug("Acquiring folder creation lock for " + path);

				lock.acquire();
			} catch (InterruptedException e) {
				logger.error("Thread interrupted while waiting for lock");
				e.printStackTrace();
			}
		}

		Reference reference;
		try {
			reference = getReference(companyHomePath + getXPathEscape(path), true);
		} catch (RepositoryAccessException e) {
			if (USE_FOLDER_CREATION_LOCKS) {
				logger.debug("Releasing folder creation lock for " + path);
				lock.release();
			}
			throw e;
		}

		if (reference == null) {
			// get parent
			if (path.indexOf("/") != -1) {
				String parentSpacePath = path.substring(0,
						path.lastIndexOf("/"));
				logger.info("TIMESTAMP: Getting parent {} at {}", parentSpacePath, (new Date()).getTime());
				Reference parentSpaceReference = createSpaceIfNotExists(parentSpacePath);
				if (parentSpaceReference == null) {
					// on 20100211 we encountered the case that Alfresco
					// did
					// not
					// return
					// a reference for the main space /cm:Vivium (an
					// Alfresco
					// internal
					// timing problem?). to handle this, we check on
					// nullness
					// and exit
					if (USE_FOLDER_CREATION_LOCKS) {
						logger.debug("Releasing folder creation lock for " + path);
						lock.release();
					}
					logger.warn("Can not find space |{}|", parentSpacePath);
					throw new RepositoryFatalException("Can not find space " + parentSpacePath);
				}

				// skip "/cm:" part
				String spaceName = path.substring(path.lastIndexOf("/") + 4, path.length());

				// based on http://wiki.alfresco.com/wiki/
				// IngresTutorial_Alfresco_Web_Service_API_for_Java
				ParentReference parentReference = new ParentReference(store, parentSpaceReference.getUuid(), null,
						Constants.ASSOC_CONTAINS, Constants.createQNameString(
						Constants.NAMESPACE_CONTENT_MODEL, spaceName));

				logger.info("TIMESTAMP: Creating folder {} at {}", spaceName,
						(new Date()).getTime());
				// Create space
				NamedValue[] properties = new NamedValue[] {Utils
						.createNamedValue(Constants.PROP_NAME, spaceName)};
				CMLCreate create = new CMLCreate("1", parentReference, null,
						null, null, Constants.TYPE_FOLDER, properties);
				CML cml = new CML();
				cml.setCreate(new CMLCreate[] {create});

				try {
					UpdateResult[] results = repositoryService.update(cml);
					if (results.length > 0) {
						UpdateResult result = results[0];
						logger.debug(result.getStatement());
						// put in cache
						logger.info("Put {} in cache (learned from creation): {}",
								path, result.getDestination());
						referenceCache.put(getXPathEscape(path).toLowerCase(), result.getDestination());
						return result.getDestination();
					} else {
						logger.warn("Did not get result from repositoryService.update");
						throw new RepositoryException(
								"Did not get result from repositoryService.update");
					}
				} catch (RepositoryFault e) {
					// we can end up here when:
					// 1. trying to create space with invalid name =>
					// log
					// and
					// continue
					// 2. another thread created the space => wait and
					// continue
					// to lower space
					logger.info("Message {}", e.getMessage());
					Writer result = new StringWriter();
					PrintWriter printWriter = new PrintWriter(result);
					e.printStackTrace(printWriter);
					String stackTrace = result.toString();
					if (stackTrace.contains("is not valid as a file name")) {
						logger.warn("Invalid spaceName {}", spaceName);
						throw new RepositoryException("Invalid spaceName "
								+ spaceName);
					} else if (stackTrace.contains("uplicate")) {
						logger.info("SLEEPING", e.getMessage());
						logger.debug("SLEEPING", e);
						try {
							Thread.sleep(recoverySleepTime);
						} catch (InterruptedException e1) {
							logger.warn("Can't sleep", e1);
							throw new RepositoryFatalException("Can't sleep",
									e1);
						}
						// try to get it again (instead of creating)
						return getReference(path, true);
					} else if (stackTrace
							.contains("IllegalMonitorStateException")) {
						// concurrency problem at server side?
						logger.info("SLEEPING", e.getMessage());
						logger.debug("SLEEPING", e);
						try {
							Thread.sleep(recoverySleepTime);
						} catch (InterruptedException e1) {
							logger.warn("Can't sleep", e1);
							throw new RepositoryFatalException("Can't sleep",
									e1);
						}
						// try to get it again (instead of creating)
						return getReference(path, true);
					} else {
						logger.warn("Exception without special handling", e);
						throw new RepositoryException(e.getMessage(), e);
					}
				} catch (RemoteException e) {
					// connectivity problem
					throw new RepositoryAccessException(e.getMessage(), e);
				} finally {
					if (USE_FOLDER_CREATION_LOCKS) {
						logger.debug("Releasing folder creation lock for "
								+ path);
						lock.release();
					}
				}
			} else {
				if (USE_FOLDER_CREATION_LOCKS) {
					logger.debug("Releasing folder creation lock for " + path);
					lock.release();
				}
				// top level space should be there
				logger.warn("Root folder not present |{}|", path);
				throw new RepositoryFatalException("Root folder not present "
						+ path);
			}
		} else {
			if (USE_FOLDER_CREATION_LOCKS) {
				logger.debug("Releasing folder creation lock for " + path);
				lock.release();
			}
			return reference;
		}
	}

	private Reference getReference(String path, boolean useCache) throws RepositoryAccessException {
		return getReference(path, true, useCache);
	}

	private Reference getReference(String path, boolean escaped, boolean useCache)
			throws RepositoryAccessException {
		logger.debug("TIMESTAMP: Check existence space |{}| at {}", path, (new Date()).getTime());
		String escapedPath = new String(path);

		if (!escaped)
			escapedPath = getXPathEscape(path);

		Reference reference = null;
		if (useCache) {
			reference = referenceCache.get(escapedPath.toLowerCase());
		}

		if (reference == null) {
			if (useCache) {
				if (referenceCache.size() > maxSizeReferenceCache) {
					// avoid cache becoming too large
					logger.info("Clearing Space Cache");
					referenceCache.clear();
				}
			}

			Reference uncheckedReference = new Reference(store, null, escapedPath);
			// can throw:
			// java.rmi.RemoteException: when connection problem?
			// org.alfresco.webservice.repository.RepositoryFault: this will
			// be thrown if space can not be found (experimentally
			// determined)
			// see also
			// https://issues.alfresco.com/jira/browse/ALFCOM-1033?page
			// =com.atlassian
			// .jira.plugin.system.issuetabpanels%3Aall-tabpanel

			Node[] nodes;
			try {
				nodes = repositoryService.get(new Predicate(new Reference[] {uncheckedReference}, store, null));
				reference = nodes[0].getReference();

				logger.info("Put {} in cache (learned from reference query) for path {}", reference.getUuid(), escapedPath);
				if (useCache) {
					referenceCache.put(escapedPath.toLowerCase(), reference);
				}
			} catch (RepositoryFault e) {
				// space does not exist, return null
				logger.debug("Could not get results from reference query for path {}", path);
				
				if(enableLuceneFallback){
					logger.debug("Falling back to lucene search");
					// fallback to Lucene search
					// happens when searching case sensitive for folder or file names
					String luceneQuery = "\\@" + escapeQName(QName.createQName(Constants.NAMESPACE_CONTENT_MODEL, "name")) + ":\"" + getNameFromPath(path) + "\"";
					List<Reference> references;
					try {
						references = locateByLuceneQuery(luceneQuery, MAX_LUCENE_RESULTS);
					} catch (RepositoryException e1) {
						logger.error("Could not get lucene reference for path {}", path);
						throw new RuntimeException(e1);
					}
					if (references.size() > 0) {
						for (int i = 0; i < references.size(); i++) {
							logger.debug("Comparing " + references.get(i).getPath() + " with " + escapedPath);
							if (equalPaths(references.get(i).getPath(), escapedPath)) {
								logger.info("Found lucene reference {} for path {}", references.get(i).getUuid(), references.get(i).getPath());
								return references.get(i);
							}
						}
					}
					logger.debug("Could not get lucene reference for path {}", path);
				}
				return null;
			} catch (RemoteException e) {
				// connectivity problem
				throw new RepositoryAccessException(e.getMessage(), e);
			}
		} else {
			logger.info("Obtained reference from cache {}", escapedPath);
		}
		return reference;
	}

	// first path: /{http://www.alfresco.org/model/application/1.0}company_home/{http://www.alfresco.org/model/content/1.0}Move2alf/{http://www.alfresco.org/model/content/1.0}file1.txt
	// second path: /app:company_home/cm:move2alf/cm:File1.txt
	// should be equal case insensitive
	private boolean equalPaths(String path, String path2) {
		// replace prefix namespace with uri in path2
		String newPath2 = path2.replaceAll("/cm:", "/{" + Constants.NAMESPACE_CONTENT_MODEL + "}");
		newPath2 = newPath2.replaceAll("/app:", "/{" + Constants.NAMESPACE_APPLICATION_MODEL + "}");

		//logger.info("path=" + String.valueOf(path) + " and newPath2=" + String.valueOf(newPath2) + " and equality=" + String.valueOf(path).compareTo(String.valueOf(newPath2)));
		return (path.toLowerCase().compareTo(newPath2.toLowerCase()) == 0);
	}

	private String escapeQName(QName qname) {
		return LuceneQueryParser.escape(qname.toString());
	}


	private String getNameFromPath(String path) {
		String sDelimiters = "/:";
		StringTokenizer T = new StringTokenizer(path, sDelimiters, true);
		String name = path;

		while (T.hasMoreTokens()) {
			name = T.nextToken();
		}
		return name;
	}

	private void addDocumentToCML(File file, String mimeType,
								  Reference parentSpace, String description,
								  String contentModelNamespace, String contentModelType,
								  Map<String, String> meta, Map<String, String> multiValueMeta, CML cml, String cmlId)
			throws RepositoryAccessException, RepositoryException {
		if (parentSpace == null) {
			logger.warn("ParentSpace is null, can not store {}", file.getName());
			throw new RepositoryException("ParentSpace is null");
		}
		ParentReference parentRef = new ParentReference(store,
				parentSpace.getUuid(), null, Constants.ASSOC_CONTAINS, null);

		logger.debug("Path {}", file.getAbsolutePath());

		logger.debug("ContentModelNamespace {}", contentModelNamespace);
		logger.debug("ContentModelType {}", contentModelType);

		String fileName = file.getName();

		logger.debug("Filename {}", fileName);

		String contentDetails = putContent(file, mimeType);

		// audtiable properties need a special handling, they can not be set
		// like other
		// properties,
		int nbrOfAuditableProperties = 0;
		if (meta != null) {
			for (String auditablePropertyName : auditablePropertyNameSet) {
				if (meta.keySet().contains(auditablePropertyName))
					nbrOfAuditableProperties++;
			}
		}
		int nbrOfNonAuditableProperties = ((meta != null) ? meta.size() : 0)
				+ ((multiValueMeta != null) ? multiValueMeta.size() : 0) + 2 // fixed
				// properties
				- nbrOfAuditableProperties;

		List<NamedValue> contentProps = new ArrayList<NamedValue>();

		// these properties are always present
		contentProps.add(Utils.createNamedValue(Constants.PROP_NAME, fileName));
		contentProps.add(Utils.createNamedValue(Constants.PROP_CONTENT,
				contentDetails));

		if (meta != null) {
			// Enumeration<String> E = meta.;
			// while (E.hasMoreElements()) {
			processMetadata(contentModelNamespace, meta, contentProps);
		}

		// multiValue properties
		if (multiValueMeta != null) {
			processMultiValuedMetadata(contentModelNamespace, multiValueMeta,
					contentProps);
		}

		parentRef.setChildName("{http://www.alfresco.org/model/content/1.0}"
				+ fileName);
		logger.debug("Childname set");

		// a title aspect will always be added
		NamedValue[] titledProps = new NamedValue[2];
		titledProps[0] = Utils.createNamedValue(Constants.PROP_TITLE,
				description);
		titledProps[1] = Utils.createNamedValue(Constants.PROP_DESCRIPTION,
				description);
		CMLAddAspect titleAspect = new CMLAddAspect(Constants.ASPECT_TITLED,
				titledProps, null, cmlId);

		CMLCreate[] createsArray = cml.getCreate();
		List<CMLCreate> creates;
		if (createsArray == null) {
			creates = new ArrayList<CMLCreate>();
		} else {
			creates = new ArrayList<CMLCreate>(Arrays.asList(createsArray));
		}

		CMLAddAspect[] addAspectsArray = cml.getAddAspect();
		List<CMLAddAspect> addAspects;
		if (addAspectsArray == null) {
			addAspects = new ArrayList<CMLAddAspect>();
		} else {
			addAspects = new ArrayList<CMLAddAspect>(Arrays.asList(addAspectsArray));
		}

		CMLCreate create = new CMLCreate(cmlId, parentRef, parentSpace.getUuid(),
				Constants.ASSOC_CONTAINS, null, /* Constants.TYPE_CONTENT */
				contentModelNamespace + contentModelType, contentProps.toArray(new NamedValue[0]));

		creates.add(create);
		addAspects.add(titleAspect);

		cml.setCreate(creates.toArray(new CMLCreate[creates.size()]));
		cml.setAddAspect(addAspects.toArray(new CMLAddAspect[addAspects.size()]));
	}

	protected void processMultiValuedMetadata(String contentModelNamespace,
											  Map<String, String> multiValueMeta, List<NamedValue> contentProps) {
		for (String key : multiValueMeta.keySet()) {
			String val = multiValueMeta.get(key);
			//logger.debug("Prop - Value:  {} - {}", key, val);
			List<String> valList = new ArrayList<String>();
			StringTokenizer tokenizer = new StringTokenizer(val, ",");
			while (tokenizer.hasMoreElements()) {
				String token = tokenizer.nextToken();
				valList.add(token);
			}
			if (!key.startsWith("{")) {
				key = contentModelNamespace + key;
			}
			contentProps.add(Utils.createNamedValue(key,
					(String[]) valList.toArray(new String[valList.size()])));
		}
	}

    @Override
	public String putContent(File file, String mimeType) {
		String contentDetails = ContentUtils.putContent(file, host, port, webapp,
				mimeType, "UTF-8");
		logger.debug("File put in repository, details: {}", contentDetails);
		return contentDetails;
	}

	protected void processMetadata(String contentModelNamespace,
								   Map<String, String> meta, List<NamedValue> contentProps) {
		for (String key : meta.keySet()) {
			String value = meta.get(key);
			//logger.debug("Prop - Value:  {} - {}", key, value);
			if (!auditablePropertyNameSet.contains(key)) {
				if (!key.startsWith("{")) {
					key = contentModelNamespace + key;
				}
				contentProps.add(Utils.createNamedValue(key, value));
			}
		}
	}

	private Reference locateSpaceByPath(String path)
			throws RepositoryAccessException, RepositoryException {

		Reference pathref = new Reference(store, null, companyHomePath
				+ getXPathEscape(path));

		Node[] nodes;
		try {
			nodes = repositoryService.get(new Predicate(
					new Reference[] {pathref}, store, null));
			if (nodes.length > 0) {

				return nodes[0].getReference();
			} else {
				return null;
			}
		} catch (RepositoryFault e) {
			logger.warn("Can not get space reference", e);
			throw new RepositoryException(e.getMessage(), e);
		} catch (RemoteException e) {
			// connectivity problem
			throw new RepositoryAccessException(e.getMessage(), e);
		}
	}

	private Reference locateByFileNameAndPath(String parentPath, String name, boolean useCache)
			throws RepositoryAccessException {

		String path = getXPathEscape(companyHomePath
				+ parentPath + "/cm:" + name);

		return getReference(path, useCache);
	}

	private Reference locateByFileNameAndSpace(Reference parent, String name, boolean useCache)
			throws RepositoryAccessException {
		String path = parent.getPath() + "/cm:" + getXPathEscape(name);
		logger.info("Locating document: " + path);

		return getReference(path, useCache);
	}

	// note that this can be unsafe when the filename is not unique
	private Reference locateByFileName(String name)
			throws RepositoryAccessException, RepositoryException {

		Query query = new Query(Constants.QUERY_LANG_LUCENE, "@cm\\:name:\"" + name + "\"");
		// Execute the query
		QueryResult queryResult;
		try {
			queryResult = repositoryService.query(store, query, true);
			// Display the results
			ResultSet resultSet = queryResult.getResultSet();
			ResultSetRow[] rows = resultSet.getRows();

			if (rows == null) {
				logger.warn("NO ROWS: NULL");
				throw new RepositoryException("NO ROWS: NULL");
			}
			logger.info("FOUND " + rows.length + " results... ");

			String firstResultId = rows[0].getNode().getId();

			Reference ref = new Reference(store, firstResultId, null);

			return ref;
		} catch (RepositoryFault e) {
			logger.info("Cannot locate {}", name);
			return null;
		} catch (RemoteException e) {
			// connectivity problem
			throw new RepositoryAccessException(e.getMessage(), e);
		}
	}

	private long getSize(Reference ref) throws RepositoryAccessException {

		long size = -1;
		Content[] theContents;
		try {
			theContents = contentService.read(new Predicate(
					new Reference[] {ref}, store, null),
					Constants.PROP_CONTENT);
			if (theContents != null)
				size = theContents[0].getLength();
		} catch (ContentFault e) {
			// return -1
		} catch (RemoteException e) {
			throw new RepositoryAccessException(e.getMessage(), e);
		}
		return size;
	}

	private void deleteByDocNameAndSpace(Reference parent, String docName)
			throws RepositoryAccessException, RepositoryException, DocumentNotFoundException {
		Reference ref = locateByFileNameAndSpace(parent, docName, true);
		// acquire a content reference ...
		if (ref != null) {
			logger.info("Reference {}", ref.getPath());
			Predicate p = new Predicate(new Reference[] {ref}, store, null);

			CMLDelete delete = new CMLDelete(p);

			CML cml = new CML();
			cml.setDelete(new CMLDelete[] {delete});

			UpdateResult[] results;
			try {
				results = repositoryService.update(cml);
				for (UpdateResult result : results) {
					logger.info("DELETE..., {} ", result.getStatement());
					referenceCache.remove(ref.getPath().toLowerCase());
					logger.info("Deleted from cache {}", ref.getPath());
				}
			} catch (RepositoryFault e) {
				logger.warn("Could not delete file {}", docName);
				throw new RepositoryException(e.getMessage(), e);
			} catch (RemoteException e) {
				throw new RepositoryAccessException(e.getMessage(), e);
			}
		} else {
			logger.info("File {} is not present in {}", docName,
					parent.getPath());
			throw new DocumentNotFoundException("File " + docName
					+ " is not present in " + parent.getPath());
		}
	}

	private void deleteByReference(Reference reference)
			throws RepositoryAccessException, RepositoryException {
		logger.info("Reference {}", reference.getPath());
		Predicate p = new Predicate(new Reference[] {reference}, store, null);

		CMLDelete delete = new CMLDelete(p);

		CML cml = new CML();
		cml.setDelete(new CMLDelete[] {delete});
		try {
			UpdateResult[] results = repositoryService.update(cml);
			for (UpdateResult result : results) {
				logger.info("DELETE..., {} ", result.getStatement());
				referenceCache.remove(reference.getPath().toLowerCase());
				logger.info("Deleted from cache {}", reference.getPath());
			}
		} catch (RepositoryFault e) {
			logger.warn("Could not delete file {}", reference.getPath());
			throw new RepositoryException(e.getMessage(), e);
		} catch (RemoteException e) {
			throw new RepositoryAccessException(e.getMessage(), e);
		}
	}

	private Reference updateContentByDocNameAndSpace(Reference parent,
													 String docName, File docNewContent, String mimeType,
													 boolean checkSize) throws RepositoryAccessException,
			RepositoryException {
		Reference contentReference = locateByFileNameAndSpace(parent, docName, true);

		if (contentReference == null) {
			logger.warn("Document {} not known in space {}", docName,
					parent.getPath());
			return null;
		}

		if (checkSize) {
			long sizeStoredFile = -1;
			sizeStoredFile = getSize(contentReference);
			if (sizeStoredFile == docNewContent.length()) {
				logger.info("Size of stored file {} is ok ({})",
						docNewContent.getName(), sizeStoredFile);
				return contentReference;
			} else if (sizeStoredFile == -1) {
				logger.warn("Can not obtain size of stored file");
				throw new RepositoryException(
						"Can not obtain size of stored file");
			}
		}

		logger.info("About to update {} with content of {}", docName,
				docNewContent.getName());

		String contentDetails = ContentUtils.putContent(docNewContent, host,
				port, webapp, mimeType, "UTF-8");
		logger.debug("File put in repository");

		NamedValue[] contentProps = new NamedValue[1];

		contentProps[0] = Utils.createNamedValue(Constants.PROP_CONTENT,
				contentDetails);
		Predicate pred = new Predicate(new Reference[] {contentReference},
				store, null);

		CMLUpdate cmlUpdate = new CMLUpdate(contentProps, pred, null);
		CML cml = new CML();
		cml.setUpdate(new CMLUpdate[] {cmlUpdate});

		try {
			repositoryService.update(cml);
		} catch (RepositoryFault e) {
			throw new RepositoryAccessException(e.getMessage(), e);
		} catch (RemoteException e) {
			throw new RepositoryAccessException(e.getMessage(), e);
		} catch (Exception e) {
			logger.warn("File update error", e);
			throw new RepositoryException(
					"File update error " + e.getMessage(), e);
		}
		return contentReference;
	}

	private void updateMetaData(Reference reference, String nameSpace, Map<String, String> meta)
			throws RepositoryAccessException, RepositoryException {

		List<NamedValue> contentProps = new ArrayList<NamedValue>();

		processMetadata(nameSpace, meta, contentProps);

		Predicate predicate = new Predicate(new Reference[] {reference},
				store, null);

		CMLUpdate update = new CMLUpdate(contentProps.toArray(new NamedValue[0]), predicate, null);

		CML cml = new CML();

		cml.setUpdate(new CMLUpdate[] {update});

		try {
			repositoryService.update(cml);
		} catch (RepositoryFault e) {
			logger.warn("Could not update metadata", e);
			throw new RepositoryException(e.getMessage(), e);
		} catch (RemoteException e) {
			throw new RepositoryAccessException(e.getMessage(), e);
		}
	}

	private void setAccessControlList(Reference ref,
									  boolean inheritPermissions, Map<String, String> accessControl)
			throws RepositoryAccessException, RepositoryException {
		try {
			Predicate predicate = new Predicate(new Reference[] {ref}, store,
					null);

			ACE[] aces = new ACE[accessControl.size()];

			int count = 0;
			logger.info("Size Access Control list=" + accessControl.size() + " for "+ref.getPath());
			for (String key : accessControl.keySet()) {
				String authority = key;
				logger.info("Authority {}", authority);
				String permission = accessControl.get(key);
				logger.info("Permission {}", permission);
				ACE ace = new ACE();
				ace.setAuthority(authority);
				ace.setPermission(permission);
				ace.setAccessStatus(AccessStatus.fromString("acepted"));
				aces[count] = ace;
				count++;
			}

            if(aces.length>0) {
                // remove existing ...
                logger.info("Removing ACEs");
                accessControlService.removeACEs(predicate, null);
                logger.info("Adding ACEs");
                accessControlService.addACEs(predicate, aces);
                logger.info("Setting Inherit Permission to {}", inheritPermissions);
                accessControlService.setInheritPermission(predicate,
                        inheritPermissions);
            }
			// } catch (AccessControlFault e) {
			// logger.warn(e.getMessage() + " space: " + ref.getPath(), e);
			// throw new RepositoryException(e.getMessage());
		} catch (RemoteException e) {
			logger.info("Problem setting access rights", e);
			throw new RepositoryAccessException(e.getMessage(), e);
		}
	}

	// dangerous version (if filename is not unique in system)!
	private String getProperty(String filename, String property)
			throws RepositoryAccessException, RepositoryException {
		try {

			Query query = new Query(Constants.QUERY_LANG_LUCENE,
					"@cm\\:name:\"" + filename + "\"");

			// Execute the query
			QueryResult queryResult = repositoryService.query(store, query,
					true);

			// Display the results
			ResultSet resultSet = queryResult.getResultSet();
			ResultSetRow[] rows = resultSet.getRows();

			if (rows == null) {
				logger.warn("Could not find: " + filename);
				throw new RepositoryException("Could not find: " + filename);
			}
			if (rows.length > 1) {
				logger.warn("Duplicate filename: " + filename);
				throw new RepositoryException("Duplicate filename: " + filename);
			}
			NamedValue[] properties = rows[0].getColumns();
			String propertyval = getNamedValue(properties, property);
			return propertyval;
		} catch (RepositoryFault e) {
			logger.warn(e.getMessage() + " file : " + filename, e);
			throw new RepositoryException(e.getMessage(), e);
		} catch (RemoteException e) {
			throw new RepositoryAccessException(e.getMessage(), e);
		}
	}

	private List<Reference> locateByLuceneQuery(String luceneQueryString,
												int maxNbrOfResults) throws RepositoryAccessException,
			RepositoryException {
		Query query = new Query(Constants.QUERY_LANG_LUCENE, luceneQueryString);
		logger.info("Query {}", query.getStatement());

		List<Reference> referenceList = new ArrayList<Reference>();

		QueryConfiguration queryCfg = new QueryConfiguration();
		queryCfg.setFetchSize(maxNbrOfResults);// batchSize

		try {
			repositoryService.setHeader(new RepositoryServiceLocator()
					.getServiceName().getNamespaceURI(), "QueryHeader",
					queryCfg);

			logger.info("Store {}", store.getAddress());
			QueryResult queryResult = repositoryService.query(store, query,
					false);
			

			ResultSet resultSet = queryResult.getResultSet();
			ResultSetRow[] rows = resultSet.getRows();

			//outputResultSet(rows);

			if (rows != null) {
				logger.info("Number of rows {}", rows.length);
				for (ResultSetRow row : rows) {
					String resultId = row.getNode().getId();
					NamedValue[] columns = row.getColumns();
					String resultPath = "";
					for (int i = 0; i < columns.length; i++) {
						if ("{http://www.alfresco.org/model/content/1.0}path".equals(columns[i].getName()))
							resultPath = columns[i].getValue();
					}
					Reference reference = new Reference(store, resultId, resultPath);
					referenceList.add(reference);
				}
				// if there are still results; is there a better way to do this?
				while (rows.length == maxNbrOfResults) {
					try {
					String querySession = queryResult.getQuerySession();
					queryResult = repositoryService.fetchMore(querySession);

					resultSet = queryResult.getResultSet();
					rows = resultSet.getRows();

					outputResultSet(rows);

					if (rows != null) {
						logger.info("Number of rows {}", rows.length);
						for (ResultSetRow row : rows) {
							String resultId = row.getNode().getId();
							NamedValue[] columns = row.getColumns();
							String resultPath = "";
							for (int i = 0; i < columns.length; i++) {
								if ("{http://www.alfresco.org/model/content/1.0}path".equals(columns[i].getName()))
									resultPath = columns[i].getValue();
							}
							Reference reference = new Reference(store, resultId, resultPath);
							referenceList.add(reference);
						}
					}
					} catch (RepositoryFault e) {
						 logger.warn("Caught a repository exception when doing a fetchMore, aborting Lucene query. This is a workaround, we should not get a repository exception.");
						 break;
					}
				}
			}
		} catch (RepositoryFault e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (RemoteException e) {
			throw new RepositoryAccessException(e.getMessage(), e);
		}

		return referenceList;
	}

	// helper methods

	/**
	 * Helper method to output the rows contained within a result set
	 *
	 * @param rows an array of rows
	 */
	private void outputResultSet(ResultSetRow[] rows) {
		if (rows != null) {
			for (int x = 0; x < rows.length; x++) {
				ResultSetRow row = rows[x];

				NamedValue[] columns = row.getColumns();
				for (int y = 0; y < columns.length; y++) {
					logger.debug("row " + x + ": "
							+ row.getColumns(y).getName() + " = "
							+ row.getColumns(y).getValue());
				}
			}
		}
	}

	/**
	 * The ISO9075 encoding class should be used to encode local names. This
	 * method will encode all local names found in a path. For example
	 * "/app:company_home/cm:My Space//*" will become
	 * "/app:company_home/cm:My_x0020_Space//*"
	 */
	public static String getXPathEscape(String psToEncode) {
		StringBuilder sbResult = new StringBuilder(psToEncode.length());

		String sDelimiters = ":/_*";
		StringTokenizer T = new StringTokenizer(psToEncode, sDelimiters, true);

		while (T.hasMoreTokens()) {
			String sToken = T.nextToken();

			if (sDelimiters.contains(sToken))
				sbResult.append(sToken);
			else {
				sbResult.append(ISO9075.encode(sToken));
			}
		}

		return sbResult.toString();
	}

	private String getNamedValue(NamedValue[] namedValues, String name) {
		String value = null;

		for (int i = 0; i < namedValues.length; i++) {
			NamedValue namedValue = namedValues[i];
			// endsWith, because name has namespace prefix
			if (namedValue.getName().endsWith(name)) {
				value = namedValue.getValue();
				break;
			}
		}

		return value;
	}

	@Override
	public boolean doesFileNameExists(String name) throws RepositoryAccessException, RepositoryException {
		try {
			locateByFileName(name);
			return true;
		} catch (RepositoryException e) {
			return false;
		}
	}
}
