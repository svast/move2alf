package eu.xenit.move2alf.core.simpleaction;

import com.google.common.io.Files;
import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.action.CmisQuery;
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import eu.xenit.move2alf.core.action.messages.RecursiveCmisMessage;
import eu.xenit.move2alf.common.CMISHelper;
import org.alfresco.cmis.client.AlfrescoDocument;
import org.apache.chemistry.opencmis.client.api.*;
import eu.xenit.move2alf.core.action.metadata.ProcessCmisDocument;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
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
 * @author Roxana Angheluta
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

    public static final int RETRIES = Integer.MAX_VALUE;
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
            executeImplSimpleQuery(message);
        else
            executeImplOpenCmis(message);
    }


    private void executeImplSimpleQuery(Object inputMessage) {
        logger.info("Simple query");
        Session session = createSession();
        int pageSize = CmisQuery.pageSize;

        int count = 0;
        int pageNumber = 0;
        boolean finished = false;
        logger.debug("Sending query {}", cmisQuery);
        ItemIterable<QueryResult> itemIterable = CmisQuery.executeQuery(cmisQuery,session);
        while (!finished) {
            int countStart=count;
            ItemIterable<QueryResult> currentPage = itemIterable.skipTo(count).getPage();
            logger.debug("Processing page {}", pageNumber, " with total number of items=" + currentPage.getTotalNumItems());
            if(pageSize!=currentPage.getTotalNumItems() && currentPage.getHasMoreItems()) {
                logger.error("Page " + pageNumber + " did not get the corrent number of results:" + currentPage.getTotalNumItems() + " will repeat");
                continue;
            }
            for (QueryResult item : currentPage) {

                String objectId = item.getPropertyValueById(PropertyIds.OBJECT_ID);
                try {
                    AlfrescoDocument doc = (AlfrescoDocument) session.getObject(session.createObjectId(objectId));
                    Map<String, Object> properties = CMISHelper.propertyDataToMap(doc.getProperties());
                    List<Folder> parents = doc.getParents();
                    if(parents!=null && !(parents.isEmpty())) {
                        properties.put(Parameters.PARAM_ACL, doc.getAcl());
                        String path = parents.get(0).getPath();
                        String name = doc.getName();
                        properties.put(Parameters.PARAM_INPUT_PATH, path);
                        Object objectBaseTypeId = item.getPropertyValueById(PropertyIds.BASE_TYPE_ID);
                        InputStream inputStream = null;
                        if(!(skipContentUpload) && (CMISHelper.CMIS_DOCUMENT).equals(objectBaseTypeId)) {
                            inputStream = CmisQuery.getContentStreamFor(item, session);
                        }
                        logger.debug("Will process document " + objectId + " with path " + path + " and inputStream=" + inputStream + " and skipContentUpload=" + skipContentUpload);
                        ProcessCmisDocument.processDocument(objectId, name, path, CmisQuery.tempFolder, inputStream, properties, skipContentUpload, sendingContext);
                    } else {
                        logger.info("Skipping " + doc.getName() + " because it has no parents");
                    }
                } catch (Exception e) {
                    logger.error("Skipping " + objectId + ", exception: " + e);
                }

                count++;
            }
            if(!finished)
                if(currentPage.getTotalNumItems()*(pageNumber+1)==count) {
                    pageNumber++;
                    logger.debug("Increased page number, now " + pageNumber + " where count=" + count + " and total number of items on paged processed=" + currentPage.getTotalNumItems());
                } else {
                    logger.error("Page " + pageNumber + " has not been processed correctly, count=" + count + " and total number of items=" + currentPage.getTotalNumItems() + " retrying");
                    count=countStart;
                }
            if (!(currentPage.getHasMoreItems())) {
                //if(count==currentPage.getTotalNumItems()) {
                finished = true;
            }
        }
    }

    private void executeImplOpenCmis(Object inputMessage) {
        logger.info("Recursive query");

        String objectId;
        Pattern p = Pattern.compile("(.*) WHERE IN_TREE\\(d,'(.*)'\\)(.*)");
        Matcher m = p.matcher(cmisQuery);
        if(m.matches()) {
            objectId=m.group(2);
        } else {
            String errorMessage = "Query " + cmisQuery + " is not suited for the recursive CMIS, it should contain an IN_TREE operator AND a specifier. Example: SELECT * FROM cmn:lepWinstc AS d JOIN cmn:hasProducerNumber AS e ON d.cmis:objectId=e.cmis:objectId JOIN cmn:hasYear AS f ON d.cmis:objectId=f.cmis:objectId JOIN cmn:hasNameInsuranceTaker AS g ON d.cmis:objectId=g.cmis:objectId JOIN cmn:hasAssNumber AS h ON d.cmis:objectId=h.cmis:objectId JOIN cmn:hasPolicyNumber AS i ON d.cmis:objectId=i.cmis:objectId WHERE IN_TREE(d,'workspace://SpacesStore/b44dc3a4-6b09-4db9-a687-2b3095b984a8')";
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

        parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");

        List<Repository> repositories = factory.getRepositories(parameter);
        Session session = repositories.get(0).createSession();

        return session;
    }
}
