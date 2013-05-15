package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.io.IOException;

import eu.xenit.move2alf.core.action.Move2AlfAction;
import eu.xenit.move2alf.core.action.messages.FileInfoMessage;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionSingleResult;

public class SAMimeType extends Move2AlfAction<FileInfoMessage> {

	private static final Logger logger = LoggerFactory.getLogger(SAMimeType.class);
	private static final Tika tika = new Tika();

	@Override
	public String getDescription() {
		return "Determining MIME types";
	}

	String determineMimeType (File file) {
		String mimeType;
		try {
			mimeType = tika.detect(file);
			if(mimeType.contains(";")){
				mimeType = mimeType.split(";")[0];
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		}
		return mimeType;
	}

    @Override
    public void executeImpl(FileInfoMessage message) {
        FileInfo output = new FileInfo();
        output.putAll(message.fileInfo);
        File file = (File) message.fileInfo.get(Parameters.PARAM_FILE);
        output.put(Parameters.PARAM_MIMETYPE, determineMimeType(file));
        sendMessage(new FileInfoMessage(output));
    }
}
