package eu.xenit.move2alf.core.simpleaction;

import java.io.File;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.action.ClassInfo;
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.logic.PipelineAssemblerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@ClassInfo(classId = "MoveAction",
            description = "Moves files on the filesystem")
public class MoveAction extends Move2AlfReceivingAction<FileInfo> {
    private static final Logger logger = LoggerFactory.getLogger(MoveAction.class);

    public static final String PARAM_PATH = "path";
    private String path;

    public void setPath(String path){
        this.path = path;
    }

    @Value(value = "#{'${move.keepstructure}'}")
    private boolean moveKeepStructure;

    @Override
    public void executeImpl(FileInfo fileInfo) {
        FileInfo output = new FileInfo();
        output.putAll(fileInfo);
        File file = (File) fileInfo.get(Parameters.PARAM_FILE);
        String inputPath = (String)fileInfo.get(Parameters.PARAM_INPUT_PATH);
        // if input file is in a subdirectory, add the subdirectory path to destination
        String newPath = path;
        if(moveKeepStructure)
            newPath = Util.createRelativePath(path,file.getPath(),inputPath);
        logger.debug("Will move file " + file.getPath() + " to " + newPath);
        File newFile = Util.moveFile(newPath, file);
        if (newFile != null) {
            output.put(Parameters.PARAM_FILE, newFile);
        } else {
            throw new Move2AlfException("Could not move file "
                    + file.getAbsolutePath() + " to " + output);
        }

        if(sendingContext.hasReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER)) {
            sendMessage(PipelineAssemblerImpl.DEFAULT_RECEIVER, output);
        }
    }
}
