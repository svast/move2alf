package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.action.ClassInfo;
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.pipeline.actions.EOCAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rox
 * Date: 10/1/13
 * Time: 12:53 PM
 */
@ClassInfo(classId = "MoveWithCounterAction",
        description = "Moves files on the filesystem when a counter gets to 0")
public class MoveWithCounterAction extends Move2AlfReceivingAction<FileInfo> implements EOCAware {
    private static final Logger logger = LoggerFactory.getLogger(MoveWithCounterAction.class);

    public static final String PARAM_PATH = "path";
    private String path;

    public void setPath(String path){
        this.path = path;
    }

    private static Map counters = new HashMap();


    @Override
    public void executeImpl(FileInfo fileInfo) {
        FileInfo output = new FileInfo();
        output.putAll(fileInfo);
        File file = (File) fileInfo.get(Parameters.PARAM_INPUT_FILE);
        Integer counter = (Integer) counters.get(file);

        if(counter==null) {  // first time the function is called
            try {
                logger.info("Counting lines of file " + file);
                counter = Util.countLines(file)-1;
                counter--;
                counters.put(file,counter);
            } catch (IOException e) {
                throw new Move2AlfException("Could not count lines in file " + file.getAbsolutePath());
            }
        } else {
            counter--;
            counters.put(file,counter);
        }

        logger.info("After decreasing the counter, there are still " + counter + " files to be processed");
        if(counter.intValue()==0) {
            File newFile = Util.moveFile(path, file);
            if (newFile != null) {
                output.put(Parameters.PARAM_INPUT_FILE, newFile);
            } else {
                throw new Move2AlfException("Could not move file " + file.getAbsolutePath() + " to " + output);
            }
        }

        if(sendingContext.hasReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER)){
            sendMessage(PipelineAssemblerImpl.DEFAULT_RECEIVER, output);
        }
    }

    @Override
    public void beforeSendEOC() {
        resetCounters();
    }

    private void resetCounters() {
        counters.clear();
    }
}