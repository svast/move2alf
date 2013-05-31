package eu.xenit.move2alf.core.simpleaction;

import static eu.xenit.move2alf.common.Parameters.*;

import com.google.common.io.ByteStreams;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.action.Move2AlfStartAction;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultConsumerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * SACMISInput
 *
 * @author Jonas Heylen
 */
public class SACMISInput extends Move2AlfStartAction {

	private static final Logger logger = LoggerFactory.getLogger(SACMISInput.class);

	private static final String DIRECT_ENDPOINT = "direct:input";
	public static final String PARAM_CMIS_URL = "cmisUrl";

	private String cmisURL;

	public void setCmisURL(String cmisURL) {
		this.cmisURL = cmisURL;
	}

	public String getEndpoint() {
		return "cmis://" + cmisURL + "&readContent=true";
	}

	@Override
	protected void onStartImpl() {
		final CamelContext camel = new DefaultCamelContext();
		try {
			camel.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from(getEndpoint())
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

			final boolean isFile = (cmisPath == null);
			final String path = isFile ? folderPath + "/" + cmisName : cmisPath;

			if (first == null) {
				logger.debug("First file encountered: " + path);
				first = path;
			}

			if (isFile) {
				logger.debug("This is a file, writing to filesystem");
				final InputStream in = exchange.getIn().getBody(InputStream.class);
				logger.debug("Body: " + exchange.getIn().getBody());
				logger.debug("Inputstream: " + in);
				final File file = new File(cmisName);
				try {
					ByteStreams.copy(in, new FileOutputStream(file));
				} catch (FileNotFoundException e) {
					// file is actually a folder. should never happen
					throw new Move2AlfException(e);
				} catch (IOException e) {
					throw new Move2AlfException(e);
				}

				final FileInfo fileInfo = new FileInfo();
				fileInfo.put(PARAM_FILE, file);
				sendMessage(fileInfo);
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
