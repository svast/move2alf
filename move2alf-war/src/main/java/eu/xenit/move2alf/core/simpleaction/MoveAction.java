package eu.xenit.move2alf.core.simpleaction;

import java.io.File;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.action.ClassInfo;
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.logic.PipelineAssemblerImpl;

@ClassInfo(classId = "MoveAction",
            description = "Moves files on the filesystem")
public class MoveAction extends Move2AlfReceivingAction<FileInfo> {

    public static final String PARAM_PATH = "path";
    private String path;
    public void setPath(String path){
        this.path = path;
    }

    @Override
    public void executeImpl(FileInfo fileInfo) {
        FileInfo output = new FileInfo();
        output.putAll(fileInfo);
        String source = (String) fileInfo.get(Parameters.PARAM_INPUT_PATH);
        File file = (File) fileInfo.get(Parameters.PARAM_FILE);
        File newFile = Util.moveFile(source, path, file);
        if (newFile != null) {
            output.put(Parameters.PARAM_FILE, newFile);
        } else {
            throw new Move2AlfException("Could not move file "
                    + file.getAbsolutePath() + " to " + output);
        }
        if(sendingContext.hasReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER)){
            sendMessage(PipelineAssemblerImpl.DEFAULT_RECEIVER, output);
        }
    }
}
