package eu.xenit.move2alf.core.cyclelistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;

import eu.xenit.move2alf.common.Config;
import eu.xenit.move2alf.core.CycleListener;
import eu.xenit.move2alf.core.action.EmailAction;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;

public class ReportCycleListener extends CycleListener {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportCycleListener.class);

	@Override
	public void cycleEnd(int cycleId) {
		// send email report
		String to = getToAddress(cycleId);
		if (to != null && !"".equals(to)) {
			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setFrom(Config.get("mail.from"));
			mail.setTo(to);
			mail.setSubject("Move2Alf report");
			Cycle cycle = getJobService().getCycle(cycleId);
			Job job = cycle.getSchedule().getJob();
			mail.setText("Cycle " + cycleId + " of job " + job.getName()
					+ " completed.\n" + "The full report can be found on "
					+ Config.get("url") + "/job/" + job.getId() + "/" + cycleId
					+ "/report" + "\n\nSent by Move2Alf");

			logger.debug("Sending email report for cycle " + cycleId + " to "
					+ to);
			getJobService().sendMail(mail);
		} else {
			logger.info("No email address found, not sending report.");
		}
	}

	private String getToAddress(int cycleId) {
		Cycle cycle = getJobService().getCycle(cycleId);
		ConfiguredAction action = cycle.getSchedule().getJob()
				.getFirstConfiguredAction();
		while (action != null) {
			if (EmailAction.class.getName().equals(action.getClassName())) {
				return action.getParameter("emailAddressReport");
			}
			action = action.getAppliedConfiguredActionOnSuccess();
		}
		return null;
	}

	@Override
	public void cycleStart(int cycleId) {
		// do nothing
	}

}
