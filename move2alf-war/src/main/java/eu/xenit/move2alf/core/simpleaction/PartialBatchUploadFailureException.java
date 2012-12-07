package eu.xenit.move2alf.core.simpleaction;

import java.util.Map;

import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.repository.IllegalDocumentException;

public class PartialBatchUploadFailureException extends Exception {
	
	private Map<FileInfo, IllegalDocumentException> fileInfoExceptions;

	public PartialBatchUploadFailureException(
			Map<FileInfo, IllegalDocumentException> fileInfoExceptions) {
		this.fileInfoExceptions = fileInfoExceptions;
	}
	
	public IllegalDocumentException getException(FileInfo fileInfo){
		return fileInfoExceptions.get(fileInfo);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7831209994937532346L;

}
