package eu.xenit.move2alf.core.action;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.core.action.messages.CmisDocumentMessage;
import eu.xenit.move2alf.core.action.messages.RecursiveCmisMessage;
import eu.xenit.move2alf.core.action.messages.RecursiveCmisMessage;
import eu.xenit.move2alf.core.action.metadata.ProcessCmisDocument;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.simpleaction.SACMISInput;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.logic.PipelineAssemblerImpl;
import eu.xenit.move2alf.pipeline.actions.StartAware;
import org.apache.camel.component.cmis.CMISHelper;
import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

import static eu.xenit.move2alf.common.Parameters.*;

/**
 * Created with IntelliJ IDEA.
 * User: rox
 * Date: 2/3/14
 * Time: 4:55 PM
 */
public class CmisQuery extends Move2AlfReceivingAction<RecursiveCmisMessage> implements StartAware, Parameterized {
    private static final Logger logger = LoggerFactory.getLogger(CmisQuery.class);
    public static final String TYPE_FOLDER = "folder";
    public static final String TYPE_DOCUMENT = "document";
    public static final String PARAM_SESSION = "session";

    private static final int pageSize = 1000;

    final static File tempFolder = Files.createTempDir();


    @Override
    protected void executeImpl(RecursiveCmisMessage m) {
        String query = m.getQuery();
        String path = m.getPath();
        String type = m.getType();

        boolean skipContentUpload = Boolean.parseBoolean(getParameter(SACMISInput.PARAM_SKIP_CONTENT_UPLOAD));
        int count =0 ;
        int pageNumber = 0;
        boolean finished = false;

        logger.info("query=" + query + " and path=" + path + " and type=" + type);
        ItemIterable<QueryResult> itemIterable = executeQuery(query);
        while (!finished) {
            logger.debug("Processing page " + pageNumber);
            try {
                ItemIterable<QueryResult> currentPage = itemIterable.skipTo(count).getPage();
                for(QueryResult item : currentPage) {
                    if(type.equals(TYPE_FOLDER)) {

                        String objectId = item.getPropertyValueById(PropertyIds.OBJECT_ID);
                        String nameFolder = item.getPropertyValueById(PropertyIds.NAME);
                        String pathFolder = path + "/" + nameFolder;

                        // send message to RecursiveCmis with objectId, pathFolder
                        RecursiveCmisMessage outMessage = new RecursiveCmisMessage(objectId,type,pathFolder,query);
                        sendMessage(PipelineAssemblerImpl.RECURSIVE_CMIS,outMessage);
                    } else {
                        List<? extends PropertyData<?>> props = item.getProperties();
                        //		logger.debug("Got results " + props);
                        String objectIdDocument = item.getPropertyValueById(PropertyIds.OBJECT_ID);
                        String nameDocument = item.getPropertyValueById(PropertyIds.NAME);
                        logger.debug("ObjectId=" + objectIdDocument + " with name=" + nameDocument + " and path=" + path);
                        Map<String, Object> properties = CMISHelper.propertyDataToMap(item.getProperties());
                        try {
                            properties.put(CamelCMISConstants.CMIS_FOLDER_PATH, path);
                            Object objectBaseTypeId = item.getPropertyValueById(PropertyIds.BASE_TYPE_ID);
                            InputStream inputStream = null;
                            if (!(skipContentUpload) && CamelCMISConstants.CMIS_DOCUMENT.equals(objectBaseTypeId)) {
                                inputStream = getContentStreamFor(item);
                            }
                            logger.debug("Will process document " + objectIdDocument + " with name " + nameDocument + " and path " + path + " and inputStream=" + inputStream + " and skipContentUpload=" + skipContentUpload);
                            ProcessCmisDocument.processDocument(objectIdDocument,nameDocument,path, tempFolder, inputStream, properties, skipContentUpload, sendingContext);
                        }  catch (Exception e) {
                            logger.error("Skipping " + objectIdDocument + ", exception: " + e);
                        }
                    }
                    count++;
                 }
                pageNumber++;
                logger.debug("here count=" + count + " and pageNumberFolder=" + pageNumber + " and hasMoreItems=" + currentPage.getHasMoreItems() + " and total number of items=" + currentPage.getTotalNumItems());
                //if(count==currentPage.getTotalNumItems()) {
                if (!currentPage.getHasMoreItems()) {
                    finished = true;
                }
            }  catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }


    public ItemIterable<QueryResult> executeQuery(String query) {
        Session session = (Session)getStateValue(PARAM_SESSION);
        OperationContext operationContext = session.createOperationContext();
        operationContext.setMaxItemsPerPage(pageSize);
        return session.query(query, false, operationContext);
    }

    public InputStream getContentStreamFor(QueryResult item) {
        Document document = getDocument(item);
        if (document != null && document.getContentStream() != null) {
            return document.getContentStream().getStream();
        }
        return null;
    }

    public Document getDocument(QueryResult queryResult) {
        Session session = (Session)getStateValue(PARAM_SESSION);
        if (CamelCMISConstants.CMIS_DOCUMENT.equals(queryResult.getPropertyValueById(PropertyIds.OBJECT_TYPE_ID))
                || CamelCMISConstants.CMIS_DOCUMENT.equals(queryResult.getPropertyValueById(PropertyIds.BASE_TYPE_ID))) {
            String objectId = (String) queryResult.getPropertyById(PropertyIds.OBJECT_ID).getFirstValue();
            return (org.apache.chemistry.opencmis.client.api.Document) session.getObject(objectId);
        }
        return null;
    }

    @Override
    public void onStart() {
        // create the session and save it in a state parameter
        setState(PARAM_SESSION, createSession());
    }

    private Session createSession() {
        SessionFactory factory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();

        // user credentials
        parameter.put(SessionParameter.USER, getParameter(SACMISInput.PARAM_CMIS_USERNAME));
        parameter.put(SessionParameter.PASSWORD, getParameter(SACMISInput.PARAM_CMIS_PASSWORD));

        // connection settings
        parameter.put(SessionParameter.ATOMPUB_URL, getParameter(SACMISInput.PARAM_CMIS_URL));
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

        List<Repository> repositories = factory.getRepositories(parameter);
        Session session = repositories.get(0).createSession();

        return session;
    }
}
