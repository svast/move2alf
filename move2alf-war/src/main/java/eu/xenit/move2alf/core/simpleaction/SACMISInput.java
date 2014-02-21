package eu.xenit.move2alf.core.simpleaction;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.logic.PipelineAssemblerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultConsumerTemplate;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static eu.xenit.move2alf.common.Parameters.*;

/**
 * SACMISInput
 *
 * @author Jonas Heylen
 */
public class SACMISInput extends Move2AlfReceivingAction<Object> {

    private static final Logger logger = LoggerFactory.getLogger(SACMISInput.class);

    private static final String DIRECT_ENDPOINT = "direct:input";
    public static final String PARAM_CMIS_URL = "cmisUrl";
    public static final String PARAM_CMIS_USERNAME = "cmisUsername";
    public static final String PARAM_CMIS_PASSWORD = "cmisPassword";
    public static final String PARAM_CMIS_QUERY = "cmisQuery";
    public static final String PARAM_SKIP_CONTENT_UPLOAD = "skipContentUpload";

    public static final String PARAM_CAMEL_HEADER = "camelHeader";

    private String cmisURL;
    private String cmisUsername;
    private String cmisPassword;
    private String cmisQuery;
    private Boolean skipContentUpload;

    public Boolean getSkipContentUpload() {
        return skipContentUpload;
    }

    public void setSkipContentUpload(String skipContentUpload) {
        this.skipContentUpload = Boolean.valueOf(skipContentUpload);
    }

    public String getCmisQuery() {
        return cmisQuery;
    }

    public void setCmisQuery(String cmisQuery) {
        this.cmisQuery = cmisQuery;
    }

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
        cmisURL = cmisURL.replace("?","QUESTIONMARK");  // Sharepoint endpoint contains parameters in the url; temporary hide them, otherwise problems parsing the cmis parameters
        if(cmisQuery.isEmpty())
            return String.format("cmis://%s?username=%s&password=%s&readContent=%s&objectFactoryClass=org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl", cmisURL, cmisUsername, cmisPassword,!getSkipContentUpload());
        else
            return String.format("cmis://%s?username=%s&password=%s&readContent=%s&objectFactoryClass=org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl&query=%s", cmisURL, cmisUsername, cmisPassword,!getSkipContentUpload(),cmisQuery);
    }

    @Override
    protected void executeImpl(Object message) {
        final CamelContext camel = new DefaultCamelContext();
        logger.debug("Endpoint=" + getEndpoint());
        RouteDefinition route = new RouteDefinition();
        route.from(getEndpoint());
        route.to(DIRECT_ENDPOINT);

        camel.getShutdownStrategy().setShutdownNowOnTimeout(true);
        camel.getShutdownStrategy().setTimeout(6000);

        try {
			/*camel.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from(getEndpoint()).to(DIRECT_ENDPOINT);
				}
			});*/
            ((ModelCamelContext)camel).addRouteDefinition(route);
            camel.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Move2AlfException("Camel exception", e);
        }

        final ConsumerTemplate template = new DefaultConsumerTemplate(camel);
        template.setMaximumCacheSize(100);
        try {
            template.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Move2AlfException("Camel exception", e);
        }

        logger.debug("maximum cache size=" + template.getMaximumCacheSize() + " and current=" + template.getCurrentCacheSize());
        // Create temporary folder
        final File tempFolder = Files.createTempDir();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Using temporary folder: %s", tempFolder.toString()));
        }

        boolean done = false;
        boolean firstLoop = true;
        String first = null;
        int retries = 0;
        while (!done) {
            Exchange exchange = template.receive(DIRECT_ENDPOINT);
            if(exchange==null) {
  /*              if(retries<10) {
                    logger.debug("exchange=null, trying again");
                    retries++;
                    continue;
                }   else {
                    logger.error("Too many retries with exchange=null, exiting");
                    done=true;
                    break;
                }*/
                continue;
            }

            final Message messageIn = exchange.getIn();

            // CMIS specific
            final String folderPath = (String) messageIn.getHeader(CamelCMISConstants.CMIS_FOLDER_PATH);
            String cmisName = (String) messageIn.getHeader("cmis:name");
            String cmisPath = (String) messageIn.getHeader("cmis:path");
            String cmisObjectId = (String) messageIn.getHeader("cmis:objectId");
            logger.debug(String.format("folderPath: %s, cmisName: %s, cmisPath: %s", folderPath, cmisName, cmisPath));

			// log all headers
			for (Map.Entry<String, Object> header : messageIn.getHeaders().entrySet()) {
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Message ID: '%s' - Header name: '%s' value: '%s'",
                            messageIn.getMessageId(),
                            header.getKey(),
                            (header.getValue() != null) ? header.getValue().toString() : "null"));
				}
			}

            final boolean isFile = (cmisPath == null);
            final String path = isFile ? folderPath + "/" + cmisName : cmisPath;

            if (first == null) {
//				logger.debug("First file encountered: " + path);
                first = path;
            }

            if (path.equals(first) && !(firstLoop)) {
                logger.info("done=true, will stop, got again to first");
                done = true;
                template.doneUoW(exchange);
                break;
            }

            if (isFile) {
                boolean failed = false;

//				logger.debug("This is a file, writing to filesystem");
                final InputStream in;

                in = messageIn.getBody(InputStream.class);

                String uuid = extractUuid(cmisObjectId);

                File file = null;
                //final File file = new File(tempFolder, cmisName);

                if(!getSkipContentUpload()) {
                    file = new File(tempFolder, uuid);
                    try {
//					logger.debug("Stream: " + in);
                        ByteStreams.copy(in, new FileOutputStream(file));
                        in.close();
                    } catch (FileNotFoundException e) {
                        // file is actually a folder. should never happen
                        throw new Move2AlfException(e);
                    } catch (IOException e) {
                        logger.error(String.format("'%s' failed", cmisName), e);
                        Set<ProcessedDocumentParameter> params = new HashSet<ProcessedDocumentParameter>();
                        ProcessedDocumentParameter parameter = new ProcessedDocumentParameter();
                        parameter.setName(PARAM_ERROR_MESSAGE);
                        parameter.setValue(e.toString());
                        params.add(parameter);
                        ReportMessage reportMessage = new ReportMessage(file.getName(),
                                new Date(),
                                VALUE_FAILED,
                                params,
                                null);
                        sendMessage(PipelineAssemblerImpl.REPORTER, reportMessage);
                        failed = true;
                    } catch (NullPointerException e) {
                        logger.debug("Empty message body (no content), will continue anyway!");
                    }
                }

                if (!failed) {

                    final FileInfo fileInfo = new FileInfo();
                    fileInfo.put(PARAM_RELATIVE_PATH, folderPath);
                    fileInfo.put(PARAM_FILE, file);
                    fileInfo.put(PARAM_NAME, cmisName);

                    fileInfo.put(PARAM_CAMEL_HEADER, messageIn.getHeaders());

                    sendMessage(fileInfo);
                }
            }
            firstLoop = false;
            template.doneUoW(exchange);
        }

        try {
            logger.info("shutting down");
            //camel.stopRoute(route.getId(),30, TimeUnit.SECONDS);
            camel.stop();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Move2AlfException("Camel exception", e);
        }
    }

    private String extractUuid(String cmisObjectId) {
        // for Alfresco, ids contain the space e.g. workspace/SpacesStore; remove it
        int idx = cmisObjectId.lastIndexOf("/");
        if(idx != -1)
            return cmisObjectId.substring(cmisObjectId.lastIndexOf("/"));
        else
            return cmisObjectId;
    }
}
