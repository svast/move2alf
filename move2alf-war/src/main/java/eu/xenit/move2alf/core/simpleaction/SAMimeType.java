package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.io.IOException;

import eu.xenit.move2alf.core.action.ClassInfo;
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;

@ClassInfo(classId = "SAMimeType",
            description = "Detects the mimetype of a file and adds it to the metadata")
public class SAMimeType extends Move2AlfReceivingAction<FileInfo> {

	private static final Logger logger = LoggerFactory.getLogger(SAMimeType.class);
	private static final Tika tika = new Tika();

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
    public void executeImpl(FileInfo fileInfo) {
        FileInfo output = new FileInfo();
        output.putAll(fileInfo);
        File file = (File) fileInfo.get(Parameters.PARAM_FILE);
        output.put(Parameters.PARAM_MIMETYPE, determineMimeType(file));
        sendMessage(output);
    }
}
