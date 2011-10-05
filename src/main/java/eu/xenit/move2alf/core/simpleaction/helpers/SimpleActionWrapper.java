package eu.xenit.move2alf.core.simpleaction.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;

import akka.actor.ActorRef;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfiguredObject;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.ProcessedDocument;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.dto.Schedule;
import eu.xenit.move2alf.core.enums.EScheduleState;
import eu.xenit.move2alf.core.simpleaction.SimpleAction;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.web.dto.HistoryInfo;

public class SimpleActionWrapper extends SimpleAction {

	private List<FileInfo> output;
	private final Action action;
	
	private static final Logger logger = LoggerFactory.getLogger(SimpleActionWrapper.class);

	public SimpleActionWrapper(Action action) {
		this.action = action;
	}

	@Override
	public List<FileInfo> execute(
			final FileInfo parameterMap,
			final ActionConfig config) {
		output = new ArrayList<FileInfo>();
		//Map<String, Object> newParameterMap = new HashMap<String, Object>(parameterMap);
		parameterMap.put(Parameters.PARAM_CYCLE, 0); // hack
		parameterMap.put(Parameters.PARAM_COUNTER, new CountDownLatch(0));
		// some old actions except PARAM_COUNTER to be present, it's added here to prevent NPEs
		
		ConfiguredAction configuredAction = new ConfiguredAction();
		configuredAction.setParameters(config);
		// trick the action so it thinks there is a next action to execute
		configuredAction.setAppliedConfiguredActionOnSuccess(new ConfiguredAction());

		action.setJobService(new CollectingJobService());
		action.execute(configuredAction, parameterMap);
		return output;
	}

	class CollectingJobService implements JobService {
		// Collect calls to executeAction and add to output
		@Override
		public void executeAction(int cycleId, ConfiguredAction action,
				Map<String, Object> parameterMap) {
			FileInfo fileInfo = new FileInfo();
			fileInfo.putAll(parameterMap);
			output.add(fileInfo);
		}

		// //////////////////////////////////////////////////////////////
		// OTHER METHODS NOT IMPLEMENTED
		// make sure the wrapped action doesn't use the jobservice!
		// //////////////////////////////////////////////////////////////

		@Override
		public void addSourceSinkToAction(ConfiguredAction action,
				ConfiguredSourceSink sourceSink) {
		}

		@Override
		public boolean checkDestinationExists(String destinationName) {
			return false;
		}

		@Override
		public boolean checkJobExists(String jobName) {
			return false;
		}

		@Override
		public void createAction(String className,
				Map<String, String> parameters) {
		}

		@Override
		public ConfiguredSourceSink createDestination(String destinationType,
				HashMap destinationParams) {
			return null;
		}

		@Override
		public Job createJob(String name, String description) {
			return null;
		}

		@Override
		public void createProcessedDocument(int cycleId, String name,
				Date date, String state, Set<ProcessedDocumentParameter> params) {
		}

		@Override
		public Schedule createSchedule(int jobId, String cronJob) {
			return null;
		}

		@Override
		public void createSourceSink(String className,
				Map<String, String> parameters) {
		}

		@Override
		public void deleteAction(int id) {
		}

		@Override
		public void deleteDestination(int id) {
		}

		@Override
		public void deleteJob(int id) {
		}

		@Override
		public void deleteSchedule(int scheduleId) {
		}

		@Override
		public ConfiguredSourceSink editDestination(int sinkId,
				String destinationType, HashMap destinationParams) {
			return null;
		}

		@Override
		public Job editJob(int id, String name, String description) {
			return null;
		}

		@Override
		public Map<String, String> getActionParameters(int cycleId,
				Class<? extends Action> clazz) {
			return null;
		}

		@Override
		public ConfiguredAction getActionRelatedToConfiguredSourceSink(
				int sourceSinkId) {
			return null;
		}

		@Override
		public List<Action> getActionsByCategory(String category) {
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
		public ConfiguredObject getConfiguredSourceSink(int sourceSinkId) {
			return null;
		}

		@Override
		public List<String> getCronjobsForJob(int jobId) {
			return null;
		}

		@Override
		public Cycle getCycle(int cycleId) {
			return null;
		}

		@Override
		public List<Cycle> getCyclesForJob(int jobId) {
			return null;
		}

		@Override
		public List<Cycle> getCyclesForJobDesc(int jobId) {
			return null;
		}

		@Override
		public ConfiguredSourceSink getDestination(int sinkId) {
			return null;
		}

		@Override
		public String getDuration(Date startDateTime, Date endDateTime) {
			return null;
		}

		@Override
		public List<HistoryInfo> getHistory(int jobId) {
			return null;
		}

		@Override
		public Job getJob(int id) {
			return null;
		}

		@Override
		public EScheduleState getJobState(int jobId) {
			return null;
		}

		@Override
		public Cycle getLastCycleForJob(Job job) {
			return null;
		}

		@Override
		public List<ProcessedDocument> getProcessedDocuments(int cycleId) {
			return null;
		}

		@Override
		public ActorRef getReportActor() {
			return null;
		}

		@Override
		public Schedule getSchedule(int scheduleId) {
			return null;
		}

		@Override
		public int getScheduleId(int jobId, String cronJob) {
			return 0;
		}

		@Override
		public List<Schedule> getSchedulesForJob(int jobId) {
			return null;
		}

		@Override
		public List<SourceSink> getSourceSinksByCategory(String category) {
			return null;
		}

		@Override
		public void resetSchedules() {
		}

		@Override
		public void sendMail(SimpleMailMessage message) {
		}
	}
}