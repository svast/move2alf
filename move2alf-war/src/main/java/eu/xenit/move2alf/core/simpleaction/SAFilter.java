package eu.xenit.move2alf.core.simpleaction;

import java.io.File;

import eu.xenit.move2alf.core.action.ActionInfo;
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;

@ActionInfo(classId = "SAFilter",
            description = "Filters messages based on the file extension")
public class SAFilter extends Move2AlfReceivingAction<FileInfo> {

	public static final String PARAM_EXTENSION = "extension";

	private static final Logger logger = LoggerFactory
			.getLogger(SAFilter.class);

    private String extension;
    public void setExtension(String extension){
        this.extension = extension;
    }

    @Override
    public void executeImpl(FileInfo fileInfo) {
        File file = (File) fileInfo.get(Parameters.PARAM_FILE);

        if (extension != null && extension.startsWith("*")) {
            extension = extension.substring(1);
        }

        if ("".equals(extension)
                || extension == null
                || file.getPath().toLowerCase().endsWith(
                extension.toLowerCase())) {
            sendMessage(fileInfo);
        } else {
            logger.debug("File " + file.getName()
                    + " does not have the correct extension - skip.");
        }
    }
}
