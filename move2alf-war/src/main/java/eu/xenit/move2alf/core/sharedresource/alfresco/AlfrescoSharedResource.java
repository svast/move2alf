package eu.xenit.move2alf.core.sharedresource.alfresco;

import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.repository.*;
import eu.xenit.move2alf.repository.alfresco.ws.Document;
import eu.xenit.move2alf.repository.alfresco.ws.WebServiceRepositoryAccessFactory;
import org.alfresco.webservice.util.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

/**
 * This is the main endpoint for communication to alfresco
 * See AlfrescoResourceAction
 */
public class AlfrescoSharedResource extends AbstractAlfrescoSharedResource {

	@Value(value = "#{'${repo.overwrite.optimistic}'}")
    private boolean overwriteOptimistic;

    @Value(value = "#{'${repo.create.optimistic}'}")
    private boolean createOptimistic;

    private static boolean luceneFallbackEnabled;

    @Value(value = "#{'${repo.luceneFallback.enabled}'}")
    public void setLuceneFallbackEnabled(boolean luceneFallbackEnabled){
        AlfrescoSharedResource.luceneFallbackEnabled = luceneFallbackEnabled;
    }

	private RepositoryAccessFactory repositoryAccessFactory;

	public AlfrescoSharedResource(){
		super();
		this.repositoryAccessFactory = null;
	}

	public AlfrescoSharedResource(RepositoryAccessFactory repositoryAccessFactory){
		super();
		this.repositoryAccessFactory = repositoryAccessFactory;
	}

    private ThreadLocal<RepositoryAccessSession> ras = new ThreadLocal<RepositoryAccessSession>();

	private static final Logger logger = LoggerFactory.getLogger(AlfrescoSharedResource.class);

	public static final String PUT_CONTENT_401_MESSAGE = "Content could not be uploaded because invalid credentials have been supplied.";

    @Override
	public String putContent(File file, String mimeType){
		try {
			return createRepositoryAccessSession().putContent(file, mimeType);
		} catch (RuntimeException e){
			if (e.getMessage().equals(PUT_CONTENT_401_MESSAGE)){
				logger.debug("401 message on put content, ticket could be expired, retrying");
				destroyRepositoryAccessSession();
				return createRepositoryAccessSession().putContent(file, mimeType);
			} else {
				throw e;
			}
		}

    }

	@Override
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

	@Override
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

	@Override
	public boolean exists(final String remotePath, final String name) {
		
		return new RepositoryOperation<Boolean>() {
		    
			@Override
			protected Boolean executeImpl(RepositoryAccessSession ras) throws RepositoryAccessException {
				return ras.doesDocExist(name, remotePath, false);
			}
		}.execute();

	}

	@Override
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
	
	@Override
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

	private RepositoryAccessFactory getRepositoryAccessFactory() throws MalformedURLException {
		if(repositoryAccessFactory == null){
			repositoryAccessFactory = new WebServiceRepositoryAccessFactory(url, user, password, AlfrescoSharedResource.luceneFallbackEnabled);
		}
		return repositoryAccessFactory;
	}


    private RepositoryAccessSession createRepositoryAccessSession() {
		// RepositoryAccessSession ras;
		if (ras.get() == null) {
			logger.debug("Creating new RepositoryAccessSession for thread "
					+ Thread.currentThread());
			RepositoryAccess ra = null;
			try {
				ra = getRepositoryAccessFactory().createRepositoryAccess();
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

	@Override
	public String getDescription() {
		return "Alfresco using SOAP web services";
	}

	@Override
	public String getName() {
		return "Alfresco";
	}

    @Override
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
