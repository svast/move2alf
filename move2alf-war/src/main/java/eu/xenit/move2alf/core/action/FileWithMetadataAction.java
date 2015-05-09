package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.action.messages.SetCounterMessage;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.logic.PipelineAssemblerImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created with IntelliJ IDEA.
 * User: rox
 * Date: 10/1/13
 * Time: 10:51 AM
 */
public abstract class FileWithMetadataAction extends Move2AlfReceivingAction<FileInfo> implements ActionWithCounter {

    @Override
    public void setCounters(Map<String, Integer> counters) {
        for(String id : (Set<String>)counters.keySet()) {
            setCounter(id, counters.get(id));
        }
    }

    @Override
    public void setCounter(String counterId, int counter) {
        if (sendingContext.hasReceiver(PipelineAssemblerImpl.MOVE_AFTER_ID)) {
            sendMessage(PipelineAssemblerImpl.MOVE_AFTER_ID, new SetCounterMessage(counterId, Integer.valueOf(counter)));
        }
        if (sendingContext.hasReceiver(PipelineAssemblerImpl.MOVE_NOT_LOADED_ID)) {
            sendMessage(PipelineAssemblerImpl.MOVE_NOT_LOADED_ID, new SetCounterMessage(counterId, Integer.valueOf(counter)));
        }
    }

    @Override
    public void sendFileInfoWithCounters(FileInfo fileInfo, String... counterIds) {
        Map<String, Integer> counters = new HashMap<String, Integer>();
        for(String counter: counterIds){
            counters.put(counter, 1);
        }
        sendFileInfoWithCounters(fileInfo, counters);
    }

    @Override
    public void sendFileInfoWithCounters(FileInfo fileInfo, Map<String, Integer> counters) {
        fileInfo.put(Parameters.PARAM_COUNTERS, counters);
        sendMessage(fileInfo);
    }
}
