package eu.xenit.move2alf.core.action.metadata;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.action.ActionInfo;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionWithSourceSink;

import java.io.File;

@ActionInfo(classId = "SetAlfrescoContentUrl",
            description = "Action that uploads a file and gets the contenturl")
public class SetAlfrescoContentUrlAction extends SimpleActionWithSourceSink<FileInfo> {
    @Override
    protected void executeImpl(FileInfo message) {
        String contentUrl = getSink().putContent(getSinkConfig(),(File) message.get(Parameters.PARAM_FILE),(String) message.get(Parameters.PARAM_MIMETYPE));
        message.put(Parameters.PARAM_CONTENTURL, contentUrl);
        sendMessage(message);
    }
}
