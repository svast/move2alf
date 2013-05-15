package eu.xenit.move2alf.core;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.ProcessedDocument;
import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;
import eu.xenit.move2alf.logic.JobService;
import akka.actor.UntypedActor;

public class ReportActor extends UntypedActor {

	public JobService getJobService() {
		return (JobService) ApplicationContextProvider.getApplicationContext().getBean("jobService");
	}
	
	@Value(value="#{'${mail.from}'}")
	private String mailFrom;
	
	@Value(value="#{'${url}'}")
	private String url;

	@Override
	public void onReceive(Object message) {
		if (message instanceof ReportMessage) {
//			ReportMessage reportMessage = (ReportMessage) message;
//			getJobService().createProcessedDocument(reportMessage.cycleId,
//					reportMessage.name, reportMessage.date,
//					reportMessage.state, reportMessage.params, reportMessage.reference);
		} else if (message instanceof SendMailMessage) {
			SendMailMessage sendMailMessage = (SendMailMessage) message;
			int cycleId = sendMailMessage.getCycleId();

			// send email report
			Map<String, String> emailParameters = getEmailActionParameter(cycleId);
			String to = emailParameters.get("emailAddressReport");
			String sendReport = emailParameters.get("sendReport");

			Cycle cycle = getJobService().getCycle(cycleId);
			if (cycle == null) {
				return;
			}
			// only send report on errors
			boolean errorsOccured = false;
			int counter = 0;
			int amountFailed = 0;
			Date firstDocDateTime = null;
			List<ProcessedDocument> processedDocuments = getJobService()
					.getProcessedDocuments(cycleId);
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

			String[] addresses = null;
			if (errorsOccured == true && to != null && !"".equals(to)
					&& "true".equals(sendReport)) {
				String[] address = to.split(",");
				int numberAddresses = address.length + 1;
				addresses = new String[numberAddresses];
				for (int i = 0; i < numberAddresses; i++) {
					try {
						addresses[i] = address[i];
					} catch (Exception e) {
						addresses[i] = "move2alf_support@xenit.eu";
					}
				}
			} else {
				addresses = new String[1];
				addresses[0] = "move2alf_support@xenit.eu";
			}

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setFrom(mailFrom);
			mail.setTo(addresses);
			mail.setSubject("Move2Alf error report");

			Job job = cycle.getJob();

			mail.setText("Cycle " + cycleId + " of job " + sendMailMessage.getJobName()
					+ " completed.\n" + "The full report can be found on "
					+ url + "/job/" + job.getId() + "/" + cycleId
					+ "/report" + "\n\nStatistics:" + "\nNr of files: "
					+ processedDocuments.size() + "\nNr of failed: "
					+ amountFailed + "\n\nTime to process: " + duration
					+ "\nStart date/time: " + startDateTime
					+ "\nTime first document loaded: " + firstDocDateTime
					+ "\n\nSent by Move2Alf");

			getJobService().sendMail(mail);
		} else {
			throw new IllegalArgumentException("Unknown message: " + message);
		}
	}

	private Map<String, String> getEmailActionParameter(int cycleId) {
		//return getJobService().getActionParameters(cycleId, EmailAction.class);
        return null;
	}
}
