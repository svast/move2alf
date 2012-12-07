package eu.xenit.move2alf.logic;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.cyclelistener.CommandCycleListener;
import eu.xenit.move2alf.core.cyclelistener.CycleListener;
import eu.xenit.move2alf.core.cyclelistener.LoggingCycleListener;
import eu.xenit.move2alf.core.cyclelistener.ReportCycleListener;
import eu.xenit.move2alf.core.cyclestate.CycleStateManager;
import eu.xenit.move2alf.core.cyclestate.StateCycleListener;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.simpleaction.SimpleAction;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.execution.ActionExecutor;
import eu.xenit.move2alf.logic.PipelineAssembler.PipelineStep;
import eu.xenit.move2alf.web.dto.JobConfig;

@Service("jobExecutionService")
@Transactional
public class JobExecutionServiceImpl extends AbstractHibernateService
        implements JobExecutionService {

	private static final Logger logger = LoggerFactory
			.getLogger(JobExecutionServiceImpl.class);

	private JobService jobService;

	private PipelineAssembler pipelineAssembler;

	private final List<CycleListener> cycleListeners = new ArrayList<CycleListener>();

    private CycleStateManager stateManager;

	private Map<Integer, List<PipelineStepProgress>> progress = new ConcurrentHashMap<Integer, List<PipelineStepProgress>>();

	@Autowired
	public void setJobService(final JobService jobService) {
		this.jobService = jobService;
	}

	public JobService getJobService() {
		return jobService;
	}

	@Autowired
	public void setPipelineAssembler(final PipelineAssembler pipelineAssembler) {
		this.pipelineAssembler = pipelineAssembler;
	}

	public PipelineAssembler getPipelineAssembler() {
		return pipelineAssembler;
	}

    @Autowired
    public void setStateManager(CycleStateManager stateManager) {
        this.stateManager = stateManager;
    }

	@PostConstruct
	public void init() {
		registerCycleListener(new LoggingCycleListener());
		registerCycleListener(new CommandCycleListener());
		// removed MoveCycleListener
		registerCycleListener(new ReportCycleListener());
        registerCycleListener(new StateCycleListener(this.stateManager));
	}

    @Override
	public void registerCycleListener(final CycleListener listener) {
		listener.setJobService(getJobService());
		this.cycleListeners.add(listener);
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void executeJobSteps(final Job job, final Cycle cycle) {
		// get jobconfig
		final JobConfig jobConfig = getPipelineAssembler().getJobConfigForJob(
				job.getId());
		final List<PipelineStep> pipeline = getPipelineAssembler().getPipeline(
				jobConfig);

		// execute job...
		final List<String> inputFolders = jobConfig.getInputFolder();
		List<FileInfo> input = new ArrayList<FileInfo>();
		for (final String inputFolder : inputFolders) {
			final FileInfo inputMap = new FileInfo();
			inputMap.put(Parameters.PARAM_FILE, new File(inputFolder));
			input.add(inputMap);
		}
		if ("true".equals(jobConfig.getMoveNotLoad())) {
			final FileInfo inputMap = new FileInfo();
			inputMap.put(Parameters.PARAM_FILE,
					new File(jobConfig.getMoveNotLoadText()));
			input.add(inputMap);
		}

		final List<PipelineStepProgress> cycleProgress = new ArrayList<PipelineStepProgress>();
		for(final PipelineStep step : pipeline) {
			cycleProgress.add(new PipelineStepProgress(step.getAction(), 0, -1));
		}
		this.progress.put(cycle.getId(), cycleProgress);

		final Map<String, Serializable> state = this.stateManager.getState(cycle.getId());
		for (final PipelineStep step : pipeline) {
			input = executePipelineStep(step, input, jobConfig, cycle, state);
		}

		this.progress.remove(cycle.getId());
	}

	public List<PipelineStepProgress> getProgress(Integer cycleId) {
		return this.progress.get(cycleId);
	}

	private List<FileInfo> executePipelineStep(final PipelineStep step,
			final List<FileInfo> input, final JobConfig jobConfig,
			final Cycle cycle, Map<String, Serializable> state) {
		final SimpleAction action = step.getAction();
		final ActionConfig config = step.getConfig();
		final ActionExecutor executor = step.getExecutor();

		final SuccessHandler successHandler = step.getSuccessHandler();
		final ErrorHandler errorHandler = step.getErrorHandler();

		final Date start = new Date();
		final int numberOfInputFiles = input.size();
		logger.info("STEP: " + action.getClass().toString());
		logger.info(" * INPUT: " + numberOfInputFiles + " files");

		PipelineStepProgress currentProgress = null;
		for(PipelineStepProgress stepProgress : getProgress(cycle.getId())) {
			if (stepProgress.getAction().equals(action)) {
				currentProgress = stepProgress;
			}
		}
		currentProgress.setTotal(numberOfInputFiles);

		final List<FileInfo> output = executor.execute(input, jobConfig, cycle,
				action, config, successHandler, errorHandler, state, currentProgress);

		final Date stop = new Date();
		final long time = stop.getTime() - start.getTime();
		logger.info(" * OUTPUT: " + output.size() + " files in " + time
				+ " ms - " + new Float(numberOfInputFiles) / time * 1000
				+ " input files / sec");
		return output;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void closeCycle(final Cycle cycle) {
		final Session session = getSessionFactory().getCurrentSession();

		cycle.setEndDateTime(new Date());
		session.update(cycle);

		notifyCycleListenersEnd(cycle.getId());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Cycle openCycleForJob(final Integer jobId) {
		final Session session = getSessionFactory().getCurrentSession();

		final Job job = jobService.getJob(jobId);
		logger.debug("Executing job \"" + job.getName() + "\"");

		final Cycle cycle = new Cycle();
		cycle.setJob(job);
		cycle.setStartDateTime(new Date());
		session.save(cycle);

		notifyCycleListenersStart(cycle.getId(), new HashMap<String, Object>());

		return cycle;
	}

	private void notifyCycleListenersStart(final int cycleId,
			final Map<String, Object> parameterMap) {
		for (final CycleListener listener : this.cycleListeners) {
			listener.cycleStart(cycleId, parameterMap);
		}
	}

	private void notifyCycleListenersEnd(final int cycleId) {
		for (final CycleListener listener : this.cycleListeners) {
			listener.cycleEnd(cycleId);
		}
	}

}
