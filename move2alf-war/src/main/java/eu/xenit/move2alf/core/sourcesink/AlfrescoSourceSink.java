package eu.xenit.move2alf.core.sourcesink;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.alfresco.webservice.util.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.repository.IllegalDocumentException;
import eu.xenit.move2alf.repository.PartialUploadFailureException;
import eu.xenit.move2alf.repository.RepositoryAccessException;
import eu.xenit.move2alf.repository.RepositoryAccessSession;
import eu.xenit.move2alf.repository.RepositoryException;
import eu.xenit.move2alf.repository.RepositoryFatalException;
import eu.xenit.move2alf.repository.alfresco.ws.Document;
import eu.xenit.move2alf.repository.alfresco.ws.WebServiceRepositoryAccess;

public class AlfrescoSourceSink extends SourceSink {

	private static final String PARAM_URL = "url";
	private static final String PARAM_PASSWORD = "password";
	private static final String PARAM_USER = "user";
	private static final boolean OPTIMISTIC = true;

	private static ThreadLocal<RepositoryAccessSession> ras = new ThreadLocal<RepositoryAccessSession>();

	private static final Logger logger = LoggerFactory
			.getLogger(AlfrescoSourceSink.class);

	@Override
	public List<File> list(final ConfiguredSourceSink sourceConfig,
			final String path, final boolean recursive) {
		return null;
	}

	@Override
	public void send(final ConfiguredSourceSink configuredSourceSink,
			final String docExistsMode,
			final String remotePath, final String mimeType,
			final String namespace, final String contentType,
			final String description, final Map<String, String> metadata,
			final Map<String, String> multiValueMetadata,
			final File document) throws IllegalDocumentException {
		try {
			final RepositoryAccessSession ras = createRepositoryAccessSession(configuredSourceSink);
			try {
				logger.debug("Uploading file " + document.getName() + " to remotePath " + remotePath);
				uploadFile(docExistsMode, ras, remotePath, mimeType,
						namespace, contentType, description, metadata,
						multiValueMetadata, document);
			} catch (final RepositoryAccessException e) {
				if (!(e.getMessage() == null)
						&& (e.getMessage()
								.indexOf("security processing failed") != -1)) {
					retryUpload(configuredSourceSink, docExistsMode,
							remotePath, mimeType, namespace, contentType,
							description, metadata, multiValueMetadata, document);
				} else {
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				}
			} catch (final RuntimeException e) {
				if ("Error writing content to repository server".equals(e
						.getMessage())) {
					retryUpload(configuredSourceSink, docExistsMode, 
							remotePath, mimeType, namespace, contentType,
							description, metadata, multiValueMetadata, document);
				} else {
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				}
			}
		} catch (final RepositoryAccessException e) {
			// we end up here if there is a communication error during a session
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		} catch (final RepositoryException e) {
			// we end up here if the request could not be handled by the
			// repository
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		} catch (final WebServiceException e) {
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		} catch (final RepositoryFatalException e) {
			logger.error("Fatal Exception", e);
			throw new Move2AlfException(e.getMessage(), e);
			// TODO: stop job instead of stopping tomcat
			// System.exit(1);
		} catch (final RuntimeException e2) {
			logger.error(e2.getMessage(), e2);
			throw new Move2AlfException(e2.getMessage(), e2);
		}
	}

	@Override
	public void sendBatch(final ConfiguredSourceSink configuredSourceSink,
			final String docExistsMode, final List<Document> documents) throws PartialUploadFailureException {
		try {
			final RepositoryAccessSession ras = createRepositoryAccessSession(configuredSourceSink);
			try {
				logger.debug("Uploading " + documents.size() + " files");
				uploadBatch(docExistsMode, ras, documents);
			} catch (final RepositoryAccessException e) {
				if (!(e.getMessage() == null)
						&& (e.getMessage()
								.indexOf("security processing failed") != -1)) {
					retryBatch(configuredSourceSink, docExistsMode, documents);
				} else {
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				}
			} catch (final RuntimeException e) {
				if ("Error writing content to repository server".equals(e
						.getMessage())) {
					retryBatch(configuredSourceSink, docExistsMode, documents);
				} else {
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				}
			}
		} catch (final RepositoryAccessException e) {
			// we end up here if there is a communication error during a session
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		} catch (final RepositoryException e) {
			// we end up here if the request could not be handled by the
			// repository
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		} catch (final WebServiceException e) {
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		} catch (final RepositoryFatalException e) {
			logger.error("Fatal Exception", e);
			throw new Move2AlfException(e.getMessage(), e);
			// TODO: stop job instead of stopping tomcat
			// System.exit(1);
		} catch (final RuntimeException e2) {
			logger.error(e2.getMessage(), e2);
			throw new Move2AlfException(e2.getMessage(), e2);
		}
	}

	private void uploadBatch(final String docExistsMode,
		final RepositoryAccessSession ras, final List<Document> documents)
		throws RepositoryAccessException, RepositoryException, PartialUploadFailureException {
		
		boolean overwrite = MODE_OVERWRITE.equals(docExistsMode);
		ras.storeDocsAndCreateParentSpaces(documents, overwrite, OPTIMISTIC);
	}

	private void retryBatch(final ConfiguredSourceSink configuredSourceSink,
			final String docExistsMode, final List<Document> documents)
			throws RepositoryAccessException, RepositoryException, PartialUploadFailureException {
		logger.debug("Authentication failure? Creating new RAS");
		destroyRepositoryAccessSession();
		final RepositoryAccessSession ras = createRepositoryAccessSession(configuredSourceSink);

		logger.debug("Retrying batch");
		uploadBatch(docExistsMode, ras, documents);
	}

	@Override
	public void setACL(final ConfiguredSourceSink configuredSourceSink,
			final ACL acl) {
		if (acl != null && acl.acls != null) {
			final RepositoryAccessSession ras = createRepositoryAccessSession(configuredSourceSink);
			for (final String aclPath : acl.acls.keySet()) {
				try {
					ras.setAccessControlList(aclPath, acl.inheritsPermissions,
							acl.acls.get(aclPath));
				} catch (final RepositoryAccessException e) {
					// we end up here if there is a communication error during a
					// session
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				} catch (final RepositoryException e) {
					// we end up here if the request could not be handled by the
					// repository
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				} catch (final WebServiceException e) {
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				} catch (final RepositoryFatalException e) {
					logger.error("Fatal Exception", e);
					throw new Move2AlfException(e.getMessage(), e);
					// TODO: stop job instead of stopping tomcat
					// System.exit(1);
				} catch (final RuntimeException e2) {
					logger.error(e2.getMessage(), e2);
					throw new Move2AlfException(e2.getMessage(), e2);
				}
			}
		}
	}

	private static void retryUpload(
			final ConfiguredSourceSink configuredSourceSink,
			final String docExistsMode,
			final String remotePath, final String mimeType,
			final String namespace, final String contentType,
			final String description, final Map<String, String> metadata,
			final Map<String, String> multiValueMetadata,
			final File document)
			throws RepositoryAccessException, RepositoryException, IllegalDocumentException {
		logger.debug("Authentication failure? Creating new RAS");
		destroyRepositoryAccessSession();
		final RepositoryAccessSession ras = createRepositoryAccessSession(configuredSourceSink);

		logger.debug("Retrying file " + document.getName());
		uploadFile(docExistsMode, ras, remotePath, mimeType,
				namespace, contentType, description, metadata,
				multiValueMetadata, document);
	}

	private static void uploadFile(final String docExistsMode,
			final RepositoryAccessSession ras,
			final String remotePath, final String mimeType,
			final String namespace, final String contentType,
			final String description, final Map<String, String> metadata,
			final Map<String, String> multiValueMetadata,
			final File document)
			throws RepositoryAccessException, RepositoryException, IllegalDocumentException {
		try {
			ras.storeDocAndCreateParentSpaces(document, mimeType, remotePath,
					description, namespace, contentType, metadata,
					multiValueMetadata);
		} catch (final RepositoryException e) {
			Throwable cause = e.getCause();
			if (cause == null) {
				cause = e;
			}
			logger.info("Message {}", cause.getMessage());
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			cause.printStackTrace(printWriter);
			final String stackTrace = result.toString();
			if (stackTrace
					.contains("org.alfresco.service.cmr.repository.DuplicateChildNodeNameException")) {
				if (MODE_SKIP.equals(docExistsMode)) {
					// ignore
				} else if (MODE_SKIP_AND_LOG.equals(docExistsMode)) {
					logger.warn("Document " + document.getName()
							+ " already exists in " + remotePath);
					throw new Move2AlfException("Document "
							+ document.getName() + " already exists in "
							+ remotePath);
				} else if (MODE_OVERWRITE.equals(docExistsMode)) {
					logger.info("Overwriting document " + document.getName()
							+ " in " + remotePath);
					ras.updateContentByDocNameAndPath(remotePath,
							document.getName(), document, mimeType, false);
					if (metadata != null) {
						ras.updateMetaDataByDocNameAndPath(remotePath,
								document.getName(), namespace, metadata);
						// TODO: updating multivalue metadata not supported by
						// RRA?
					}
				}
			} else {
				throw e;
			}
		}
	}

	@Override
	public boolean exists(final ConfiguredSourceSink sinkConfig,
			final String remotePath, final String name) {
		try {
			try {
				final RepositoryAccessSession ras = createRepositoryAccessSession(sinkConfig);
				return ras.doesDocExist(name, remotePath);
			} catch (final RepositoryAccessException e) {
				if (!(e.getMessage() == null)
						&& (e.getMessage()
								.indexOf("security processing failed") != -1)) {
					// retry
					destroyRepositoryAccessSession();
					final RepositoryAccessSession ras = createRepositoryAccessSession(sinkConfig);
					try {
						return ras.doesDocExist(name, remotePath);
					} catch (final RepositoryAccessException e2) {
						logger.error(e2.getMessage(), e2);
						throw new Move2AlfException(e2.getMessage(), e2);
					}
				} else {
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				}
			}
		} catch (final RuntimeException e) {
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		}
	}

	@Override
	public void delete(final ConfiguredSourceSink sinkConfig,
			final String remotePath, final String name) {
		try {
			try {
				final RepositoryAccessSession ras = createRepositoryAccessSession(sinkConfig);
				ras.deleteByDocNameAndSpace(remotePath, name);
			} catch (final RepositoryAccessException e) {
				if (!(e.getMessage() == null)
						&& (e.getMessage()
								.indexOf("security processing failed") != -1)) {
					// retry
					destroyRepositoryAccessSession();
					final RepositoryAccessSession ras = createRepositoryAccessSession(sinkConfig);
					try {
						ras.deleteByDocNameAndSpace(remotePath, name);
					} catch (final RepositoryAccessException e2) {
						logger.error(e.getMessage(), e);
						throw new Move2AlfException(e.getMessage(), e);
					}
				} else {
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				}
			}
		} catch (final RepositoryException e) {
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		} catch (final RuntimeException e) {
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		}
	}

	@Override
	public void clearCaches(final ConfiguredSourceSink sinkConfig) {
		RepositoryAccessSession ras = createRepositoryAccessSession(sinkConfig);
		if(ras != null) {
			ras.clearCaches();
		} else {
			logger.warn("Tried to clear caches of inexistent RepositoryAccessSession");
		}
	}

	private static RepositoryAccessSession createRepositoryAccessSession(
			final ConfiguredSourceSink sinkConfig) {
		// RepositoryAccessSession ras;
		RepositoryAccessSession ras = AlfrescoSourceSink.ras.get();
		if (ras == null) {
			logger.debug("Creating new RepositoryAccessSession for thread "
					+ Thread.currentThread());
			final String user = sinkConfig.getParameter(PARAM_USER);
			final String password = sinkConfig.getParameter(PARAM_PASSWORD);
			String url = sinkConfig.getParameter(PARAM_URL);
			if (url.endsWith("/")) {
				url = url + "api/";
			} else {
				url = url + "/api/";
			}
			WebServiceRepositoryAccess ra = null;
			try {
				ra = new WebServiceRepositoryAccess(new URL(url), user,
						password);
			} catch (final MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ras = ra.createSessionAndRetry();
			AlfrescoSourceSink.ras.set(ras);
		} else {
			logger.debug("Reusing existing RepositoryAccessSession in thread "
					+ Thread.currentThread());
		}
		return AlfrescoSourceSink.ras.get();
		// return ras;
	}

	private static void destroyRepositoryAccessSession() {
		AlfrescoSourceSink.ras.get().closeSession();
		AlfrescoSourceSink.ras.remove();
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_DESTINATION;
	}

	@Override
	public String getDescription() {
		return "Alfresco using SOAP web services";
	}

	@Override
	public String getName() {
		return "Alfresco";
	}
}
