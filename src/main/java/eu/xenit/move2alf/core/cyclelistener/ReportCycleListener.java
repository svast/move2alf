package eu.xenit.move2alf.core.cyclelistener;

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
		List<ProcessedDocument> processedDocuments = getJobService().getProcessedDocuments(cycleId);
		if (processedDocuments != null) {
			for (ProcessedDocument doc : processedDocuments) {
				if (EProcessedDocumentStatus.FAILED.equals(doc.getStatus())) {
					errorsOccured = true;
					break;
				}
			}
		} else {
			logger.info("No processed documents found");
		}

		if (errorsOccured == true && to != null && !"".equals(to)
				&& "true".equals(sendReport)) {
			String[] addresses = to.split(",");
			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setFrom(Config.get("mail.from"));
			mail.setTo(addresses);
			mail.setSubject("Move2Alf report");

			Job job = cycle.getSchedule().getJob();
			mail.setText("Cycle " + cycleId + " of job " + job.getName()
					+ " completed.\n" + "The full report can be found on "
					+ Config.get("url") + "/job/" + job.getId() + "/" + cycleId
					+ "/report" + "\n\nSent by Move2Alf");

			logger.debug("Sending email report for cycle " + cycleId + " to "
					+ to);
			getJobService().sendMail(mail);
		} else {
			logger
					.info("No email address or no errors found, not sending report.");
		}
	}

	private Map<String, String> getEmailActionParameter(int cycleId) {
		return getJobService().getActionParameters(cycleId, EmailAction.class);
	}

	@Override
	public void cycleStart(int cycleId) {
		// do nothing
	}

}
