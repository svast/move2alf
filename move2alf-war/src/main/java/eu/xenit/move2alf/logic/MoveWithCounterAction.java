package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.action.ClassInfo;
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import eu.xenit.move2alf.core.action.messages.SetCounterMessage;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.pipeline.actions.EOCAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
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
public class MoveWithCounterAction extends Move2AlfReceivingAction implements EOCAware {
    private static final Logger logger = LoggerFactory.getLogger(MoveWithCounterAction.class);

    public static final String PARAM_PATH = "path";
    private String path;

    @Value(value = "#{'${move.keepstructure}'}")
    private boolean moveKeepStructure;

    public void setPath(String path){
        this.path = path;
    }

    private Map<String,Integer> counters = new HashMap();

    @Override
    protected void executeImpl(Object message) {
        FileInfo output = new FileInfo();
        if(message instanceof FileInfo) {
            FileInfo fileInfo = (FileInfo) message;
            logger.info("In MoveWithCounterAction, fileInfo=" + fileInfo);
            logger.debug("Decreasing the counters in object " + this + " for input file " + fileInfo.get(Parameters.PARAM_NAME));
            String inputPath = (String)fileInfo.get(Parameters.PARAM_INPUT_PATH);
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
                    tryToMove(key,inputPath,message);
                }
            }
        } else if(message instanceof SetCounterMessage) {
            SetCounterMessage counterMessage = (SetCounterMessage)message;
            String key = counterMessage.getId();
            Integer value = counterMessage.getCounter();
            String inputPath = counterMessage.getInputPath();
            Integer counterExisting = counters.get(key);
            if(counterExisting==null)
                counterExisting=0;
            value+=counterExisting;
            counters.put(key,value);
            logger.debug("Setting counter in object " + this + " for " + key + " to " + value + ", before it was " + counterExisting);
            if(value==0)
                tryToMove(key,inputPath,message);
            return;
        } else if(message instanceof Map) {
            logger.debug("Setting the counters in object " + this + " for message=" + message);
            Map<String,Integer> inputCounters = (Map<String,Integer>)message;
            for(String key : (Set<String>)inputCounters.keySet()) {
                Integer counterExisting = counters.get(key);
                Integer counterNew = inputCounters.get(key);
                if(counterExisting==null)
                    counterExisting=0;
                counterNew+=counterExisting;
                counters.put(key,counterNew);
                logger.debug("Setting counter for " + key + " to " + counterNew + ", before it was " + counterExisting);
                if(counterNew==0)
                    tryToMove(key,"",message);

            }
            // do not send further messages in this case
            return;
        }

        if(sendingContext.hasReceiver(PipelineAssemblerImpl.DEFAULT_RECEIVER)){
            sendMessage(PipelineAssemblerImpl.DEFAULT_RECEIVER, output);
        }
    }

    private void tryToMove(String key, String inputPath, Object message) {
        File oldFile = new File(key);
        String newPath = path;
        if(moveKeepStructure && (inputPath!=null) && !(inputPath.isEmpty()))
            newPath = Util.createRelativePath(path, oldFile.getPath(), inputPath);
        logger.info("Will move file " + oldFile.getPath() + " to " + newPath);
        File newFile = Util.moveFile(newPath, oldFile);
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
        counters.clear();
    }
}