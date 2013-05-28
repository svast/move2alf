package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.simpleaction.data.Batch;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.pipeline.actions.EOCAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ActionInfo(classId = "BatchAction",
            description = "Action that puts FileInfoMessages into batches")
public class BatchAction extends Move2AlfReceivingAction<FileInfo> implements EOCAware{

    private static final Logger logger = LoggerFactory.getLogger(BatchAction.class);
    
    private Batch batch = new Batch();

    public static final String PARAM_BATCHSIZE = "batchSize";
    private int batchSize;
    public void setBatchSize(String batchSize){
        this.batchSize = Integer.parseInt(batchSize);
    }

    @Override
    public void beforeSendEOC() {
        try{
            if(batch.size() > 0){
                sendMessage(batch);
                batch = new Batch();
            }
        } catch(Exception e){
            logger.error("Error in beforeSendEOC", e);
            handleError("BeforeSendEOC", e);
        }
    }

    @Override
    protected void executeImpl(FileInfo message) {
        batch.add(message);
        if(batch.size() == batchSize){
            sendMessage(batch);
            batch = new Batch();
        }
    }
}
