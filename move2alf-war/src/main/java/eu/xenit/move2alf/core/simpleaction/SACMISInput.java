package eu.xenit.move2alf.core.simpleaction;

import com.google.common.io.Files;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import eu.xenit.move2alf.core.action.messages.RecursiveCmisMessage;
import org.apache.camel.*;
import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultConsumerTemplate;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import eu.xenit.move2alf.core.action.metadata.ProcessCmisDocument;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static final String PARAM_RECURSIVE = "recursive";

    public static final int RETRIES = 1;
    public static final int TIMEOUT = 600;

    private String cmisUrl;
    private String cmisUsername;
    private String cmisPassword;
    private String cmisQuery;
    private Boolean skipContentUpload;
    private Boolean recursive;

    public Boolean getSkipContentUpload() {
        return skipContentUpload;
    }

    public void setSkipContentUpload(String skipContentUpload) {
        this.skipContentUpload = Boolean.valueOf(skipContentUpload);
    }

    public Boolean getRecursive() {
        return recursive;
    }

    public void setRecursive(String recursive) {
        this.recursive = Boolean.valueOf(recursive);
    }

    public String getCmisQuery() {
        return cmisQuery;
    }

    public void setCmisQuery(String cmisQuery) {
        this.cmisQuery = cmisQuery;
    }

    public void setCmisUrl(String cmisURL) {
        this.cmisUrl = cmisURL;
    }

    public void setCmisUsername(final String cmisUsername) {
        this.cmisUsername = cmisUsername;
    }

    public void setCmisPassword(final String cmisPassword) {
        this.cmisPassword = cmisPassword;
    }

    public String getEndpoint() {
        cmisUrl = cmisUrl.replace("?","QUESTIONMARK");  // Sharepoint endpoint contains parameters in the url; temporary hide them, otherwise problems parsing the cmis parameters
        if(cmisQuery.isEmpty())
            return String.format("cmis://%s?username=%s&password=%s&readContent=%s&objectFactoryClass=org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl", cmisUrl, cmisUsername, cmisPassword,!getSkipContentUpload());
        else
            return String.format("cmis://%s?username=%s&password=%s&readContent=%s&objectFactoryClass=org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl&query=%s", cmisUrl, cmisUsername, cmisPassword,!getSkipContentUpload(),cmisQuery);
    }

    @Override
    protected void executeImpl(Object message) {
        if(!recursive)
            executeImplCamel(message);
        else
            executeImplOpenCmis(message);
    }

    private void executeImplOpenCmis(Object inputMessage) {
        logger.info("openCmis");

        String objectId;
        Pattern p = Pattern.compile("(.*) WHERE IN_TREE\\(d,'(.*)'\\)(.*)");
        Matcher m = p.matcher(cmisQuery);
        if(m.matches()) {
            objectId=m.group(2);
        } else {
            String errorMessage = "Query " + cmisQuery + " is not suited for the recursive CMIS, it should contain an IN_TREE operator";
            logger.error(errorMessage);
            throw new Move2AlfException("Recursive CMIS exception " + errorMessage);
        }

        Session session = createSession();
        Folder folder = (Folder) session.getObject(session.createObjectId(objectId));
        String path = folder.getPaths().get(0);

        RecursiveCmisMessage message = new RecursiveCmisMessage(objectId,null,path,"");
        sendMessage(message);
    }

    private Session createSession() {
        logger.info("cmisUrl=" + cmisUrl + " and cmisUser=" + cmisUsername + " and cmisPassword=" + cmisPassword);
        SessionFactory factory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();

        // user credentials
        parameter.put(SessionParameter.USER, cmisUsername);
        parameter.put(SessionParameter.PASSWORD, cmisPassword);

        // connection settings
        parameter.put(SessionParameter.ATOMPUB_URL, cmisUrl);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

        List<Repository> repositories = factory.getRepositories(parameter);
        Session session = repositories.get(0).createSession();

        return session;
    }

    private void executeImplCamel(Object message) {
        final CamelContext camel = new DefaultCamelContext();
        camel.getShutdownStrategy().setShutdownNowOnTimeout(true);
        camel.getShutdownStrategy().setTimeout(60);

        logger.debug("Endpoint=" + getEndpoint());

        RouteDefinition route = new RouteDefinition();
        try {
            route.from(getEndpoint());
            route.onException(CmisObjectNotFoundException.class).throwException(new Move2AlfException("camel"));
            route.onException(Exception.class).throwException(new Move2AlfException("camel"));
            route.to(DIRECT_ENDPOINT);

            ((ModelCamelContext)camel).addRouteDefinition(route);
            camel.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Move2AlfException("Camel exception", e);
        }

/*        try {
			camel.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
                    onException(CmisObjectNotFoundException.class).throwException(new Move2AlfException("camel"));
                    onException(Exception.class).throwException(new Move2AlfException("camel"));
                    from(getEndpoint()).to(DIRECT_ENDPOINT);
				}
			});
            camel.getShutdownStrategy().setShutdownNowOnTimeout(true);
            camel.getShutdownStrategy().setTimeout(30);
			camel.start();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Move2AlfException("Camel exception", e);
		}*/

        final ConsumerTemplate template = new DefaultConsumerTemplate(camel);
        template.setMaximumCacheSize(100);
        try {
            template.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Move2AlfException("Camel exception", e);
        }

        logger.debug("got here");
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
            Exchange exchange = template.receive(DIRECT_ENDPOINT,TIMEOUT);
            logger.debug("***************** exchange=" + exchange + " and retries=" + retries);
            if(exchange==null && retries<RETRIES) {
                retries++;
                continue;
            } else if(retries==RETRIES) {
                done=true;
                break;
            }

            final Message messageIn = exchange.getIn();
            Object ex = messageIn.getHeader("exception");
            if(ex != null) {
                throw new Move2AlfException(ex.toString());
            }

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
                ProcessCmisDocument.processDocument(cmisObjectId, cmisName, folderPath, tempFolder, messageIn.getBody(InputStream.class), messageIn.getHeaders(), skipContentUpload, sendingContext);
            }
            firstLoop = false;
            template.doneUoW(exchange);
        }

        try {
            logger.info("shutting down");
            camel.stopRoute(route.getId(),30, TimeUnit.SECONDS);
//            camel.stop();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Move2AlfException("Camel exception", e);
        }
    }
}
