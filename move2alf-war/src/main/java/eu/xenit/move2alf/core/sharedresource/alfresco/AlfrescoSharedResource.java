package eu.xenit.move2alf.core.sharedresource.alfresco;

import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.sharedresource.SharedResource;
import eu.xenit.move2alf.repository.*;
import eu.xenit.move2alf.repository.alfresco.ws.Document;
import eu.xenit.move2alf.repository.alfresco.ws.WebServiceRepositoryAccess;
import org.alfresco.webservice.util.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class AlfrescoSharedResource extends SharedResource {
	
	public static final String PARAM_URL = "url";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_USER = "user";

    @Value(value = "#{'${repo.overwrite.optimistic}'}")
    private boolean overwriteOptimistic;

    @Value(value = "#{'${repo.create.optimistic}'}")
    private boolean createOptimistic;

    private static boolean luceneFallbackEnabled;

    @Value(value = "#{'${repo.luceneFallback.enabled}'}")
    public void setLuceneFallbackEnabled(boolean luceneFallbackEnabled){
        AlfrescoSharedResource.luceneFallbackEnabled = luceneFallbackEnabled;
    }

    private ThreadLocal<RepositoryAccessSession> ras = new ThreadLocal<RepositoryAccessSession>();

	private static final Logger logger = LoggerFactory.getLogger(AlfrescoSharedResource.class);

	public void send(final WriteOption docExistsMode,
			final String remotePath, final String mimeType,
			final String namespace, final String contentType,
			final String description, final Map<String, String> metadata,
			final Map<String, String> multiValueMetadata,
			final File document,
            final String name) throws IllegalDocumentException {
		try {
			try {
				logger.debug("Uploading file " + document.getName() + " to remotePath " + remotePath);
				uploadFile(docExistsMode, remotePath, mimeType,
						namespace, contentType, description, metadata,
						multiValueMetadata, document, name);
			} catch (final RepositoryAccessException e) {
				if (!(e.getMessage() == null)
						&& (e.getMessage()
								.indexOf("security processing failed") != -1)) {
					retryUpload(docExistsMode,
							remotePath, mimeType, namespace, contentType,
							description, metadata, multiValueMetadata, document, name);
				} else {
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				}
			} catch (final RuntimeException e) {
				if ("Error writing content to repository server".equals(e
						.getMessage())) {
					retryUpload(docExistsMode,
							remotePath, mimeType, namespace, contentType,
							description, metadata, multiValueMetadata, document, name);
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

    public String putContent(File file, String mimeType){
        return createRepositoryAccessSession().putContent(file, mimeType);
    }

	public List<UploadResult> sendBatch(
			final WriteOption docExistsMode, final List<Document> documents) {
		List<UploadResult> results;
		try {
			final RepositoryAccessSession ras = createRepositoryAccessSession();
			try {
				logger.debug("Uploading " + documents.size() + " files");
				results = uploadBatch(docExistsMode, ras, documents);
				if(results.size() != documents.size())
					throw new RuntimeException("Upload results do not match required number of documents, results=" + results.size() + " and documents=" + documents.size());
					
			} catch (final RepositoryAccessException e) {
				if (!(e.getMessage() == null)
						&& (e.getMessage()
								.indexOf("security processing failed") != -1)) {
					results = retryBatch(docExistsMode, documents);
				} else {
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				}
			} catch (final RuntimeException e) {
				if ("Error writing content to repository server".equals(e
						.getMessage())) {
					results = retryBatch(docExistsMode, documents);
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
        if(overwrite) {
            return ras.storeDocsAndCreateParentSpaces(documents, overwrite, overwriteOptimistic);
        }
		else
			return ras.storeDocsAndCreateParentSpaces(documents, overwrite, createOptimistic, acceptDuplicate);
	}

	private List<UploadResult> retryBatch(final WriteOption docExistsMode, final List<Document> documents)
			throws RepositoryAccessException, RepositoryException {
		logger.debug("Authentication failure? Creating new RAS");
		destroyRepositoryAccessSession();
		final RepositoryAccessSession ras = createRepositoryAccessSession();

		logger.debug("Retrying batch");
		return uploadBatch(docExistsMode, ras, documents);
	}

	public void setACL(final ACL acl) {
		if (acl != null && acl.acls != null) {
			final RepositoryAccessSession ras = createRepositoryAccessSession();
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

	private void retryUpload(
			final WriteOption docExistsMode,
			final String remotePath, final String mimeType,
			final String namespace, final String contentType,
			final String description, final Map<String, String> metadata,
			final Map<String, String> multiValueMetadata,
			final File document,
            final String name)
			throws RepositoryAccessException, RepositoryException, IllegalDocumentException {
		logger.debug("Authentication failure? Creating new RAS");
		destroyRepositoryAccessSession();

		logger.debug("Retrying file " + document.getName());
		uploadFile(docExistsMode, remotePath, mimeType,
				namespace, contentType, description, metadata,
				multiValueMetadata, document, name);
	}

	private void uploadFile(final WriteOption docExistsMode,
			final String remotePath, final String mimeType,
			final String namespace, final String contentType,
			final String description, final Map<String, String> metadata,
			final Map<String, String> multiValueMetadata,
			final File document,
            final String name)
			throws RepositoryAccessException, RepositoryException, IllegalDocumentException {
		try {
			createRepositoryAccessSession().storeDocAndCreateParentSpaces(document, name, mimeType, remotePath,
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
					createRepositoryAccessSession().updateContentByDocNameAndPath(remotePath,
                            document.getName(), document, mimeType, false);
					if (metadata != null) {
						createRepositoryAccessSession().updateMetaDataByDocNameAndPath(remotePath,
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

	public boolean exists(final String remotePath, final String name) {
		
		return new RepositoryOperation<Boolean>() {

			@Override
			protected Boolean executeImpl(RepositoryAccessSession ras) throws RepositoryAccessException {
				return ras.doesDocExist(name, remotePath, false);
			}
		}.execute();

	}

	public void delete(final String remotePath, final String name, final DeleteOption option) {
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
		}.execute();
	}
	
	public boolean fileNameExists(final String name) {
		return new RepositoryOperation<Boolean>(){

			@Override
			protected Boolean executeImpl(RepositoryAccessSession ras)
					throws RepositoryAccessException, RepositoryException {
				return ras.doesFileNameExists(name);
			}
			
		}.execute();
	}

	public void clearCaches() {
		RepositoryAccessSession ras = createRepositoryAccessSession();
		if(ras != null) {
			ras.clearCaches();
		} else {
			logger.warn("Tried to clear caches of inexistent RepositoryAccessSession");
		}
	}

    private String user;
    public void setUser(String user) {
        this.user = user;
    }

    private String password;
    private String url;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        if (url.endsWith("/")) {
            this.url = url + "api/";
        } else {
            this.url = url + "/api/";
        }
    }

    private RepositoryAccessSession createRepositoryAccessSession() {
		// RepositoryAccessSession ras;
		if (ras.get() == null) {
			logger.debug("Creating new RepositoryAccessSession for thread "
					+ Thread.currentThread());
			WebServiceRepositoryAccess ra = null;
			try {
				ra = new WebServiceRepositoryAccess(new URL(url), user,
						password, AlfrescoSharedResource.luceneFallbackEnabled);
			} catch (final MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ras.set(ra.createSessionAndRetry());
		} else {
			logger.debug("Reusing existing RepositoryAccessSession in thread "
					+ Thread.currentThread());
		}
		return ras.get();
	}

	private void destroyRepositoryAccessSession() {
		ras.get().closeSession();
        ras.remove();
	}

	public String getCategory() {
		return ConfigurableObject.CAT_DESTINATION;
	}

	public String getDescription() {
		return "Alfresco using SOAP web services";
	}

	public String getName() {
		return "Alfresco";
	}

    public boolean validate() {
        RepositoryAccessSession ras = createRepositoryAccessSession();
        return (ras!= null);
    }

	private abstract class RepositoryOperation<T>{
		
		protected abstract T executeImpl(RepositoryAccessSession ras) throws RepositoryAccessException, RepositoryException;
		
		public T execute(){
			try {
				try {
					final RepositoryAccessSession ras = createRepositoryAccessSession();
					return executeImpl(ras);
				} catch (final RepositoryAccessException e) {
					if (!(e.getMessage() == null)
							&& (e.getMessage().indexOf(
									"security processing failed") != -1)) {
						// retry
						destroyRepositoryAccessSession();
						final RepositoryAccessSession ras = createRepositoryAccessSession();
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
