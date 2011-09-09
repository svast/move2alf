package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.core.CycleListener;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;

public interface JobExecutionService {

	void registerCycleListener(CycleListener listener);

	Cycle openCycleForSchedule(Integer scheduleId);

	void closeCycle(Cycle cycle);

	void executeJobSteps(Job job, Cycle cycle);

}
