package eu.xenit.move2alf.core.cyclelistener;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;

import eu.xenit.move2alf.common.Config;
import eu.xenit.move2alf.core.CycleListener;
import eu.xenit.move2alf.core.action.EmailAction;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.ProcessedDocument;
import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;

public class ReportCycleListener extends CycleListener {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportCycleListener.class);

	@Override
	public void cycleEnd(int cycleId) {
		// send email report
		Map<String, String> emailParameters = getEmailActionParameter(cycleId);
		String to = emailParameters.get("emailAddressReport");
		String sendReport = emailParameters.get("sendReport");

		Cycle cycle = getJobService().getCycle(cycleId);
		if (cycle == null) {
			logger.error("Cycle " + cycleId + " not found!");
			return;
		}
		// only send report on errors
		boolean errorsOccured = false;
		int counter = 0;
		int amountFailed = 0;
		Date firstDocDateTime=null;
		List<ProcessedDocument> processedDocuments = getJobService().getProcessedDocuments(cycleId);
		if (processedDocuments != null) {
			for (ProcessedDocument doc : processedDocuments) {
				if(counter == 0){
					firstDocDateTime = doc.getProcessedDateTime();
				}
				
				if (EProcessedDocumentStatus.FAILED.equals(doc.getStatus())) {
					errorsOccured = true;
					amountFailed+=1;
				}
				counter+=1;
			}
		} else {
			logger.info("No processed documents found");
		}

		//Get cycle information
		Date startDateTime = cycle.getStartDateTime();
		Date endDateTime = cycle.getEndDateTime();
		String duration = getJobService().getDuration(startDateTime,
				endDateTime);
		
		String[] addresses = null;
		if (errorsOccured == true && to != null && !"".equals(to)
				&& "true".equals(sendReport)) {
			String[] address = to.split(",");
			int numberAddresses = address.length+1;
			addresses = new String[numberAddresses];
			for(int i =0; i<numberAddresses; i++){
				try{
					addresses[i] = address[i];
				}
				catch(Exception e){
					addresses[i] = "move2alf_support@xenit.eu";

				}
			}
		}else{
			addresses = new String[1];
			addresses[0] = "move2alf_support@xenit.eu";
		}
			
			
			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setFrom(Config.get("mail.from"));
			mail.setTo(addresses);
			mail.setSubject("Move2Alf error report");

			Job job = cycle.getSchedule().getJob();
			
			mail.setText("Cycle " + cycleId + " of job " + job.getName()
					+ " completed.\n" + "The full report can be found on "
					+ Config.get("url") + "/job/" + job.getId() + "/" + cycleId
					+ "/report" +"\n\nStatistics:" + "\nNr of files: " + processedDocuments.size()
					+ "\nNr of failed: " + amountFailed + "\n\nTime to process: " 
					+ duration + "\nStart date/time: " + startDateTime
					+ "\nTime first document loaded: " + firstDocDateTime 
					+ "\n\nSent by Move2Alf");

			logger.debug("Sending email report for cycle " + cycleId + " to "
					+ to);
			getJobService().sendMail(mail);

	}

	private Map<String, String> getEmailActionParameter(int cycleId) {
		return getJobService().getActionParameters(cycleId, EmailAction.class);
	}

	@Override
	public void cycleStart(int cycleId) {
		// do nothing
	}

}
