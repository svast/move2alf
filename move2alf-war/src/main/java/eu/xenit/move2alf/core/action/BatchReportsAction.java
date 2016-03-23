package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.pipeline.actions.EOCAware;

import java.util.ArrayList;
import java.util.List;

/**
 * Batches database reports before sending to database
 * User: thijs
 * Date: 5/27/13
 * Time: 5:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class BatchReportsAction extends Move2AlfReceivingAction<ReportMessage> implements EOCAware{
    public static final String PARAM_BATCHSIZE = "batchSize";
    private int batchSize;
    public void setBatchSize(String batchSize){
        this.batchSize = Integer.parseInt(batchSize);
    }

    private List<ReportMessage> reportMessages = new ArrayList<ReportMessage>();

    @Override
    protected void executeImpl(ReportMessage message) {
        reportMessages.add(message);
        if(reportMessages.size() == batchSize){
            sendMessage(reportMessages);
            reportMessages = new ArrayList<ReportMessage>();
        }
    }

    @Override
    public void beforeSendEOC() {
        if(reportMessages.size() > 0){
            sendMessage(reportMessages);
            reportMessages = new ArrayList<ReportMessage>();
        }
    }
}
