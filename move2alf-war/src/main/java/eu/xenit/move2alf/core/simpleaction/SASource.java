package eu.xenit.move2alf.core.simpleaction;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.action.ClassInfo;
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import eu.xenit.move2alf.core.action.Move2AlfStartAction;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.logic.PipelineAssemblerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ClassInfo(classId = "SASource",
            category = ConfigurableObject.CAT_DEFAULT,
            description = "Reads files from the filesystem and sends fileinfo messages")
public class SASource extends Move2AlfReceivingAction<Object> {
    
    private static final Logger logger = LoggerFactory.getLogger(SASource.class);

	public String getDescription() {
		return "Listing documents";
	}

    private Boolean skipContentUpload;

    public Boolean getSkipContentUpload() {
        return skipContentUpload;
    }

    public void setSkipContentUpload(Boolean skipContentUpload) {
        this.skipContentUpload = skipContentUpload;
    }

    public void setSkipContentUpload(String skipContentUpload) {
        this.skipContentUpload = Boolean.valueOf(skipContentUpload);
    }

    public static final String PARAM_INPUTPATHS = "inputPaths";
    private List<String> inputPaths;

    public void setInputPaths(String inputPaths){
        this.inputPaths = Arrays.asList(inputPaths.split("\\|"));
    }

    @Override
    public void executeImpl(Object message) {
        for (String inputPath: inputPaths){
            list(inputPath, true);
        }
    }

    private void list(final String path, final boolean recursive) {
        logger.info("Reading files from " + path);
        final File source = new File(path);
        if (source.exists() == false) {
            source.mkdir();
        }
        listFiles(source, recursive, path);
    }

    private void listFiles(final File source, final boolean recursive,
                                 String path) {
        final File[] files = source.listFiles();
        for (final File file : files) {
            if (recursive && file.isDirectory()) {
                listFiles(file, recursive, path);
            }
            if (file.isFile()) {
                FileInfo fileMap = new FileInfo();
                fileMap.put(Parameters.PARAM_FILE, file);
                fileMap.put(Parameters.PARAM_INPUT_FILE, file);
                List<File> transformFiles = new ArrayList<File>();
                transformFiles.add(file);
                fileMap.put(Parameters.PARAM_TRANSFORM_FILE_LIST, transformFiles);
                fileMap.put(Parameters.PARAM_RELATIVE_PATH, Util.relativePath(path,
                        file));
                fileMap.put(Parameters.PARAM_INPUT_PATH, path);
                sendMessage(fileMap);
            }
        }
    }
}
