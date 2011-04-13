package eu.xenit.move2alf.core.cyclelistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;

import eu.xenit.move2alf.common.Config;
import eu.xenit.move2alf.core.CycleListener;

public class ReportCycleListener extends CycleListener {
	
	private static final Logger logger = LoggerFactory
	.getLogger(ReportCycleListener.class);

	@Override
	public void cycleEnd(int cycleId) {
		// send email report
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setFrom(Config.get("mail.from"));
		mail.setTo("jonas.heylen@xenit.eu");
		mail.setSubject("Move2Alf report");
		mail.setText("Cycle " + cycleId + " completed");
		logger.debug("Sending email report");
		getJobService().sendMail(mail);
	}

	@Override
	public void cycleStart(int cycleId) {
		// do nothing
	}

}
