package eu.xenit.move2alf.core.simpleaction;

import java.io.File;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.action.ClassInfo;
import eu.xenit.move2alf.core.dto.ConfiguredSharedResource;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.sourcesink.SourceSink;

@ClassInfo(classId = "SAList",
            description = "Checks if the file exists in the given path")
public class SAList extends ActionWithDestination<FileInfo> {

	public static final String PARAM_PATH = "path";
    private String path;
    public void setPath(String path){
        this.path = path;
    }

    @Override
    public void executeImpl(FileInfo fileInfo) {
        FileInfo newParameterMap = new FileInfo();
        newParameterMap.putAll(fileInfo);

        ConfiguredSharedResource sinkConfig = getSinkConfig();
        SourceSink sink = getSink();

        String basePath = path;
        if (!basePath.endsWith("/")) {
            basePath = basePath + "/";
        }

        if (!basePath.startsWith("/")) {
            basePath = "/" + basePath;
        }

        String relativePath = (String) newParameterMap
                .get(Parameters.PARAM_RELATIVE_PATH);
        relativePath = relativePath.replace("\\", "/");

        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }

        // add "cm:" in front of each path component
        String remotePath = basePath + relativePath;
        String[] components = remotePath.split("/");
        remotePath = "";
        for (String component : components) {
            if ("".equals(component)) {
                remotePath += "/";
            } else if (component.startsWith("cm:")) {
                remotePath += component + "/";
            } else {
                remotePath += "cm:" + component + "/";
            }
        }
        remotePath = remotePath.substring(0, remotePath.length() - 1);

        String name = ((File) newParameterMap.get(Parameters.PARAM_FILE))
                .getName();

        if (!sink.exists(sinkConfig, remotePath, name)) {
            throw new Move2AlfException("Document not found");
        }

        sendMessage(fileInfo);
    }
}
