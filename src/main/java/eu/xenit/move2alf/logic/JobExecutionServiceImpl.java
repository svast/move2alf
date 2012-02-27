package eu.xenit.move2alf.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.CycleListener;
import eu.xenit.move2alf.core.cyclelistener.CommandCycleListener;
import eu.xenit.move2alf.core.cyclelistener.LoggingCycleListener;
import eu.xenit.move2alf.core.cyclelistener.ReportCycleListener;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.dto.Schedule;
import eu.xenit.move2alf.core.enums.EScheduleState;
import eu.xenit.move2alf.core.simpleaction.SimpleAction;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.execution.ActionExecutor;
import eu.xenit.move2alf.logic.PipelineAssembler.PipelineStep;
import eu.xenit.move2alf.web.dto.JobConfig;

@Service("jobExecutionService")
@Transactional
public class JobExecutionServiceImpl extends AbstractHibernateService implements
		JobExecutionService {

	private static final Logger logger = LoggerFactory
			.getLogger(JobExecutionServiceImpl.class);

	private JobService jobService;

	private PipelineAssembler pipelineAssembler;

	private List<CycleListener> cycleListeners = new ArrayList<CycleListener>();

	@Autowired
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	public JobService getJobService() {
		return jobService;
	}

	@Autowired
	public void setPipelineAssembler(PipelineAssembler pipelineAssembler) {
		this.pipelineAssembler = pipelineAssembler;
	}

	public PipelineAssembler getPipelineAssembler() {
		return pipelineAssembler;
	}

	@PostConstruct
	public void init() {
		registerCycleListener(new LoggingCycleListener());
		registerCycleListener(new CommandCycleListener());
		// removed MoveCycleListener
		registerCycleListener(new ReportCycleListener());
	}

	@Override
	public void registerCycleListener(CycleListener listener) {
		listener.setJobService(getJobService());
		this.cycleListeners.add(listener);
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void executeJobSteps(Job job, Cycle cycle) {
		// get jobconfig
		JobConfig jobConfig = getPipelineAssembler().getJobConfigForJob(
				job.getId());
		List<PipelineStep> pipeline = getPipelineAssembler().getPipeline(
				jobConfig);

		// execute job...
		List<String> inputFolders = jobConfig.getInputFolder();
		List<FileInfo> input = new ArrayList<FileInfo>();
		for (String inputFolder : inputFolders) {
			FileInfo inputMap = new FileInfo();
			inputMap.put(Parameters.PARAM_FILE, new File(inputFolder));
			input.add(inputMap);
		}
		if ("true".equals(jobConfig.getMoveNotLoad())) {
			FileInfo inputMap = new FileInfo();
			inputMap.put(Parameters.PARAM_FILE, new File(jobConfig
					.getNotLoadPath()));
			input.add(inputMap);
		}

		for (PipelineStep step : pipeline) {
			input = executePipelineStep(step, input, jobConfig, cycle);
		}
	}

	// TODO: make multithreaded by passing executor ...
	private List<FileInfo> executePipelineStep(PipelineStep step,
			List<FileInfo> input, JobConfig jobConfig, Cycle cycle) {
		SimpleAction action = step.getAction();
		ActionConfig config = step.getConfig();
		ActionExecutor executor = step.getExecutor();
		
		SuccessHandler successHandler = step.getSuccessHandler();
		ErrorHandler errorHandler = step.getErrorHandler();

		Date start = new Date();
		int numberOfInputFiles = input.size();
		logger.info("STEP: " + action.getClass().toString());
		logger.info(" * INPUT: " + numberOfInputFiles + " files");

		List<FileInfo> output = executor.execute(input, jobConfig, cycle,
				action, config, successHandler, errorHandler);

		Date stop = new Date();
		long time = stop.getTime() - start.getTime();
		logger.info(" * OUTPUT: " + output.size() + " files in " + time
				+ " ms - " + new Float(numberOfInputFiles) / time * 1000
				+ " input files / sec");
		return output;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void closeCycle(Cycle cycle) {
		Session session = getSessionFactory().getCurrentSession();

		Schedule schedule = cycle.getSchedule();
		schedule.setState(EScheduleState.NOT_RUNNING);
		session.update(schedule);

		cycle.setEndDateTime(new Date());
		session.update(cycle);

		notifyCycleListenersEnd(cycle.getId());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Cycle openCycleForSchedule(Integer scheduleId) {
		Session session = getSessionFactory().getCurrentSession();

		Schedule schedule = getJobService().getSchedule(scheduleId);
		Job job = schedule.getJob();
		logger.debug("Executing job \"" + job.getName() + "\"");

		Cycle cycle = new Cycle();
		cycle.setSchedule(schedule);
		cycle.setStartDateTime(new Date());
		session.save(cycle);

		schedule.setState(EScheduleState.RUNNING);
		session.update(schedule);

		notifyCycleListenersStart(cycle.getId(), new HashMap<String, Object>());

		return cycle;
	}

	private void notifyCycleListenersStart(int cycleId,
			Map<String, Object> parameterMap) {
		for (CycleListener listener : this.cycleListeners) {
			listener.cycleStart(cycleId, parameterMap);
		}
	}

	private void notifyCycleListenersEnd(int cycleId) {
		for (CycleListener listener : this.cycleListeners) {
			listener.cycleEnd(cycleId);
		}
	}

}
