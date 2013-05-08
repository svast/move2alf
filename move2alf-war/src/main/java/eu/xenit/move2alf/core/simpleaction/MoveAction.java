package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.action.Move2AlfAction;
import eu.xenit.move2alf.core.action.messages.FileInfoMessage;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;

public class MoveAction extends Move2AlfAction<FileInfoMessage> {

    private String path;
    public void setPath(String path){
        this.path = path;
    }

	@Override
	public String getDescription() {
		return "Moving documents";
	}


    @Override
    public void execute(FileInfoMessage message) {
        FileInfo output = new FileInfo();
        output.putAll(message.fileInfo);
        String source = (String) message.fileInfo.get(Parameters.PARAM_INPUT_PATH);
        File file = (File) message.fileInfo.get(Parameters.PARAM_FILE);
        File newFile = Util.moveFile(source, path, file);
        if (newFile != null) {
            output.put(Parameters.PARAM_FILE, newFile);
        } else {
            throw new Move2AlfException("Could not move file "
                    + file.getAbsolutePath() + " to " + output);
        }
        sendMessage(new FileInfoMessage(output));
    }
}
