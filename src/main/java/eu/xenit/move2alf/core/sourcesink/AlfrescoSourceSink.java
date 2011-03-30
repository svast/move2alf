package eu.xenit.move2alf.core.sourcesink;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.alfresco.webservice.util.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		WebServiceRepositoryAccess ra = createRepositoryAccess(configuredSourceSink);
		try {
			RepositoryAccessSession ras = ra.createSessionAndRetry();
			// run(ras);
			String basePath = getParameterWithDefault(parameterMap, "path", "/");
			if (!basePath.endsWith("/")) {
				basePath = basePath + "/";
			}
			
			if(!basePath.startsWith("/")) {
				basePath = "/" + basePath;
			}

			String relativePath = getParameterWithDefault(parameterMap,
					"relativePath", "");
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
				} else if (component.startsWith("cm:")) {
					remotePath += component + "/";
				} else {
					remotePath += "cm:" + component + "/";
				}
			}
			remotePath = remotePath.substring(0, remotePath.length() - 1);

			logger.debug("Writing to " + remotePath);

			String mimeType = getParameterWithDefault(parameterMap, "mimetype",
					"text/plain");
			String namespace = getParameterWithDefault(parameterMap,
					"namespace", "{http://www.alfresco.org/model/content/1.0}");
			String contentType = getParameterWithDefault(parameterMap,
					"contenttype", "content");

			String description = getParameterWithDefault(parameterMap,
					"description", "");

			Map<String, String> metadata = (Map<String, String>) parameterMap
					.get("metadata");
			Map<String, String> multiValueMetadata = (Map<String, String>) parameterMap
					.get("multiValueMetadata");

			File document = (File) parameterMap.get("file");

			if (!ras.doesDocExist(document.getName(), remotePath)) {
				ras.storeDocAndCreateParentSpaces(document, mimeType,
						remotePath, description, namespace, contentType, metadata, // TODO:
																			// description
						multiValueMetadata);
				parameterMap.put("status", "ok");
			} else {
				if (MODE_SKIP.equals(docExistsMode)) {
					// ignore
					parameterMap.put("status", "ok");
				} else if (MODE_SKIP_AND_LOG.equals(docExistsMode)) {
					logger.warn("Document " + document.getName()
							+ " already exists in " + remotePath);
					parameterMap.put("status", "failed");
					parameterMap.put("errormessage", "Document "
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
					}
					parameterMap.put("status", "ok");
				}
			}

		} catch (RepositoryAccessException e) {
			// we end up here if there is a communication error during a session
			parameterMap.put("status", "failed");
			parameterMap.put("errormessage", e.getMessage());
			logger.error(e.getMessage(), e);
		} catch (RepositoryException e) {
			// we end up here if the request could not be handled by the
			// repository
			parameterMap.put("status", "failed");
			parameterMap.put("errormessage", e.getMessage());
			logger.error(e.getMessage(), e);
		} catch (WebServiceException e) {
			parameterMap.put("status", "failed");
			parameterMap.put("errormessage", e.getMessage());
			logger.error(e.getMessage(), e);
		} catch (RepositoryFatalException e) {
			logger.error("Fatal Exception", e);
			System.exit(1);
		}
	}

	@Override
	public boolean exists(ConfiguredSourceSink sinkConfig, String remotePath,
			String name) {
		WebServiceRepositoryAccess ra = createRepositoryAccess(sinkConfig);

		RepositoryAccessSession ras = ra.createSessionAndRetry();
		try {
			return ras.doesDocExist(name, remotePath);
		} catch (RepositoryAccessException e) {
			throw new Move2AlfException(e.getMessage());
		}
	}

	private WebServiceRepositoryAccess createRepositoryAccess(
			ConfiguredSourceSink sinkConfig) {
		String user = sinkConfig.getParameter("user");
		String password = sinkConfig.getParameter("password");
		String url = sinkConfig.getParameter("url");
		if (url.endsWith("/")) {
			url = url + "api/";
		} else {
			url = url + "/api/";
		}
		WebServiceRepositoryAccess ra = null;
		try {
			ra = new WebServiceRepositoryAccess(new URL(url), user, password);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return ra;
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
