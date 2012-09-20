package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.web.dto.JobConfig;

public interface ErrorHandler {

	public abstract void handleError(FileInfo parameterMap,
			JobConfig jobConfig, Cycle cycle, Exception e);

}