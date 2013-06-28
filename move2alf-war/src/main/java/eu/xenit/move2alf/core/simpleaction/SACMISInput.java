package eu.xenit.move2alf.core.simpleaction;

import static eu.xenit.move2alf.common.Parameters.*;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.core.action.Move2AlfStartAction;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.logic.PipelineAssemblerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultConsumerTemplate;
import org.apache.chemistry.opencmis.commons.impl.jaxb.CmisExtensionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * SACMISInput
 *
 * @author Jonas Heylen
 */
public class SACMISInput extends Move2AlfStartAction {

	private static final Logger logger = LoggerFactory.getLogger(SACMISInput.class);

	private static final String DIRECT_ENDPOINT = "direct:input";
	public static final String PARAM_CMIS_URL = "cmisUrl";
	public static final String PARAM_CMIS_USERNAME = "cmisUsername";
	public static final String PARAM_CMIS_PASSWORD = "cmisPassword";

	public static final String PARAM_CAMEL_HEADER = "camelHeader";

	private String cmisURL;
	private String cmisUsername;
	private String cmisPassword;

	public void setCmisUrl(String cmisURL) {
		this.cmisURL = cmisURL;
	}

	public void setCmisUsername(final String cmisUsername) {
		this.cmisUsername = cmisUsername;
	}

	public void setCmisPassword(final String cmisPassword) {
		this.cmisPassword = cmisPassword;
	}

	public String getEndpoint() {
		return String.format("cmis://%s?username=%s&password=%s&readContent=true", cmisURL, cmisUsername, cmisPassword);
	}

	@Override
	protected void onStartImpl() {
		final CamelContext camel = new DefaultCamelContext();
		try {
			camel.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from(getEndpoint())//.streamCaching()
							.to(DIRECT_ENDPOINT);
				}
			});
			camel.start();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Move2AlfException("Camel exception", e);
		}

		final ConsumerTemplate template = new DefaultConsumerTemplate(camel);
		try {
			template.start();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Move2AlfException("Camel exception", e);
		}

		// Create temporary folder
		final File tempFolder = Files.createTempDir();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Using temporary folder: %s", tempFolder.toString()));
		}

		boolean done = false;
		boolean firstLoop = true;
		String first = null;
		while (!done) {
			final Exchange exchange = template.receive(DIRECT_ENDPOINT);

			// CMIS specific
			final String folderPath = (String) exchange.getIn().getHeader(CamelCMISConstants.CMIS_FOLDER_PATH);
			final String cmisName = (String) exchange.getIn().getHeader("cmis:name");
			final String cmisPath = (String) exchange.getIn().getHeader("cmis:path");
			logger.debug(String.format("folderPath: %s, cmisName: %s, cmisPath: %s", folderPath, cmisName, cmisPath));

			// log all headers
			for (Map.Entry<String, Object> header : exchange.getIn().getHeaders().entrySet()) {
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Message ID: '%s' - Header name: '%s' value: '%s'",
							exchange.getIn().getMessageId(),
							header.getKey(),
							(header.getValue() != null) ? header.getValue().toString() : "null"));
				}
			}

			final boolean isFile = (cmisPath == null);
			final String path = isFile ? folderPath + "/" + cmisName : cmisPath;

			if (first == null) {
				logger.debug("First file encountered: " + path);
				first = path;
			}

			if (isFile) {
				boolean failed = false;

				logger.debug("This is a file, writing to filesystem");
				final InputStream in = exchange.getIn().getBody(InputStream.class);
				final File file = new File(tempFolder, cmisName);
				try {
					logger.debug("Stream: " + in);
					ByteStreams.copy(in, new FileOutputStream(file));
					in.close();
				} catch (FileNotFoundException e) {
					// file is actually a folder. should never happen
					throw new Move2AlfException(e);
				} catch (IOException e) {
					logger.error(String.format("'%s' failed", cmisName), e);
					Set<ProcessedDocumentParameter> params = new HashSet<ProcessedDocumentParameter>();
					ProcessedDocumentParameter parameter = new ProcessedDocumentParameter();
					parameter.setName(Parameters.PARAM_ERROR_MESSAGE);
					parameter.setValue(e.toString());
					params.add(parameter);
					ReportMessage reportMessage = new ReportMessage(file.getName(),
							new Date(),
							Parameters.VALUE_FAILED,
							params,
							null);
					sendMessage(PipelineAssemblerImpl.REPORTER, reportMessage);
					failed = true;
				}

				if (!failed) {
					if (logger.isDebugEnabled()) {
						List<CmisExtensionType> extensions = (List<CmisExtensionType>) exchange.getIn().getHeader(CamelCMISConstants.CAMEL_CMIS_EXTENSIONS);
						if (extensions != null) {
							logger.debug("EXTENSIONS: " + extensions);
						}
						Object acl = exchange.getIn().getHeader(CamelCMISConstants.CAMEL_CMIS_ACL);
						if (acl != null) {
							logger.debug("ACL: " + acl);
						}
					}

					final FileInfo fileInfo = new FileInfo();
					fileInfo.put(PARAM_RELATIVE_PATH, folderPath);
					fileInfo.put(PARAM_FILE, file);
					fileInfo.put(PARAM_CAMEL_HEADER, exchange.getIn().getHeaders());
					sendMessage(fileInfo);
				}
			}

			if (path.equals(first) && !firstLoop) {
				done = true;
			}
			firstLoop = false;
		}

		try {
			camel.stop();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Move2AlfException("Camel exception", e);
		}
	}
}
