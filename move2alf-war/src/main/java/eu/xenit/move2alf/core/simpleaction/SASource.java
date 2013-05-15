package eu.xenit.move2alf.core.simpleaction;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.action.Move2AlfAction;
import eu.xenit.move2alf.core.action.messages.FileInfoMessage;
import eu.xenit.move2alf.core.action.messages.StartMessage;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.sourcesink.FileSourceSink;
import eu.xenit.move2alf.pipeline.actions.AbstractBasicAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SASource extends Move2AlfAction<StartMessage> {

	public String getDescription() {
		return "Listing documents";
	}

    private List<String> inputPaths;
    public void setInputPaths(List<String> inputPaths){
        this.inputPaths = inputPaths;
    }

    @Override
    public void executeImpl(StartMessage message) {
        FileSourceSink source = new FileSourceSink();

        for (String inputPath: inputPaths){
            List<File> files = source.list(null, inputPath, true);
            for (File file : files) {
                FileInfo fileMap = new FileInfo();
                fileMap.put(Parameters.PARAM_FILE, file);
                fileMap.put(Parameters.PARAM_INPUT_FILE, file);
                List<File> transformFiles = new ArrayList<File>();
                transformFiles.add(file);
                fileMap.put(Parameters.PARAM_TRANSFORM_FILE_LIST, transformFiles);
                fileMap.put(Parameters.PARAM_RELATIVE_PATH, Util.relativePath(inputPath,
                        file));
                fileMap.put(Parameters.PARAM_INPUT_PATH, inputPath);
                sendMessage(new FileInfoMessage(fileMap));
            }
        }
    }
}
