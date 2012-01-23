package eu.xenit.move2alf.web.controller;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.ConfiguredObject;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.SourceSinkFactory;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.ProcessedDocument;
import eu.xenit.move2alf.core.enums.EDestinationParameter;
import eu.xenit.move2alf.logic.AbstractHibernateService;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.logic.PipelineAssembler;
import eu.xenit.move2alf.logic.SchedulerImpl;
import eu.xenit.move2alf.logic.UsageService;
import eu.xenit.move2alf.logic.UserService;
import eu.xenit.move2alf.web.dto.DestinationConfig;
import eu.xenit.move2alf.web.dto.DestinationInfo;
import eu.xenit.move2alf.web.dto.EditDestinationConfig;
import eu.xenit.move2alf.web.dto.HistoryInfo;
import eu.xenit.move2alf.web.dto.JobConfig;
import eu.xenit.move2alf.web.dto.JobInfo;
import eu.xenit.move2alf.web.dto.ScheduleConfig;

@Controller
public class JobController {

	private static final Logger logger = LoggerFactory
			.getLogger(JobController.class);

	private JobService jobService;
	private UserService userService;
	private PipelineAssembler pipelineAssembler;
	private SourceSinkFactory sourceSinkFactory;
	private UsageService usageService;

	@Autowired
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	public JobService getJobService() {
		return jobService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserService getUserService() {
		return userService;
	}

	@Autowired
	public void setPipelineAssembler(PipelineAssembler pipelineAssembler) {
		this.pipelineAssembler = pipelineAssembler;
	}

	public PipelineAssembler getPipelineAssembler() {
		return pipelineAssembler;
	}

	@Autowired
	public void setSourceSinkFactory(SourceSinkFactory sourceSinkFactory) {
		this.sourceSinkFactory = sourceSinkFactory;
	}

	public SourceSinkFactory getSourceSinkFactory() {
		return sourceSinkFactory;
	}
	
	@Autowired
	public void setUsageService(UsageService usageService) {
		this.usageService = usageService;
	}
	
	public UsageService getUsageService() {
		return this.usageService;
	}

	@RequestMapping("/job/dashboard")
	public ModelAndView dashboard() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("jobInfoList", getJobService().getAllJobInfo());
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		
		// license info
		mav.addObject("licenseIsValid", getUsageService().isValid());
		mav.addObject("licenseValidationFailureCause", getUsageService().getValidationFailureCause());
		mav.addObject("licensee", getUsageService().getLicensee());
		mav.addObject("expirationDate", getUsageService().getExpirationDate());
		
		mav.setViewName("dashboard");
		return mav;
	}

	@RequestMapping(value = "/job/create", method = RequestMethod.GET)
	public ModelAndView createJobForm() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("job", new JobConfig());
		mav.addObject("destinations", getJobService()
				.getAllDestinationConfiguredSourceSinks());
		mav.addObject("metadataOptions", getJobService().getActionsByCategory(
				ConfigurableObject.CAT_METADATA));
		mav.addObject("transformOptions", getJobService().getActionsByCategory(
				ConfigurableObject.CAT_TRANSFORM));
		mav.addObject("destinationOptions", getJobService()
				.getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("create-job");
		return mav;
	}

	@RequestMapping(value = "/job/create", method = RequestMethod.POST)
	public ModelAndView createJob(@ModelAttribute("job") @Valid JobConfig job,
			BindingResult errors) {

		boolean jobExists = getJobService().checkJobExists(job.getName());
		boolean destinationExists = false;
		boolean doubleNewDestination = false;
		List<String> sourceSinks = job.getSourceSink();
		Set<String> uniqueSourceSinks = new HashSet();
		boolean notNull = true;
		boolean threadsIsInteger = true;
		if (sourceSinks != null) {
			for (int i = 0; i < sourceSinks.size(); i++) {
				String[] sourceSinkElements = sourceSinks.get(i).split("\\|");
				for (int j = 0; j < sourceSinkElements.length; j++) {
					if ("".equals(sourceSinkElements[j])
							|| null == sourceSinkElements[j])
						notNull = false;
				}
				destinationExists = getJobService().checkDestinationExists(
						sourceSinkElements[0]);
				// check whether any of the new source sinks have the same name
				// (should then throw error)
				boolean testBoolean = uniqueSourceSinks
						.add(sourceSinkElements[0]);
				if (testBoolean == false) {
					doubleNewDestination = true;
				}

				try {
					Integer.parseInt(sourceSinkElements[4]);
				} catch (NumberFormatException e) {
					threadsIsInteger = false;
				}
			}
		}

		List<String> metadata = job.getParamMetadata();
		Set<String> uniqueMetadataNames = new HashSet();
		boolean doubleMetadata = false;
		if (metadata != null) {
			for (int i = 0; i < metadata.size(); i++) {
				String[] metadataElements = metadata.get(i).split("\\|");
				if (metadataElements != null) {
					boolean metadataUnique = uniqueMetadataNames
							.add(metadataElements[0]);
					if (metadataUnique == false) {
						doubleMetadata = true;
					}
				}
			}
		}

		List<String> transform = job.getParamTransform();
		Set<String> uniqueTransformNames = new HashSet();
		boolean doubleTransform = false;
		if (transform != null) {
			for (int i = 0; i < transform.size(); i++) {
				String[] transformElements = transform.get(i).split("\\|");
				if (transformElements != null) {
					boolean transformUnique = uniqueTransformNames
							.add(transformElements[0]);
					if (transformUnique == false) {
						doubleTransform = true;
					}
				}
			}
		}

		List<String> inputFolder = job.getInputFolder();
		Set<String> uniqueInputFolders = new HashSet();
		boolean doubleInputFolder = false;
		if (inputFolder != null) {
			for (int i = 0; i < inputFolder.size(); i++) {
				boolean inputFolderUnique = uniqueInputFolders.add(inputFolder
						.get(i));
				if (inputFolderUnique == false) {
					doubleInputFolder = true;
				}
			}
		}

		if (errors.hasErrors() || notNull == false || threadsIsInteger == false
				|| jobExists == true || destinationExists == true
				|| doubleNewDestination == true || doubleMetadata == true
				|| doubleTransform == true || doubleInputFolder == true) {
			System.out.println("THE ERRORS: " + errors.toString());

			List<DestinationInfo> destinationInfoList = new ArrayList();

			if (sourceSinks != null) {
				for (int i = 0; i < sourceSinks.size(); i++) {
					String[] sourceSinkElements = sourceSinks.get(i).split(
							"\\|");
					DestinationInfo destinationInfo = new DestinationInfo();

					destinationInfo.setDestinationName(sourceSinkElements[0]);
					destinationInfo.setDestinationUrl(sourceSinkElements[1]);
					destinationInfo.setDestinationValue(sourceSinks.get(i));

					destinationInfoList.add(destinationInfo);
				}
			}
			ModelAndView mav = new ModelAndView("create-job");
			mav.addObject("job", job);
			mav.addObject("destinations", getJobService()
					.getAllDestinationConfiguredSourceSinks());
			mav.addObject("threadsIsInteger", threadsIsInteger);
			if (threadsIsInteger == true && destinationExists == false
					&& doubleNewDestination == false) {
				mav.addObject("destinationInfoList", destinationInfoList);
			}
			mav.addObject("jobExists", jobExists);
			mav.addObject("destinationExists", destinationExists);
			mav.addObject("doubleNewDestination", doubleNewDestination);
			mav.addObject("doubleMetadata", doubleMetadata);
			mav.addObject("doubleTransform", doubleTransform);
			mav.addObject("doubleInputFolder", doubleInputFolder);
			mav.addObject("notNull", notNull);
			mav.addObject("metadataOptions", getJobService()
					.getActionsByCategory(ConfigurableObject.CAT_METADATA));
			mav.addObject("transformOptions", getJobService()
					.getActionsByCategory(ConfigurableObject.CAT_TRANSFORM));
			mav.addObject("destinationOptions", getJobService()
					.getSourceSinksByCategory(
							ConfigurableObject.CAT_DESTINATION));
			mav.addObject("roles", getUserService().getCurrentUser()
					.getUserRoleSet());

			return mav;
		}

		ModelAndView mav = new ModelAndView();
		List<String> cronJobs = job.getCron();
		int jobId = getJobService().createJob(job.getName(),
				job.getDescription()).getId();

		for (int i = 0; i < cronJobs.size(); i++) {
			getJobService().createSchedule(jobId, cronJobs.get(i));
		}

		int destId = 0;

		if (job.getDest() != null && job.getDest().startsWith("destExists")) {
			destId = Integer.parseInt(job.getDest().substring(10));
		}

		List<String> sourceSink = job.getSourceSink();
		if (sourceSink != null) {
			for (int j = 0; j < sourceSink.size(); j++) {

				String createdSourceSink = sourceSink.get(j);
				String[] parameters = createdSourceSink.split("\\|");

				HashMap destinationParams = new HashMap();

				destinationParams
						.put(EDestinationParameter.NAME, parameters[0]);
				destinationParams.put(EDestinationParameter.URL, parameters[1]);
				destinationParams
						.put(EDestinationParameter.USER, parameters[2]);
				destinationParams.put(EDestinationParameter.PASSWORD,
						parameters[3]);
				destinationParams.put(EDestinationParameter.THREADS,
						parameters[4]);
				ConfiguredObject dest = getJobService().createDestination(
						parameters[5], destinationParams);

				if (createdSourceSink.equals(job.getDest())) {
					destId = dest.getId();
				}
			}
		}

		job.setId(jobId);
		job.setDest("" + destId);
		getPipelineAssembler().assemblePipeline(job);

		mav.setViewName("redirect:/job/dashboard");
		return mav;
	}

	@RequestMapping(value = "/job/{id}/edit", method = RequestMethod.GET)
	public ModelAndView editJobForm(@PathVariable int id) {
		ModelAndView mav = new ModelAndView();
		JobConfig jobConfig = getPipelineAssembler().getJobConfigForJob(id);
		mav.addObject("job", jobConfig);
		mav.addObject("schedules", getJobService().getSchedulesForJob(id));
		mav.addObject("destinations", getJobService()
				.getAllConfiguredSourceSinks());
		mav.addObject("metadataOptions", getJobService().getActionsByCategory(
				ConfigurableObject.CAT_METADATA));
		mav.addObject("transformOptions", getJobService().getActionsByCategory(
				ConfigurableObject.CAT_TRANSFORM));
		mav.addObject("destinationOptions", getJobService()
				.getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.addObject("defaultSchedule", SchedulerImpl.DEFAULT_SCHEDULE);
		mav.setViewName("edit-job");
		return mav;
	}

	@RequestMapping(value = "/job/{id}/edit", method = RequestMethod.POST)
	public ModelAndView editJob(@PathVariable int id,
			@ModelAttribute("job") @Valid JobConfig job, BindingResult errors) {

		boolean jobExists = false;
		if (!getJobService().getJob(id).getName().equals(job.getName())) {
			jobExists = getJobService().checkJobExists(job.getName());
		}

		List<String> sourceSinks = job.getSourceSink();
		Set<String> uniqueSourceSinks = new HashSet();
		boolean notNull = true;
		boolean destinationExists = false;
		boolean threadsIsInteger = true;
		boolean doubleNewDestination = false;
		if (sourceSinks != null) {
			for (int i = 0; i < sourceSinks.size(); i++) {
				String[] sourceSinkElements = sourceSinks.get(i).split("\\|");
				for (int j = 0; j < sourceSinkElements.length; j++) {
					if ("".equals(sourceSinkElements[j])
							|| null == sourceSinkElements[j])
						notNull = false;
				}
				destinationExists = getJobService().checkDestinationExists(
						sourceSinkElements[0]);

				// check whether any of the new source sinks have the same name
				// (should then throw error)
				boolean testBoolean = uniqueSourceSinks
						.add(sourceSinkElements[0]);
				if (testBoolean == false) {
					doubleNewDestination = true;
				}

				try {
					Integer.parseInt(sourceSinkElements[4]);
				} catch (NumberFormatException e) {
					threadsIsInteger = false;
				}
			}
		}

		List<String> metadata = job.getParamMetadata();
		Set<String> uniqueMetadataNames = new HashSet();
		boolean doubleMetadata = false;
		if (metadata != null) {
			for (int i = 0; i < metadata.size(); i++) {
				String[] metadataElements = metadata.get(i).split("\\|");
				boolean metadataUnique = uniqueMetadataNames
						.add(metadataElements[0]);
				if (metadataUnique == false) {
					doubleMetadata = true;
				}
			}
		}

		List<String> transform = job.getParamTransform();
		Set<String> uniqueTransformNames = new HashSet();
		boolean doubleTransform = false;
		if (transform != null) {
			for (int i = 0; i < transform.size(); i++) {
				String[] transformElements = transform.get(i).split("\\|");
				boolean transformUnique = uniqueTransformNames
						.add(transformElements[0]);
				if (transformUnique == false) {
					doubleTransform = true;
				}
			}
		}

		List<String> inputFolder = job.getInputFolder();
		Set<String> uniqueInputFolders = new HashSet();
		boolean doubleInputFolder = false;
		if (inputFolder != null) {
			for (int i = 0; i < inputFolder.size(); i++) {
				boolean inputFolderUnique = uniqueInputFolders.add(inputFolder
						.get(i));
				if (inputFolderUnique == false) {
					doubleInputFolder = true;
				}
			}
		}

		if (errors.hasErrors() || notNull == false || threadsIsInteger == false
				|| jobExists == true || destinationExists == true
				|| doubleNewDestination == true || doubleMetadata == true
				|| doubleTransform == true || doubleInputFolder == true) {
			System.out.println("THE ERRORS: " + errors.toString());

			List<DestinationInfo> destinationInfoList = new ArrayList();

			if (sourceSinks != null) {
				for (int i = 0; i < sourceSinks.size(); i++) {
					String[] sourceSinkElements = sourceSinks.get(i).split(
							"\\|");
					DestinationInfo destinationInfo = new DestinationInfo();

					destinationInfo.setDestinationName(sourceSinkElements[0]);
					destinationInfo.setDestinationUrl(sourceSinkElements[1]);
					destinationInfo.setDestinationValue(sourceSinks.get(i));

					destinationInfoList.add(destinationInfo);
				}
			}
			ModelAndView mav = new ModelAndView("edit-job");
			mav.addObject("job", job);
			mav.addObject("schedules", getJobService().getSchedulesForJob(id));
			mav.addObject("destinations", getJobService()
					.getAllConfiguredSourceSinks());
			mav.addObject("threadsIsInteger", threadsIsInteger);
			if (threadsIsInteger == true && destinationExists == false
					&& doubleNewDestination == false) {
				mav.addObject("destinationInfoList", destinationInfoList);
			}
			mav.addObject("jobExists", jobExists);
			mav.addObject("destinationExists", destinationExists);
			mav.addObject("doubleMetadata", doubleMetadata);
			mav.addObject("doubleTransform", doubleTransform);
			mav.addObject("doubleNewDestination", doubleNewDestination);
			mav.addObject("doubleInputFolder", doubleInputFolder);
			mav.addObject("metadataOptions", getJobService()
					.getActionsByCategory(ConfigurableObject.CAT_METADATA));
			mav.addObject("transformOptions", getJobService()
					.getActionsByCategory(ConfigurableObject.CAT_TRANSFORM));
			mav.addObject("destinationOptions", getJobService()
					.getSourceSinksByCategory(
							ConfigurableObject.CAT_DESTINATION));
			mav.addObject("notNull", notNull);
			mav.addObject("roles", getUserService().getCurrentUser()
					.getUserRoleSet());

			return mav;
		}

		ModelAndView mav = new ModelAndView();
		Job editedJob = getJobService().editJob(id, job.getName(),
				job.getDescription());
		int jobId = editedJob.getId();

		handleSchedule(id, job.getCron());

		List<String> sourceSink = job.getSourceSink();

		int destId = 0;
		if (job.getDest() != null && job.getDest().startsWith("destExists")) {
			destId = Integer.parseInt(job.getDest().substring(10));
		}

		if (sourceSink != null) {
			for (int j = 0; j < sourceSink.size(); j++) {

				String createdSourceSink = sourceSink.get(j);
				String[] parameters = createdSourceSink.split("\\|");

				HashMap destinationParams = new HashMap();

				destinationParams
						.put(EDestinationParameter.NAME, parameters[0]);
				destinationParams.put(EDestinationParameter.URL, parameters[1]);
				destinationParams
						.put(EDestinationParameter.USER, parameters[2]);
				destinationParams.put(EDestinationParameter.PASSWORD,
						parameters[3]);
				destinationParams.put(EDestinationParameter.THREADS,
						parameters[4]);
				ConfiguredObject dest = getJobService().createDestination(
						parameters[5], destinationParams);

				if (createdSourceSink.equals(job.getDest())) {
					destId = dest.getId();
				}
			}
		}

		/*
		 * Create new action pipeline and remove old one by deleting first
		 * action. The delete will be cascaded by Hibernate.
		 * 
		 * TODO: delete FileSourceSink
		 */

		job.setId(jobId);
		job.setDest("" + destId);
		getPipelineAssembler().assemblePipeline(job);

		ConfiguredAction firstAction = editedJob.getFirstConfiguredAction();
		if (firstAction != null) {
			getJobService().deleteAction(firstAction.getId());
		}

		mav.setViewName("redirect:/job/dashboard");
		return mav;
	}

	private void handleSchedule(int id, List<String> cronJobs) {
		boolean deleteSchedule = true;

		List<String> existingCronJobs = getJobService().getCronjobsForJob(id);

		logger.debug("Existing cronjobs: " + existingCronJobs);
		logger.debug("Cronjobs given in form: " + cronJobs);

		CollectionUtils.filter(existingCronJobs, new Predicate() {
			@Override
			public boolean evaluate(Object arg0) {
				return !arg0.equals(SchedulerImpl.DEFAULT_SCHEDULE);
			}
		});

		int listSize;

		try {
			listSize = cronJobs.size();
		} catch (Exception e) {
			listSize = 0;
		}
		boolean removeEntry = false;

		for (int i = 0; i < existingCronJobs.size(); i++) {
			for (int j = 0; j < listSize; j++) {
				if (removeEntry == true) {
					removeEntry = false;
				}

				if (existingCronJobs.get(i).equals(cronJobs.get(j))) {
					cronJobs.remove(j);
					j--;
					listSize--;
					deleteSchedule = false;
					removeEntry = true;
				}

			}
			if (deleteSchedule == true) {
				int scheduleId = getJobService().getScheduleId(id,
						existingCronJobs.get(i));
				logger.debug("removing cronJob: "
						+ getJobService().getSchedule(scheduleId)
								.getQuartzScheduling());
				getJobService().deleteSchedule(scheduleId);
			}
			deleteSchedule = true;
		}

		for (int i = 0; i < listSize; i++) {
			getJobService().createSchedule(id, cronJobs.get(i));
		}
	}

	@RequestMapping(value = "/job/{id}/edit/schedule", method = RequestMethod.GET)
	public ModelAndView editScheduleForm(@PathVariable int id) {
		ModelAndView mav = new ModelAndView();
		JobConfig jobConfig = new JobConfig();
		jobConfig.setId(id);
		jobConfig.setName(getJobService().getJob(id).getName());
		mav.addObject("jobConfig", jobConfig);
		mav.addObject("job", new ScheduleConfig());
		mav.addObject("schedules", getJobService().getSchedulesForJob(id));
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.addObject("defaultSchedule", SchedulerImpl.DEFAULT_SCHEDULE);
		mav.setViewName("edit-schedule");
		return mav;
	}

	@RequestMapping(value = "/job/{id}/edit/schedule", method = RequestMethod.POST)
	public ModelAndView editSchedule(@PathVariable int id,
			@ModelAttribute("job") @Valid ScheduleConfig job,
			BindingResult errors) {

		if (errors.hasErrors()) {
			System.out.println("THE ERRORS: " + errors.toString());

			ModelAndView mav = new ModelAndView("edit-schedule");
			mav.addObject("job", job);
			mav.addObject("schedules", getJobService().getSchedulesForJob(id));
			mav.addObject("roles", getUserService().getCurrentUser()
					.getUserRoleSet());
			mav.addObject("defaultSchedule", SchedulerImpl.DEFAULT_SCHEDULE);
			return mav;
		}

		ModelAndView mav = new ModelAndView();

		List<String> cronJobs = job.getCron();
		handleSchedule(id, cronJobs);
		mav.setViewName("redirect:/job/dashboard");
		return mav;
	}

	@RequestMapping(value = "/job/{id}/delete", method = RequestMethod.GET)
	public ModelAndView confirmDeleteJob(@PathVariable int id) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("job", getJobService().getJob(id));
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("delete-job");
		return mav;
	}

	@RequestMapping(value = "/job/{id}/delete", method = RequestMethod.POST)
	public ModelAndView deleteJob(@PathVariable int id) {
		ModelAndView mav = new ModelAndView();
		getJobService().deleteJob(id);
		mav.setViewName("redirect:/job/dashboard");
		return mav;
	}

	@RequestMapping("/destinations")
	public ModelAndView destinations() {
		ModelAndView mav = new ModelAndView();
		List<ConfiguredSourceSink> destinations = getJobService()
				.getAllDestinationConfiguredSourceSinks();

		Map<String, String> sourceSinkNames = new HashMap<String, String>();

		for (ConfiguredSourceSink destination : destinations) {
			SourceSink sourceSink = getSourceSinkFactory().getObject(
					destination.getClassName());
			sourceSinkNames.put(destination.getClassName(), sourceSink
					.getName());
		}

		mav.addObject("destinations", destinations);
		mav.addObject("typeNames", sourceSinkNames);
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("manage-destinations");
		return mav;
	}

	@RequestMapping(value = "/destinations/create", method = RequestMethod.GET)
	public ModelAndView createDestinationsForm() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("destination", new DestinationConfig());
		mav.addObject("destinationOptions", getJobService()
				.getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.addObject("showDestinations", "false");
		mav.setViewName("create-destinations");
		return mav;
	}

	@RequestMapping(value = "/destinations/create", method = RequestMethod.POST)
	public ModelAndView createDestinations(
			@ModelAttribute("destination") @Valid DestinationConfig destination,
			BindingResult errors) {

		List<String> sourceSinks = destination.getSourceSink();
		Set<String> uniqueSourceSinks = new HashSet();
		boolean destinationExists = false;
		boolean notNull = true;
		boolean threadsIsInteger = true;
		boolean doubleNewDestination = false;
		if (sourceSinks != null) {
			for (int i = 0; i < sourceSinks.size(); i++) {
				String[] sourceSinkElements = sourceSinks.get(i).split("\\|");
				for (int j = 0; j < sourceSinkElements.length; j++) {
					if ("".equals(sourceSinkElements[j])
							|| null == sourceSinkElements[j])
						notNull = false;
				}
				destinationExists = getJobService().checkDestinationExists(
						sourceSinkElements[0]);

				// check whether any of the new source sinks have the same name
				// (should then throw error)
				boolean testBoolean = uniqueSourceSinks
						.add(sourceSinkElements[0]);
				if (testBoolean == false) {
					doubleNewDestination = true;
				}

				try {
					Integer.parseInt(sourceSinkElements[4]);
				} catch (NumberFormatException e) {
					threadsIsInteger = false;
				}
			}
		}

		if (errors.hasErrors() || notNull == false || threadsIsInteger == false
				|| destinationExists == true || doubleNewDestination == true) {
			System.out.println("THE ERRORS: " + errors.toString());

			List<DestinationInfo> destinationInfoList = new ArrayList();

			if (sourceSinks != null) {
				for (int i = 0; i < sourceSinks.size(); i++) {
					String[] sourceSinkElements = sourceSinks.get(i).split(
							"\\|");
					DestinationInfo destinationInfo = new DestinationInfo();

					destinationInfo.setDestinationName(sourceSinkElements[0]);
					destinationInfo.setDestinationUrl(sourceSinkElements[1]);
					destinationInfo.setDestinationValue(sourceSinks.get(i));

					destinationInfoList.add(destinationInfo);
				}
			}
			ModelAndView mav = new ModelAndView("create-destinations");
			mav.addObject("threadsIsInteger", threadsIsInteger);
			if (threadsIsInteger == true && destinationExists == false
					&& doubleNewDestination == false) {
				mav.addObject("createDestinationInfoList", destinationInfoList);
			}
			mav.addObject("destinationExists", destinationExists);
			mav.addObject("doubleNewDestination", doubleNewDestination);
			mav.addObject("destination", destination);
			mav.addObject("destinationOptions", getJobService()
					.getSourceSinksByCategory(
							ConfigurableObject.CAT_DESTINATION));
			mav.addObject("notNull", notNull);
			mav.addObject("showDestinations", "false");
			return mav;
		}

		ModelAndView mav = new ModelAndView();

		List<String> sourceSink = destination.getSourceSink();

		for (int j = 0; j < sourceSink.size(); j++) {

			String createdSourceSink = sourceSink.get(j);
			String[] parameters = createdSourceSink.split("\\|");

			HashMap destinationParams = new HashMap();

			destinationParams.put(EDestinationParameter.NAME, parameters[0]);
			destinationParams.put(EDestinationParameter.URL, parameters[1]);
			destinationParams.put(EDestinationParameter.USER, parameters[2]);
			destinationParams
					.put(EDestinationParameter.PASSWORD, parameters[3]);
			destinationParams.put(EDestinationParameter.THREADS, parameters[4]);
			getJobService().createDestination(parameters[5], destinationParams);

		}
		mav.setViewName("redirect:/destinations");
		return mav;
	}

	@RequestMapping(value = "/destination/{id}/edit", method = RequestMethod.GET)
	public ModelAndView editDestinationForm(@PathVariable int id) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("destinations", new EditDestinationConfig());
		mav.addObject("destination", getJobService()
				.getConfiguredSourceSink(id));
		mav.addObject("destinationOptions", getJobService()
				.getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("edit-destination");
		return mav;
	}

	@RequestMapping(value = "/destination/{id}/edit", method = RequestMethod.POST)
	public ModelAndView editDestination(
			@PathVariable int id,
			@ModelAttribute("destinations") @Valid EditDestinationConfig destinations,
			BindingResult errors) {

		boolean destinationExists = false;
		if (!getJobService().getConfiguredSourceSink(id).getParameter("name")
				.equals(destinations.getDestinationName())) {
			destinationExists = getJobService().checkDestinationExists(
					destinations.getDestinationName());
		}

		if (errors.hasErrors() || destinationExists == true) {
			System.out.println("THE ERRORS: " + errors.toString());

			ModelAndView mav = new ModelAndView("edit-destination");
			mav.addObject("destinations", destinations);
			mav.addObject("destination", getJobService()
					.getConfiguredSourceSink(id));
			mav.addObject("destinationExists", destinationExists);
			mav.addObject("destinationOptions", getJobService()
					.getSourceSinksByCategory(
							ConfigurableObject.CAT_DESTINATION));
			return mav;
		}

		ModelAndView mav = new ModelAndView();

		HashMap destinationParams = new HashMap();

		destinationParams.put(EDestinationParameter.NAME, destinations
				.getDestinationName());
		destinationParams.put(EDestinationParameter.URL, destinations
				.getDestinationURL());
		destinationParams.put(EDestinationParameter.USER, destinations
				.getAlfUser());
		destinationParams.put(EDestinationParameter.PASSWORD, destinations
				.getAlfPswd());
		destinationParams.put(EDestinationParameter.THREADS, destinations
				.getNbrThreads());
		getJobService().editDestination(id, destinations.getDestinationType(),
				destinationParams);

		mav.setViewName("redirect:/destinations");
		return mav;
	}

	@RequestMapping(value = "/destination/{id}/delete", method = RequestMethod.GET)
	public ModelAndView confirmDeleteDestination(@PathVariable int id) {
		ModelAndView mav = new ModelAndView();

		// don't delete if the destination is in use by a job
		if (getJobService().getActionRelatedToConfiguredSourceSink(id) != null) {
			mav.addObject("destination", getJobService()
					.getConfiguredSourceSink(id));
			mav.addObject("roles", getUserService().getCurrentUser()
					.getUserRoleSet());
			mav.setViewName("delete-destination-fail");
			return mav;
		}

		mav.addObject("destination", getJobService()
				.getConfiguredSourceSink(id));
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("delete-destination");
		return mav;
	}

	@RequestMapping(value = "/destination/{id}/delete", method = RequestMethod.POST)
	public ModelAndView deleteDestination(@PathVariable int id) {
		ModelAndView mav = new ModelAndView();
		getJobService().deleteDestination(id);
		mav.setViewName("redirect:/destinations");
		return mav;
	}

	@RequestMapping("/job/{jobId}/report")
	public ModelAndView recentReport(@PathVariable int jobId,
			HttpServletRequest request, HttpServletResponse response) {
		Cycle cycle = getJobService().getLastCycleForJob(
				getJobService().getJob(jobId));
		return report(jobId, cycle.getId(), request, response);
	}

	@RequestMapping("/job/{jobId}/{cycleId}/report")
	public ModelAndView report(@PathVariable int jobId,
			@PathVariable int cycleId, HttpServletRequest request,
			HttpServletResponse response) {
		int page = ServletRequestUtils.getIntParameter(request, "p", 0);
		int pageSize = ServletRequestUtils
				.getIntParameter(request, "count", 10);
		return reportWithView(jobId, cycleId, page * pageSize, pageSize,
				"report");
	}

	@RequestMapping("/job/{jobId}/{cycleId}/report/exportcsv")
	public ModelAndView exportReportToCSV(@PathVariable int jobId,
			@PathVariable int cycleId) {
		return reportWithView(jobId, cycleId, 0, 0, "export-report-csv");
	}

	@RequestMapping("/job/{jobId}/{cycleId}/report/exportpdf")
	public ModelAndView exportReportToPDF(@PathVariable int jobId,
			@PathVariable int cycleId, HttpServletRequest request,
			HttpServletResponse response) {
		return reportWithView(jobId, cycleId, 0, 0, "export-report-pdf");
	}

	private ModelAndView reportWithView(final int jobId, final int cycleId,
			final int start, final int count, final String viewName) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("job", getJobService().getJob(jobId));
		Cycle cycle = getJobService().getCycle(cycleId);
		Date startDateTime = cycle.getStartDateTime();
		Date endDateTime = cycle.getEndDateTime();
		if (endDateTime == null) {
			endDateTime = new Date();
		}

		long totalTimeInSeconds = (endDateTime.getTime() - startDateTime
				.getTime()) / 1000;
		String docsPerSecond;
		String duration = Util.formatDuration(totalTimeInSeconds);

		final Long documentListSize = getJobService().countProcessedDocuments(
				cycleId);
		List<ProcessedDocument> processedDocuments = getJobService()
				.getProcessedDocuments(cycleId, start, count);

		if (documentListSize == 0L) {
			docsPerSecond = "0";
		} else {
			if (totalTimeInSeconds == 0) {
				docsPerSecond = "" + documentListSize;
			} else {
				double d = (double) documentListSize / totalTimeInSeconds;
				NumberFormat formatter = new DecimalFormat("#.##");
				docsPerSecond = formatter.format(d);
			}
		}

		// easiest way to keep the pagination links, not the cleanest
		PagedListHolder<ProcessedDocument> pagedListHolder = new PagedListHolder<ProcessedDocument>() {
			@Override
			public int getPage() {
				return start / count;
			}
			
			@Override
			public int getPageCount() {
				return (int) ((documentListSize - 1) / count + 1);
			}
			
			@Override
			public boolean isFirstPage() {
				return (start == 0);
			}
			
			@Override
			public boolean isLastPage() {

				return (start >= documentListSize - count);
			}

			@Override
			public int getFirstLinkedPage() {
				return 0;
			}
			
			@Override
			public int getLastLinkedPage() {
				return getPageCount() - 1;
			}
			
			@Override
			public int getMaxLinkedPages() {
				return 10;
			}
		};

		mav.addObject("cycle", cycle);
		mav.addObject("duration", duration);
		mav.addObject("processedDocuments", processedDocuments);
		mav.addObject("pagedListHolder", pagedListHolder);
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.addObject("documentListSize", documentListSize);
		mav.addObject("docsPerSecond", docsPerSecond);
		mav.setViewName(viewName);
		return mav;
	}

	@RequestMapping("/job/{jobId}/history")
	public ModelAndView history(@PathVariable int jobId,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();

		List<HistoryInfo> historyInfoList = new ArrayList();
		historyInfoList = getJobService().getHistory(jobId);

		PagedListHolder pagedListHolder = new PagedListHolder(historyInfoList);
		int page = ServletRequestUtils.getIntParameter(request, "p", 0);
		pagedListHolder.setPage(page);
		int pageSize = 10;
		pagedListHolder.setPageSize(pageSize);

		mav.addObject("job", getJobService().getJob(jobId));
		mav.addObject("pagedListHolder", pagedListHolder);
		mav.addObject("historyInfoList", historyInfoList);
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("history");
		return mav;
	}

	@RequestMapping("/job/{jobId}/cycle/run")
	public ModelAndView runPoller(@PathVariable int jobId) {
		ModelAndView mav = new ModelAndView();

		getJobService().scheduleNow(jobId,
				getJobService().getDefaultScheduleIdForJob(jobId));

		mav.setViewName("redirect:/job/dashboard");
		return mav;
	}
}
