package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.core.cyclelistener.CycleListener;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;

import java.util.List;

public interface JobExecutionService {

	void registerCycleListener(CycleListener listener);

	Cycle openCycleForJob(Integer jobId);

	void closeCycle(Cycle cycle);

	void executeJobSteps(Job job, Cycle cycle);

	List<PipelineStepProgress> getProgress(Integer cycleId);

}
