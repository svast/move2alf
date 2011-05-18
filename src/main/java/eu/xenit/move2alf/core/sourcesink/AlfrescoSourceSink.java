package eu.xenit.move2alf.core.sourcesink;

import java.io.File;
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
import eu.xenit.move2alf.core.action.SourceAction;
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
			Map<String, Object> parameterMap, String docExistsMode) {
		// TODO: refactoring needed: a SourceSink shouldn't know about the
		// parameterMap and setting the status, this should be done by the
		// appropriate action, in this case SinkAction. The current code causes
		// a lot of duplication when implementing a new SourceSink.

		try {
			RepositoryAccessSession ras = createRepositoryAccessSession(configuredSourceSink);
			// run(ras);
			String basePath = getParameterWithDefault(parameterMap,
					SourceAction.PARAM_PATH, "/");
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

			if (!ras.doesDocExist(document.getName(), remotePath)) {
				ras.storeDocAndCreateParentSpaces(document, mimeType,
						remotePath, description, namespace, contentType,
						metadata, multiValueMetadata);
				if (acl != null) {
					for (String aclPath : acl.keySet()) {
						ras.setAccessControlList(aclPath, inheritPermissions,
								acl.get(aclPath));
					}
				}
				parameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_OK);
			} else {
				if (MODE_SKIP.equals(docExistsMode)) {
					// ignore
					parameterMap.put(Parameters.PARAM_STATUS,
							Parameters.VALUE_OK);
				} else if (MODE_SKIP_AND_LOG.equals(docExistsMode)) {
					logger.warn("Document " + document.getName()
							+ " already exists in " + remotePath);
					parameterMap.put(Parameters.PARAM_STATUS,
							Parameters.VALUE_FAILED);
					parameterMap.put(Parameters.PARAM_ERROR_MESSAGE,
							"Document " + document.getName()
									+ " already exists in " + remotePath);
				} else if (MODE_OVERWRITE.equals(docExistsMode)) {
					logger.info("Overwriting document " + document.getName()
							+ " in " + remotePath);
					ras.updateContentByDocNameAndPath(remotePath, document
							.getName(), document, mimeType, false);
					if (metadata != null) {
						ras.updateMetaDataByDocNameAndPath(remotePath, document
								.getName(), metadata);
						// TODO: updating multivalue metadata not supported by
						// RRA?
					}
					parameterMap.put(Parameters.PARAM_STATUS,
							Parameters.VALUE_OK);
				}
			}

		} catch (RepositoryAccessException e) {
			// we end up here if there is a communication error during a session
			parameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_FAILED);
			parameterMap.put(Parameters.PARAM_ERROR_MESSAGE, e.getMessage());
			logger.error(e.getMessage(), e);
		} catch (RepositoryException e) {
			// we end up here if the request could not be handled by the
			// repository
			parameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_FAILED);
			parameterMap.put(Parameters.PARAM_ERROR_MESSAGE, e.getMessage());
			logger.error(e.getMessage(), e);
		} catch (WebServiceException e) {
			parameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_FAILED);
			parameterMap.put(Parameters.PARAM_ERROR_MESSAGE, e.getMessage());
			logger.error(e.getMessage(), e);
		} catch (RepositoryFatalException e) {
			logger.error("Fatal Exception", e);
			System.exit(1);
		} catch (RuntimeException e) {
			parameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_FAILED);
			parameterMap.put(Parameters.PARAM_ERROR_MESSAGE, e.getMessage());
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public boolean exists(ConfiguredSourceSink sinkConfig, String remotePath,
			String name) {
		try {
			RepositoryAccessSession ras = createRepositoryAccessSession(sinkConfig);
			return ras.doesDocExist(name, remotePath);
		} catch (RepositoryAccessException e) {
			throw new Move2AlfException(e.getMessage());
		} catch (RuntimeException e) {
			throw new Move2AlfException(e.getMessage());
		}
	}

	@Override
	public void delete(ConfiguredSourceSink sinkConfig, String remotePath,
			String name) {
		try {
			RepositoryAccessSession ras = createRepositoryAccessSession(sinkConfig);
			ras.deleteByDocNameAndSpace(remotePath, name);
		} catch (RepositoryAccessException e) {
			throw new Move2AlfException(e.getMessage());
		} catch (RepositoryException e) {
			throw new Move2AlfException(e.getMessage());
		} catch (RuntimeException e) {
			throw new Move2AlfException(e.getMessage());
		}
	}

	private RepositoryAccessSession createRepositoryAccessSession(
			ConfiguredSourceSink sinkConfig) {
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
