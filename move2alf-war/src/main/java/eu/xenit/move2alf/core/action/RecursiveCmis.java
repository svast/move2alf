package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.action.messages.RecursiveCmisMessage;
import eu.xenit.move2alf.pipeline.actions.StartAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: rox
 * Date: 2/3/14
 * Time: 4:49 PM
 */
public class RecursiveCmis extends Move2AlfReceivingAction<Object> implements StartAware {
    private static final Logger logger = LoggerFactory.getLogger(RecursiveCmis.class);
    private String cmisQuery;

    public void setCmisQuery(String cmisQuery) {
        this.cmisQuery = cmisQuery;
    }

    String queryStart, queryEnd;

    public String buildFoldersQuery(String objectId) {
        String queryFull = "SELECT * FROM cmis:folder where IN_FOLDER('" + objectId + "')";
        return queryFull;
    }

    public String buildDocumentsQuery(String objectId) {
        String queryFull = queryStart + " WHERE IN_FOLDER(d,'" + objectId + "') " + queryEnd;
        return queryFull;
    }

    @Override
    protected void executeImpl(Object message) {
        RecursiveCmisMessage m = (RecursiveCmisMessage)(message);
        String objectId = m.getObjectId();
        String path = m.getPath();

        String foldersQuery = buildFoldersQuery(objectId);
        logger.debug("will send messageFolders for objectId=" + objectId + " and path=" + path + " and foldersQuery=" + foldersQuery);
        RecursiveCmisMessage cmisQueryMessageFolders = new RecursiveCmisMessage(objectId,CmisQuery.TYPE_FOLDER,path,foldersQuery);
        sendMessage(cmisQueryMessageFolders);

        String documentsQuery = buildDocumentsQuery(objectId);
        logger.debug("will send messageDocuments for objectId=" + objectId + " and path=" + path + " and documentsQuery=" + documentsQuery);
        RecursiveCmisMessage cmisQueryMessageDocuments = new RecursiveCmisMessage(objectId,CmisQuery.TYPE_DOCUMENT,path,documentsQuery);
        sendMessage(cmisQueryMessageDocuments);
    }

    @Override
    public void onStart() {
        String objectId = null;
        Pattern p = Pattern.compile("(.*) WHERE IN_TREE\\(d,'(.*)'\\)(.*)");
        Matcher m = p.matcher(cmisQuery);
        if(m.matches()) {
            queryStart=m.group(1);
            objectId=m.group(2);
            queryEnd = m.group(3);
            logger.debug("objectId=" + objectId + " and queryStart=" + queryStart + " and queryEnd=" + queryEnd);
        } else {
            String errorMessage = "Query " + cmisQuery + " is not suited for the recursive CMIS, it should contain an IN_TREE operator";
            logger.error(errorMessage);
            throw new Move2AlfException("Recursive CMIS exception " + errorMessage);
        }
    }
}
