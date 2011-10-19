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

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.repository.RepositoryAccessException;
import eu.xenit.move2alf.repository.RepositoryAccessSession;
import eu.xenit.move2alf.repository.RepositoryException;
import eu.xenit.move2alf.repository.RepositoryFatalException;
import eu.xenit.move2alf.repository.alfresco.ws.WebServiceRepositoryAccess;

public class AlfrescoSourceSink extends SourceSink {

	private static final String PARAM_URL = "url";
	private static final String PARAM_PASSWORD = "password";
	private static final String PARAM_USER = "user";

	private static ThreadLocal<RepositoryAccessSession> ras = new ThreadLocal<RepositoryAccessSession>();

	private static final Logger logger = LoggerFactory
			.getLogger(AlfrescoSourceSink.class);

	@Override
	public List<File> list(ConfiguredSourceSink sourceConfig, String path,
			boolean recursive) {
		return null;
	}

	@Override
	public void send(ConfiguredSourceSink configuredSourceSink,
			Map<String, Object> parameterMap, String path, String docExistsMode) {
		// TODO: refactoring needed: a SourceSink shouldn't know about the
		// parameterMap and setting the status, this should be done by the
		// appropriate action, in this case SinkAction. The current code causes
		// a lot of duplication when implementing a new SourceSink.

		try {
			RepositoryAccessSession ras = createRepositoryAccessSession(configuredSourceSink);
			// run(ras);
			String basePath = (path == null) ? "/" : path;
			if (!basePath.endsWith("/")) {
				basePath = basePath + "/";
			}

			if (!basePath.startsWith("/")) {
				basePath = "/" + basePath;
			}

			String relativePath = getParameterWithDefault(parameterMap,
					Parameters.PARAM_RELATIVE_PATH, "");
			relativePath = relativePath.replace("\\", "/");

			if (relativePath.startsWith("/")) {
				relativePath = relativePath.substring(1);
			}

			// add "cm:" in front of each path component
			String remotePath = basePath + relativePath;
			String[] components = remotePath.split("/");
			remotePath = "";
			for (String component : components) {
				if ("".equals(component)) {
					remotePath += "/";
				} else if (component.contains(":")) {
					remotePath += component + "/";
				} else {
					remotePath += "cm:" + component + "/";
				}
			}
			remotePath = remotePath.substring(0, remotePath.length() - 1);

			logger.debug("Writing to " + remotePath);

			String mimeType = getParameterWithDefault(parameterMap,
					Parameters.PARAM_MIMETYPE, "text/plain");
			String namespace = getParameterWithDefault(parameterMap,
					Parameters.PARAM_NAMESPACE,
					"{http://www.alfresco.org/model/content/1.0}");
			String contentType = getParameterWithDefault(parameterMap,
					Parameters.PARAM_CONTENTTYPE, "content");

			String description = getParameterWithDefault(parameterMap,
					Parameters.PARAM_DESCRIPTION, "");

			Map<String, String> metadata = (Map<String, String>) parameterMap
					.get(Parameters.PARAM_METADATA);
			Map<String, String> multiValueMetadata = (Map<String, String>) parameterMap
					.get(Parameters.PARAM_MULTI_VALUE_METADATA);

			Map<String, Map<String, String>> acl = (Map<String, Map<String, String>>) parameterMap
					.get(Parameters.PARAM_ACL);

			boolean inheritPermissions;
			if (parameterMap.get(Parameters.PARAM_INHERIT_PERMISSIONS) == null) {
				inheritPermissions = false;
			} else {
				inheritPermissions = (Boolean) parameterMap
						.get(Parameters.PARAM_INHERIT_PERMISSIONS);
			}

			File document = (File) parameterMap.get(Parameters.PARAM_FILE);

			try {
				logger.debug("Uploading file " + document.getName());
				uploadFile(docExistsMode, ras, basePath, remotePath, mimeType,
						namespace, contentType, description, metadata,
						multiValueMetadata, acl, inheritPermissions, document);
			} catch (RuntimeException e) {
				if ("Error writing content to repository server".equals(e
						.getMessage())) {
					// retry
					logger.debug("Authentication failure? Creating new RAS");
					destroyRepositoryAccessSession();
					ras = createRepositoryAccessSession(configuredSourceSink);
					try {
						logger.debug("Retrying file " + document.getName());
						uploadFile(docExistsMode, ras, basePath, remotePath,
								mimeType, namespace, contentType, description,
								metadata, multiValueMetadata, acl,
								inheritPermissions, document);
					} catch (RuntimeException e2) {
						logger.error(e2.getMessage(), e2);
						throw new Move2AlfException(e2.getMessage(), e2);
					}
				} else {
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				}
			}

		} catch (RepositoryAccessException e) {
			// we end up here if there is a communication error during a session
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		} catch (RepositoryException e) {
			// we end up here if the request could not be handled by the
			// repository
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		} catch (WebServiceException e) {
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		} catch (RepositoryFatalException e) {
			logger.error("Fatal Exception", e);
			// TODO: stop job instead of stopping tomcat
			// System.exit(1);
		}
	}

	private void uploadFile(final String docExistsMode, final RepositoryAccessSession ras,
			final String basePath, final String remotePath, final String mimeType,
			final String namespace, final String contentType, final String description,
			final Map<String, String> metadata,
			final Map<String, String> multiValueMetadata,
			final Map<String, Map<String, String>> acl, final boolean inheritPermissions,
			final File document) throws RepositoryAccessException,
			RepositoryException {
		try {
			ras.storeDocAndCreateParentSpaces(document, mimeType, remotePath,
					description, namespace, contentType, metadata,
					multiValueMetadata);
			if (acl != null) {
				for (String aclPath : acl.keySet()) {

					String parserAclPath = aclPath;
					// add "cm:" in front of each path component
					if (parserAclPath.startsWith("/")) {
						parserAclPath = parserAclPath.substring(1,
								parserAclPath.length());
					}
					if (parserAclPath.endsWith("/")) {
						parserAclPath = parserAclPath.substring(0,
								parserAclPath.length() - 1);
					}

					String remoteACLPath = basePath + parserAclPath;
					String[] aclComponents = remoteACLPath.split("/");
					remoteACLPath = "";
					for (String aclComponent : aclComponents) {
						if ("".equals(aclComponent)) {
							remoteACLPath += "/";
						} else if (aclComponent.contains(":")) {
							remoteACLPath += aclComponent + "/";
						} else {
							remoteACLPath += "cm:" + aclComponent + "/";
						}
					}
					remoteACLPath = remoteACLPath.substring(0, remoteACLPath
							.length() - 1);

					logger.debug("ACL path: " + remoteACLPath);

					ras.setAccessControlList(remoteACLPath, inheritPermissions,
							acl.get(aclPath));
				}
			}
		} catch (RepositoryException e) {
			Throwable cause = e.getCause();
			if (cause == null) {
				cause = e;
			}
			logger.info("Message {}", cause.getMessage());
			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			cause.printStackTrace(printWriter);
			String stackTrace = result.toString();
			//logger.debug("Stacktrace {}", stackTrace);
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
					ras.updateContentByDocNameAndPath(remotePath, document
							.getName(), document, mimeType, false);
					if (metadata != null) {
						ras.updateMetaDataByDocNameAndPath(remotePath, document
								.getName(), metadata);
						// TODO: updating multivalue metadata not
						// supported
						// by
						// RRA?
					}
				}
			} else {
				throw e;
			}
		}
	}

	@Override
	public boolean exists(ConfiguredSourceSink sinkConfig, String remotePath,
			String name) {
		try {
			try {
				RepositoryAccessSession ras = createRepositoryAccessSession(sinkConfig);
				return ras.doesDocExist(name, remotePath);
			} catch (RepositoryAccessException e) {
				if (!(e.getMessage() == null) && (e.getMessage().indexOf("security processing failed") != -1)) { 
					// retry
					destroyRepositoryAccessSession();
					RepositoryAccessSession ras = createRepositoryAccessSession(sinkConfig);
					try {
						return ras.doesDocExist(name, remotePath);
					} catch (RepositoryAccessException e2) {
						logger.error(e2.getMessage(), e2);
						throw new Move2AlfException(e2.getMessage(), e2);
					}
				} else {
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				}
			}
		} catch (RuntimeException e) {
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		}
	}

	@Override
	public void delete(ConfiguredSourceSink sinkConfig, String remotePath,
			String name) {
		try {
			try {
				RepositoryAccessSession ras = createRepositoryAccessSession(sinkConfig);
				ras.deleteByDocNameAndSpace(remotePath, name);
			} catch (RepositoryAccessException e) {
				if (!(e.getMessage() == null) && (e.getMessage().indexOf("security processing failed") != -1)) { 
					// retry
					destroyRepositoryAccessSession();
					RepositoryAccessSession ras = createRepositoryAccessSession(sinkConfig);
					try {
						ras.deleteByDocNameAndSpace(remotePath, name);
					} catch (RepositoryAccessException e2) {
						logger.error(e.getMessage(), e);
						throw new Move2AlfException(e.getMessage(), e);
					}
				} else {
					logger.error(e.getMessage(), e);
					throw new Move2AlfException(e.getMessage(), e);
				}
			}
		} catch (RepositoryException e) {
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		} catch (RuntimeException e) {
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		}
	}

	private RepositoryAccessSession createRepositoryAccessSession(
			ConfiguredSourceSink sinkConfig) {
		// RepositoryAccessSession ras;
		RepositoryAccessSession ras = AlfrescoSourceSink.ras.get();
		if (ras == null) {
			logger.debug("Creating new RepositoryAccessSession for thread "
					+ Thread.currentThread());
			String user = sinkConfig.getParameter(PARAM_USER);
			String password = sinkConfig.getParameter(PARAM_PASSWORD);
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
			} catch (MalformedURLException e1) {
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

	private void destroyRepositoryAccessSession() {
		AlfrescoSourceSink.ras.get().closeSession();
		AlfrescoSourceSink.ras.remove();
	}

	private String getParameterWithDefault(Map<String, Object> parameterMap,
			String parameter, String defaultValue) {
		String value = (String) parameterMap.get(parameter);
		value = (value != null) ? value : defaultValue;
		return value;
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
