package eu.xenit.move2alf.core.simpleaction;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.action.ActionInfo;
import eu.xenit.move2alf.core.action.Move2AlfStartAction;
import eu.xenit.move2alf.core.action.messages.FileInfoMessage;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.sourcesink.FileSourceSink;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ActionInfo(classId = "SASource",
            category = ConfigurableObject.CAT_DEFAULT,
            description = "Reads files from the filesystem and sends fileinfo messages")
public class SASource extends Move2AlfStartAction{

	public String getDescription() {
		return "Listing documents";
	}

    public static final String PARAM_INPUTPATHS = "inputPaths";
    private List<String> inputPaths;
    public void setInputPaths(String inputPaths){
        this.inputPaths = Arrays.asList(inputPaths.split("\\|"));
    }

    @Override
    public void onStartImpl() {
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
