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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: rox
 * Date: 10/1/13
 * Time: 12:53 PM
 */
@ClassInfo(classId = "MoveWithCounterAction",
        description = "Moves files on the filesystem when a counter gets to 0")
public class MoveWithCounterAction extends Move2AlfReceivingAction<Object> implements EOCAware {
    private static final Logger logger = LoggerFactory.getLogger(MoveWithCounterAction.class);

    public static final String PARAM_PATH = "path";
    private String path;

    public void setPath(String path){
        this.path = path;
    }

    private Map<String,Integer> counters = new HashMap();

    @Override
    public void executeImpl(Object message) {
        FileInfo output = new FileInfo();
        if(message instanceof FileInfo) {
            FileInfo fileInfo = (FileInfo) message;
            logger.debug("Decreasing the counters for " + fileInfo.get(Parameters.PARAM_NAME));
            HashMap<String,Integer> iCounters = (HashMap)fileInfo.get(Parameters.PARAM_COUNTERS);

            output.putAll(fileInfo);

            for(String key : iCounters.keySet()) {
                Integer counter = counters.get(key);
                Integer iCounter = iCounters.get(key);

                if(counter==null) { // only happens if there are multiple jobs running in the same time
                   logger.info("Got key which was not present in the batch file: " + key);
                   counter = 0;
                }

                counter -= iCounter;
                counters.put(key,Integer.valueOf(counter));
                logger.debug("Counter for " + key + " was decreased with " + iCounter + " and is now " + counter);

                if(counter.intValue()==0) {
                    tryToMove(key,message);
                    /*File inputFile = (File)fileInfo.get(Parameters.PARAM_INPUT_FILE);
                    if(inputFile!=null && key.equals(inputFile.getPath())) {
                        output.put(Parameters.PARAM_INPUT_FILE, newFile);
                    }
                    File file = (File)fileInfo.get(Parameters.PARAM_FILE);
                    if(file!=null && key.equals(file.getPath())) {
                        output.put(Parameters.PARAM_FILE, newFile);
                    }*/
                }
            }
        } else if(message instanceof HashMap) {
            logger.debug("Setting the counters for message=" + message);
            HashMap<String,Integer> inputCounters = (HashMap<String,Integer>)message;
            for(String key : (Set<String>)inputCounters.keySet()) {
                Integer counterExisting = counters.get(key);
                Integer counterNew = inputCounters.get(key);
                if(counterExisting==null)
                    counterExisting=0;
                counterNew+=counterExisting;
                counters.put(key,counterNew);
                logger.debug("Setting counter for " + key + " to " + counterNew + ", before it was " + counterExisting);
                if(counterNew==0)
                    tryToMove(key,message);

            }
            // do not send further messages in this case
            return;
        }

        if(sendingContext.hasReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER)){
            sendMessage(PipelineAssemblerImpl.DEFAULT_RECEIVER, output);
        }
    }

    private void tryToMove(String key, Object message) {
        File oldFile = new File(key);
        File newFile = Util.moveFile(path, oldFile);
        if (newFile == null) {
            String errorMessage = "Cannot move " + oldFile + " to " + newFile;
            handleError(message,errorMessage);
        } else {
            logger.info("Moved " + oldFile + " to " + newFile);
            counters.remove(key);

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