package eu.xenit.move2alf.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.annotation.PostConstruct;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.CycleListener;
import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.core.cyclelistener.CommandCycleListener;
import eu.xenit.move2alf.core.cyclelistener.LoggingCycleListener;
import eu.xenit.move2alf.core.cyclelistener.ReportCycleListener;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.dto.Schedule;
import eu.xenit.move2alf.core.enums.EScheduleState;
import eu.xenit.move2alf.core.simpleaction.SAMoveBeforeProcessing;
import eu.xenit.move2alf.core.simpleaction.SimpleAction;
import eu.xenit.move2alf.core.simpleaction.execution.ActionExecutor;
import eu.xenit.move2alf.core.simpleaction.execution.SingleThreadExecutor;
import eu.xenit.move2alf.core.simpleaction.execution.ThreadedExecutor;
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

	private ErrorHandler errorHandler = new ErrorHandler();

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
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void executeJobSteps(Job job, Cycle cycle) {
		// get jobconfig
		JobConfig jobConfig = getPipelineAssembler().getJobConfigForJob(
				job.getId());
		List<PipelineStep> pipeline = getPipelineAssembler().getPipeline(
				jobConfig);

		// execute job...
		List<String> inputFolders = jobConfig.getInputFolder();
		List<Map<String, Object>> input = new ArrayList<Map<String, Object>>();
		for (String inputFolder : inputFolders) {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put(Parameters.PARAM_FILE, new File(inputFolder));
			input.add(inputMap);
		}
		if ("true".equals(jobConfig.getMoveNotLoad())) {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put(Parameters.PARAM_FILE, new File(jobConfig
					.getNotLoadPath()));
			input.add(inputMap);
		}

		for (PipelineStep step : pipeline) {
			input = executePipelineStep(step, input, jobConfig, cycle);
		}

		for (Map<String, Object> successFullFile : input) {
			handleSuccess(successFullFile, jobConfig, cycle);
		}
	}

	// TODO: make multithreaded by passing executor ...
	private List<Map<String, Object>> executePipelineStep(PipelineStep step,
			List<Map<String, Object>> input, JobConfig jobConfig, Cycle cycle) {
		SimpleAction action = step.getAction();
		Map<String, String> config = step.getConfig();
		ActionExecutor executor = step.getExecutor();

		logger.debug("STEP: " + action.getClass().toString());
		logger.debug(" * INPUT: " + input.size() + " files");

		List<Map<String, Object>> output = executor.execute(input, jobConfig, cycle, action, config, errorHandler);

		logger.debug(" * OUTPUT: " + output.size() + " files");
		return output;
	}

	public class ErrorHandler {
		public void handleError(Map<String, Object> parameterMap, JobConfig jobConfig,
				Cycle cycle, Exception e) {
			// TODO: handle cleaner?
			File file = (File) parameterMap.get(Parameters.PARAM_FILE);

			// reporting
			Set<ProcessedDocumentParameter> params = new HashSet<ProcessedDocumentParameter>();
			ProcessedDocumentParameter msg = new ProcessedDocumentParameter();
			msg.setName(Parameters.PARAM_ERROR_MESSAGE);
			msg.setValue(e.getClass().getName() + ": " + e.getMessage());
			// Report everything using the first (deprecated) ConfiguredAction
			// of
			// the job.
			msg.setConfiguredAction(cycle.getSchedule().getJob()
					.getFirstConfiguredAction());
			params.add(msg);
			getJobService().getReportActor().sendOneWay(
					new ReportMessage(cycle.getId(), file.getName(),
							new Date(), Parameters.VALUE_FAILED, params));

			// move
			if ("true".equals(jobConfig.getMoveNotLoad())) {
				String inputFolder = (String) parameterMap
						.get(Parameters.PARAM_INPUT_PATH);
				Util.moveFile(inputFolder, jobConfig.getNotLoadPath(), file);
			}
		}
	}

	private void handleSuccess(Map<String, Object> parameterMap,
			JobConfig jobConfig, Cycle cycle) {
		File file = (File) parameterMap.get(Parameters.PARAM_FILE);

		// reporting
		Set<ProcessedDocumentParameter> params = createProcessedDocumentParameterSet(
				(Map<String, String>) parameterMap
						.get(Parameters.PARAM_REPORT_FIELDS), cycle
						.getSchedule().getJob().getFirstConfiguredAction());
		getJobService().getReportActor().sendOneWay(
				new ReportMessage(cycle.getId(), file.getName(), new Date(),
						Parameters.VALUE_OK, params));

		// move
		if ("true".equals(jobConfig.getMoveAfterLoad())) {
			String inputFolder = (String) parameterMap
					.get(Parameters.PARAM_INPUT_PATH);
			Util.moveFile(inputFolder, jobConfig.getAfterLoadPath(), file);
		}
	}

	private Set<ProcessedDocumentParameter> createProcessedDocumentParameterSet(
			Map<String, String> reportFields, ConfiguredAction configuredAction) {
		Set<ProcessedDocumentParameter> procDocParameters = new HashSet<ProcessedDocumentParameter>();
		if (reportFields != null) {
			for (String key : reportFields.keySet()) {
				ProcessedDocumentParameter procDocParameter = new ProcessedDocumentParameter();
				procDocParameter.setName(key);
				String value = reportFields.get(key);
				if (value.length() > 255) {
					value = value.substring(0, 255);
				}
				procDocParameter.setValue(value);
				procDocParameter.setConfiguredAction(configuredAction);
				procDocParameters.add(procDocParameter);
			}
		}
		return procDocParameters;
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
