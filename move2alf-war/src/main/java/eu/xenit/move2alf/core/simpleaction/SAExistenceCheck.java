package eu.xenit.move2alf.core.simpleaction;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.action.ActionInfo;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionWithSourceSink;
import eu.xenit.move2alf.core.sourcesink.SourceSink;

import java.io.File;

@ActionInfo(classId = "SAExistenceCheck",
            description = "Checks if a filename exists in the sourcesink")
public class SAExistenceCheck extends SimpleActionWithSourceSink<FileInfo> {

    @Override
    public void executeImpl(FileInfo fileInfo) {
        FileInfo newParameterMap = new FileInfo();
        newParameterMap.putAll(fileInfo);

        ConfiguredSourceSink sinkConfig = getSinkConfig();
        SourceSink sink = getSink();

        String name = ((File) newParameterMap.get(Parameters.PARAM_FILE))
                .getName();

        if (!sink.fileNameExists(sinkConfig, name)) {
            throw new Move2AlfException("Document not found");
        }

        sendMessage(newParameterMap);
    }
}
