package eu.xenit.move2alf.core.sourcesink;

import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.ApplicationContextProvider;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.repository.*;
import eu.xenit.move2alf.repository.alfresco.ws.Document;
import eu.xenit.move2alf.repository.alfresco.ws.WebServiceRepositoryAccess;
import org.alfresco.webservice.util.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class AlfrescoSourceSink extends SourceSink {
	
	private static final String PARAM_URL = "url";
	private static final String PARAM_PASSWORD = "password";
	private static final String PARAM_USER = "user";

    @Value(value = "#{'${repo.overwrite.optimistic}'}")
    private boolean overwriteOptimistic;

    @Value(value = "#{'${repo.create.optimistic}'}")
    private boolean createOptimistic;

    private static boolean luceneFallbackEnabled;

    @Value(value = "#{'${repo.luceneFallback.enabled}'}")
    public void setLuceneFallbackEnabled(boolean luceneFallbackEnabled){
        AlfrescoSourceSink.luceneFallbackEnabled = luceneFallbackEnabled;
    }

    private static ThreadLocal<RepositoryAccessSession> ras = new ThreadLocal<RepositoryAccessSession>();

	private static final Logger logger = LoggerFactory.getLogger(AlfrescoSourceSink.class);

    @Override
	public List<File> list(final ConfiguredSourceSink sourceConfig,
			final String path, final boolean recursive) {
		return null;
	}

	@Override
	public void send(final ConfiguredSourceSink configuredSourceSink,
			final WriteOption docExistsMode,
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
	public List<UploadResult> sendBatch(final ConfiguredSourceSink configuredSourceSink,
			final WriteOption docExistsMode, final List<Document> documents) {
		List<UploadResult> results = null;
		try {
			final RepositoryAccessSession ras = createRepositoryAccessSession(configuredSourceSink);
			try {
				logger.debug("Uploading " + documents.size() + " files");
				results = uploadBatch(docExistsMode, ras, documents);
				if(results.size() != documents.size())
					throw new RuntimeException("Upload results do not match required number of documents, results=" + results.size() + " and documents=" + documents.size());
					
			} catch (final RepositoryAccessException e) {
				if (!(e.getMessage() == null)
						&& (e.getMessage()
								.indexOf("security processing failed") != -1)) {
					results = retryBatch(configuredSourceSink, docExistsMode, documents);
				} else {
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				}
			} catch (final RuntimeException e) {
				if ("Error writing content to repository server".equals(e
						.getMessage())) {
					results = retryBatch(configuredSourceSink, docExistsMode, documents);
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
		return results;
	}

	private List<UploadResult> uploadBatch(final WriteOption docExistsMode,
		final RepositoryAccessSession ras, final List<Document> documents)
		throws RepositoryAccessException, RepositoryException {
		
		boolean overwrite = WriteOption.OVERWRITE == docExistsMode;
		// if overwrite=true, try directly pessimistic upload, which has a small performance penalty due to checks in the repository
		// if overwrite=false, try optimistic upload, which falls back to pessimistic in case of duplicates; in this mode, documents are uploaded twice
		boolean acceptDuplicate = WriteOption.SKIPANDIGNORE == docExistsMode;
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        if(overwrite) {
            return ras.storeDocsAndCreateParentSpaces(documents, overwrite, overwriteOptimistic);
        }
		else
			return ras.storeDocsAndCreateParentSpaces(documents, overwrite, createOptimistic, acceptDuplicate);
	}

	private List<UploadResult> retryBatch(final ConfiguredSourceSink configuredSourceSink,
			final WriteOption docExistsMode, final List<Document> documents)
			throws RepositoryAccessException, RepositoryException {
		logger.debug("Authentication failure? Creating new RAS");
		destroyRepositoryAccessSession();
		final RepositoryAccessSession ras = createRepositoryAccessSession(configuredSourceSink);

		logger.debug("Retrying batch");
		return uploadBatch(docExistsMode, ras, documents);
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
			final WriteOption docExistsMode,
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

	private static void uploadFile(final WriteOption docExistsMode,
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
				if (WriteOption.SKIPANDIGNORE == docExistsMode) {
					// ignore
				} else if (WriteOption.SKIPANDREPORTFAILED == docExistsMode) {
					logger.warn("Document " + document.getName()
							+ " already exists in " + remotePath);
					throw new Move2AlfException("Document "
							+ document.getName() + " already exists in "
							+ remotePath);
				} else if (WriteOption.OVERWRITE == docExistsMode) {
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
		
		return new RepositoryOperation<Boolean>() {

			@Override
			protected Boolean executeImpl(RepositoryAccessSession ras) throws RepositoryAccessException {
				return ras.doesDocExist(name, remotePath, false);
			}
		}.execute(sinkConfig);

	}

	@Override
	public void delete(final ConfiguredSourceSink sinkConfig,
			final String remotePath, final String name, final DeleteOption option) {
		new RepositoryOperation<Object>() { 

			@Override
			protected Object executeImpl(RepositoryAccessSession ras) throws RepositoryAccessException, RepositoryException {
				try {
					ras.deleteByDocNameAndSpace(remotePath, name);
				} catch (DocumentNotFoundException e) {
					if(DeleteOption.SKIPANDREPORTFAILED == option){
						throw new Move2AlfException(e);
					}
				}
				return null;
			}
		}.execute(sinkConfig);
	}
	
	@Override
	public boolean fileNameExists(final ConfiguredSourceSink sinkConfig, final String name) {
		return new RepositoryOperation<Boolean>(){

			@Override
			protected Boolean executeImpl(RepositoryAccessSession ras)
					throws RepositoryAccessException, RepositoryException {
				return ras.doesFileNameExists(name);
			}
			
		}.execute(sinkConfig);
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
						password, AlfrescoSourceSink.luceneFallbackEnabled);
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

	private abstract class RepositoryOperation<T>{
		
		protected abstract T executeImpl(RepositoryAccessSession ras) throws RepositoryAccessException, RepositoryException;
		
		public T execute(final ConfiguredSourceSink sinkConfig){
			try {
				try {
					final RepositoryAccessSession ras = createRepositoryAccessSession(sinkConfig);
					return executeImpl(ras);
				} catch (final RepositoryAccessException e) {
					if (!(e.getMessage() == null)
							&& (e.getMessage().indexOf(
									"security processing failed") != -1)) {
						// retry
						destroyRepositoryAccessSession();
						final RepositoryAccessSession ras = createRepositoryAccessSession(sinkConfig);
						try {
							return executeImpl(ras);
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
			} catch (RepositoryException e) {
	            logger.error(e.getMessage(), e);
	            throw new Move2AlfException(e.getMessage(), e);
	
			}
		}
	}
}
