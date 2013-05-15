package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.action.Move2AlfAction;
import eu.xenit.move2alf.core.action.messages.FileInfoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;

public class SAFilter extends Move2AlfAction<FileInfoMessage> {

	public static final String PARAM_EXTENSION = "extension";

	private static final Logger logger = LoggerFactory
			.getLogger(SAFilter.class);

	@Override
	public String getDescription() {
		return "Filtering documents";
	}

    private String extension;
    public void setExtension(String extension){
        this.extension = extension;
    }

    @Override
    public void executeImpl(FileInfoMessage message) {
        File file = (File) message.fileInfo.get(Parameters.PARAM_FILE);

        if (extension != null && extension.startsWith("*")) {
            extension = extension.substring(1);
        }

        if ("".equals(extension)
                || extension == null
                || file.getPath().toLowerCase().endsWith(
                extension.toLowerCase())) {
            sendMessage(new FileInfoMessage(message.fileInfo));
        } else {
            logger.debug("File " + file.getName()
                    + " does not have the correct extension - skip.");
        }
    }
}
