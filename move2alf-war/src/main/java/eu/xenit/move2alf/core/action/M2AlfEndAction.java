package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.ProcessedDocument;
import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.pipeline.actions.AbstractStateAction;
import eu.xenit.move2alf.pipeline.actions.EOCAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ClassInfo(classId = "M2AlfEndAction",
            description = "Action that ends the cycle and sends mailmessages if configured so")
public class M2AlfEndAction extends AbstractStateAction implements EOCAware {

    private static final Logger logger = LoggerFactory.getLogger(M2AlfEndAction.class);

    @Autowired
    private JobService jobService;

    public static final String PARAM_SENDREPORT = "sendReport";
    private boolean sendReport = false;
    public void setSendReport(String sendReport){
        this.sendReport = Boolean.parseBoolean(sendReport);
    }

    public static final String PARAM_REPORT_TO = "reportTo";
    private String[] reportTo;
    public void setReportTo(String reportTo){
        this.reportTo = reportTo.split(",");
    }

    public static final String PARAM_SENDERROR = "sendError";
    private boolean sendError = false;
    public void setSendError(String sendError){
        this.sendError = Boolean.parseBoolean(sendError);
    }

    public static final String PARAM_ERROR_TO = "errorTo";
    private String[] errorTo;
    public void setErrorTo(String errorTo){
        this.errorTo = errorTo.split(",");
    }

    public static final String PARAM_MAILFROM = "mailFrom";
    private String mailFrom;
    public void setMailFrom(String mailFrom){
        this.mailFrom = mailFrom;
    }

    public static final String PARAM_URL = "url";
    private String url;
    public void setUrl(String url){
        this.url = url;
    }

    @Override
    public void beforeSendEOC() {
        try {
            jobService.closeCycle((Cycle) getStateValue(StartCycleAction.PARAM_CYCLE));

            if(sendReport || sendError){
                Cycle cycle = (Cycle) getStateValue(StartCycleAction.PARAM_CYCLE);
                int cycleId = cycle.getId();

                // only send report on errors
                boolean errorsOccured = false;
                int counter = 0;
                int amountFailed = 0;
                Date firstDocDateTime = null;
                List<ProcessedDocument> processedDocuments = jobService
                        .getProcessedDocuments(cycle.getId());
                if (processedDocuments != null) {
                    for (ProcessedDocument doc : processedDocuments) {
                        if (counter == 0) {
                            firstDocDateTime = doc.getProcessedDateTime();
                        }

                        if (EProcessedDocumentStatus.FAILED.equals(doc.getStatus())) {
                            errorsOccured = true;
                            amountFailed += 1;
                        }
                        counter += 1;
                    }
                }

                // Get cycle information
                long startDateTime = cycle.getStartDateTime().getTime();
                long endDateTime = cycle.getEndDateTime().getTime();
                long durationInSeconds = (endDateTime - startDateTime) / 1000;
                String duration = Util.formatDuration(durationInSeconds);


                List<String> addresses = new ArrayList<String>();
                if(sendReport && reportTo!=null){
                    addresses.addAll(Arrays.asList(reportTo));
                }
                if(sendError && errorsOccured && errorTo != null){
                    addresses.addAll(Arrays.asList(errorTo));
                }
                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setFrom(mailFrom);
                mail.setTo(addresses.toArray(new String[addresses.size()]));
                mail.setSubject("Move2Alf error report");

                Job job = cycle.getJob();

                mail.setText("Cycle " + cycleId + " of job " + job.getName()
                        + " completed.\n" + "The full report can be found on "
                        + url + "/job/" + job.getId() + "/" + cycleId
                        + "/report" + "\n\nStatistics:" + "\nNr of files: "
                        + processedDocuments.size() + "\nNr of failed: "
                        + amountFailed + "\n\nTime to process: " + duration
                        + "\nStart date/time: " + startDateTime
                        + "\nTime first document loaded: " + firstDocDateTime
                        + "\n\nSent by Move2Alf");

                jobService.sendMail(mail);
            }


        } catch (Exception e) {
            logger.error("Error", e);
        }
    }
}
