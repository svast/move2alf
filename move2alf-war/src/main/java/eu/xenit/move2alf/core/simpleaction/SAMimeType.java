package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionSingleResult;

public class SAMimeType extends SimpleActionSingleResult {

	private static final Logger logger = LoggerFactory.getLogger(SAMimeType.class);
	private static final Tika tika = new Tika();

	@Override
	public String getDescription() {
		return "Determining MIME types";
	}

	@Override
	public FileInfo executeSingleResult(
			final FileInfo parameterMap, final ActionConfig config) {
		FileInfo output = new FileInfo();
		output.putAll(parameterMap);
		File file = (File) parameterMap.get(Parameters.PARAM_FILE);
		output.put(Parameters.PARAM_MIMETYPE, determineMimeType(file));
		return output;
	}

	String determineMimeType (File file) {
		String mimeType = null;        
		try {
			mimeType = tika.detect(file);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new Move2AlfException(e.getMessage(), e);
		}
		return mimeType;
	}
}
