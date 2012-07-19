package eu.xenit.move2alf.core.cyclelistener;

import eu.xenit.move2alf.core.CycleListener;
import eu.xenit.move2alf.core.SendMailMessage;
import eu.xenit.move2alf.core.dto.Cycle;

public class ReportCycleListener extends CycleListener {

	@Override
	public void cycleEnd(int cycleId) {
		Cycle cycle = getJobService().getCycle(cycleId);
		String jobName = cycle.getJob().getName();
		getJobService().getReportActor().sendOneWay(
				new SendMailMessage(cycleId, jobName));
	}

	@Override
	public void cycleStart(int cycleId) {
		// do nothing
	}

}
