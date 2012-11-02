package eu.xenit.move2alf.core.simpleaction.helpers;

import static eu.xenit.move2alf.common.Parameters.PARAM_COUNTER;
import static eu.xenit.move2alf.common.Parameters.PARAM_ERROR_MESSAGE;
import static eu.xenit.move2alf.common.Parameters.PARAM_RELATIVE_PATH;
import static eu.xenit.move2alf.common.Parameters.PARAM_STATUS;
import static eu.xenit.move2alf.common.Parameters.VALUE_FAILED;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;

import akka.actor.ActorRef;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.ConfiguredObject;
import eu.xenit.move2alf.core.action.Action;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.ProcessedDocument;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.dto.Schedule;
import eu.xenit.move2alf.core.enums.ECycleState;
import eu.xenit.move2alf.core.enums.EDestinationParameter;
import eu.xenit.move2alf.core.simpleaction.SimpleAction;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.sourcesink.SourceSink;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.web.dto.HistoryInfo;
import eu.xenit.move2alf.web.dto.JobInfo;

@SuppressWarnings("deprecation")
public class SimpleActionWrapper extends SimpleAction {

	private final Action action;
	private final CollectingJobService cjs;

	private static final Logger logger = LoggerFactory
			.getLogger(SimpleActionWrapper.class);

	public SimpleActionWrapper(final Action action) {
		this.action = action;
		cjs = new CollectingJobService();
	}

	@Override
	public List<FileInfo> execute(final FileInfo parameterMap,
			final ActionConfig config, final Map<String, Serializable> state) {
		// Map<String, Object> newParameterMap = new HashMap<String,
		// Object>(parameterMap);
		parameterMap.put(PARAM_COUNTER, new CountDownLatch(0));
		// some old actions expect PARAM_COUNTER to be present, it's added here
		// to prevent NPEs

		final String relativePath = parameterMap.get(PARAM_RELATIVE_PATH) + "/";
		parameterMap.put(PARAM_RELATIVE_PATH, relativePath);

		final ConfiguredAction configuredAction = new ConfiguredAction();
		configuredAction.setParameters(config);
		// trick the action so it thinks there is a next action to execute
		configuredAction
				.setAppliedConfiguredActionOnSuccess(new ConfiguredAction());

		List<FileInfo> results = null;

		synchronized (action) {
			action.setJobService(cjs);
			action.execute(configuredAction, parameterMap);
			results = cjs.getResults();
		}

		// The results for this thread need to be erased because the thread is
		// reused. Otherwise files can be loaded multiple times.
		cjs.eraseResults();
		return results;
	}

	static class CollectingJobService implements JobService {

		// We don't want threads interfering with each other results.
		private final ThreadLocal<List<FileInfo>> results;

		public CollectingJobService() {
			results = new ThreadLocal<List<FileInfo>>();
		}

		public void eraseResults() {
			results.remove();
		}

		public List<FileInfo> getResults() {
			logger.debug("Returning Copy");
			if (results.get() != null) {
				return new ArrayList<FileInfo>(results.get());
			} else {
				logger.debug("No results for this iteration");
			}
			return new ArrayList<FileInfo>();
		}

		// Collect calls to executeAction and add to output
		@Override
		public void executeAction(final int cycleId,
				final ConfiguredAction action,
				final Map<String, Object> parameterMap) {
			final FileInfo fileInfo = new FileInfo();
			fileInfo.putAll(parameterMap);
			if (VALUE_FAILED.equals(fileInfo.get(PARAM_STATUS))) {
				logger.debug("STATUS FAILED");
				throw new Move2AlfException(
						(String) fileInfo.get(PARAM_ERROR_MESSAGE));
			} else {
				logger.debug("Adding fileInfo to result list");
				if (results.get() == null) {
					results.set(new ArrayList<FileInfo>());
				}
				this.results.get().add(fileInfo);
				logger.debug("Number of results: {}", results.get().size());
			}
		}

		// //////////////////////////////////////////////////////////////
		// OTHER METHODS NOT IMPLEMENTED
		// make sure the wrapped action doesn't use the jobservice!
		// //////////////////////////////////////////////////////////////

		@Override
		public void addSourceSinkToAction(final ConfiguredAction action,
				final ConfiguredSourceSink sourceSink) {
		}

		@Override
		public boolean checkDestinationExists(final String destinationName) {
			return false;
		}

		@Override
		public boolean checkJobExists(final String jobName) {
			return false;
		}

		@Override
		public void createAction(final String className,
				final Map<String, String> parameters) {
		}

		@Override
		public ConfiguredSourceSink createDestination(
				final String destinationType,
				final HashMap<EDestinationParameter, Object> destinationParams) {
			return null;
		}

		@Override
		public Job createJob(final String name, final String description) {
			return null;
		}

		@Override
		public void createProcessedDocument(final int cycleId,
				final String name, final Date date, final String state,
				final Set<ProcessedDocumentParameter> params) {
		}

		@Override
		public Schedule createSchedule(final int jobId, final String cronJob) {
			return null;
		}

		@Override
		public void createSourceSink(final String className,
				final Map<String, String> parameters) {
		}

		@Override
		public void deleteAction(final int id) {
		}

		@Override
		public void deleteDestination(final int id) {
		}

		@Override
		public void deleteJob(final int id) {
		}

		@Override
		public void deleteSchedule(final int scheduleId) {
		}

		@Override
		public ConfiguredSourceSink editDestination(final int sinkId,
				final String destinationType,
				final HashMap<EDestinationParameter, Object> destinationParams) {
			return null;
		}

		@Override
		public Job editJob(final int id, final String name,
				final String description) {
			return null;
		}

		@Override
		public Map<String, String> getActionParameters(final int cycleId,
				final Class<? extends Action> clazz) {
			return null;
		}

		@Override
		public ConfiguredAction getActionRelatedToConfiguredSourceSink(
				final int sourceSinkId) {
			return null;
		}

		@Override
		public List<Action> getActionsByCategory(final String category) {
			return null;
		}

		@Override
		public List<ConfiguredSourceSink> getAllConfiguredSourceSinks() {
			return null;
		}

		@Override
		public List<ConfiguredSourceSink> getAllDestinationConfiguredSourceSinks() {
			return null;
		}

		@Override
		public List<Job> getAllJobs() {
			return null;
		}

		@Override
		public ConfiguredObject getConfiguredSourceSink(final int sourceSinkId) {
			return null;
		}

		@Override
		public List<String> getCronjobsForJob(final int jobId) {
			return null;
		}

		@Override
		public Cycle getCycle(final int cycleId) {
			return null;
		}

		@Override
		public List<Cycle> getCyclesForJob(final int jobId) {
			return null;
		}

		@Override
		public List<Cycle> getCyclesForJobDesc(final int jobId) {
			return null;
		}

		@Override
		public ConfiguredSourceSink getDestination(final int sinkId) {
			return null;
		}

		@Override
		public List<HistoryInfo> getHistory(final int jobId) {
			return null;
		}

		@Override
		public Job getJob(final int id) {
			return null;
		}

		@Override
		public ECycleState getJobState(final int jobId) {
			return null;
		}

		@Override
		public Cycle getLastCycleForJob(final Job job) {
			return null;
		}

		@Override
		public List<ProcessedDocument> getProcessedDocuments(final int cycleId) {
			return null;
		}

		@Override
		public ActorRef getReportActor() {
			return null;
		}

		@Override
		public Schedule getSchedule(final int scheduleId) {
			return null;
		}

		@Override
		public int getScheduleId(final int jobId, final String cronJob) {
			return 0;
		}

		@Override
		public List<Schedule> getSchedulesForJob(final int jobId) {
			return null;
		}

		@Override
		public List<SourceSink> getSourceSinksByCategory(final String category) {
			return null;
		}

		@Override
		public void resetCycles() {
		}

		@Override
		public void sendMail(final SimpleMailMessage message) {
		}

		@Override
		public void scheduleNow(final int jobId) {
		}

		@Override
		public List<ProcessedDocument> getProcessedDocuments(final int cycleId,
				final int first, final int count) {
			return null;
		}

		@Override
		public long countProcessedDocuments(final int cycleId) {
			return 0;
		}

		@Override
		public long countProcessedDocumentsWithStatus(final int cycleId, EProcessedDocumentStatus status) {
			return 0;
		}

		@Override
		public List<JobInfo> getAllJobInfo() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
