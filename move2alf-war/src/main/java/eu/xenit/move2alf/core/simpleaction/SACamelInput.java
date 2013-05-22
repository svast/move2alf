package eu.xenit.move2alf.core.simpleaction;

import static eu.xenit.move2alf.common.Parameters.*;

import com.google.common.io.ByteStreams;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SACamelInput
 *
 * @author Jonas Heylen
 */
public abstract class SACamelInput extends SimpleAction {

	private static final Logger logger = LoggerFactory.getLogger(SACamelInput.class);

	private static final String DIRECT_ENDPOINT = "direct:input";

	public abstract String getEndpoint();

	@Override
	public List<FileInfo> execute(final FileInfo parameterMap, final ActionConfig config,
								  final Map<String, Serializable> state) {
		final CamelContext camel = new DefaultCamelContext();
		try {
			camel.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from(getEndpoint())
							.streamCaching()  // TODO: nodig? content wordt maar 1 maal uitgelezen
							//.wireTap()
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

		final List<FileInfo> result = new ArrayList<FileInfo>();
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
				result.add(fileInfo);
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

		return result;
	}
}
