package eu.xenit.move2alf.core;


import eu.xenit.move2alf.logic.JobService;
import akka.actor.UntypedActor;

public class ReportActor extends UntypedActor {

	public JobService getJobService() {
		return (JobService) ApplicationContextProvider.getApplicationContext().getBean("jobService");
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ReportMessage) {
			ReportMessage reportMessage = (ReportMessage) message;
			getJobService().createProcessedDocument(reportMessage.cycleId,
					reportMessage.name, reportMessage.date,
					reportMessage.state, reportMessage.params);
		} else {
		      throw new IllegalArgumentException("Unknown message: " + message);
		}
	}
}
