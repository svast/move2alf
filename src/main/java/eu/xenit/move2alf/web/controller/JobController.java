package eu.xenit.move2alf.web.controller;

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
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.logic.PipelineAssembler;
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

	@RequestMapping("/job/dashboard")
	public ModelAndView dashboard() {
		ModelAndView mav = new ModelAndView();
		List<JobInfo> jobInfoList = new ArrayList();
		List<Job> jobs = getJobService().getAllJobs();
		if (null != jobs) {
			for (int i = 0; i < jobs.size(); i++) {
				JobInfo jobInfo = new JobInfo();
				Job job = jobs.get(i);
				jobInfo.setJobId(job.getId());
				jobInfo.setJobName(job.getName());
				try {
					jobInfo.setCycleId(getJobService().getLastCycleForJob(
							job).getId());
					jobInfo
							.setCycleStartDateTime(getJobService()
									.getLastCycleForJob(job)
									.getStartDateTime());
					jobInfo.setScheduleState(getJobService()
							.getJobState(job.getId()).getDisplayName());

				} catch (Exception e) {
				}

				jobInfoList.add(jobInfo);
			}
		}
		mav.addObject("jobInfoList", jobInfoList);
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("dashboard");
		return mav;
	}

	@RequestMapping(value = "/job/create", method = RequestMethod.GET)
	public ModelAndView createJobForm() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("job", new JobConfig());
		mav.addObject("destinations", getJobService()
				.getAllDestinationConfiguredSourceSinks());
		mav.addObject("metadataOptions", getJobService()
				.getActionsByCategory(ConfigurableObject.CAT_METADATA));
		mav.addObject("transformOptions", getJobService()
				.getActionsByCategory(ConfigurableObject.CAT_TRANSFORM));
		mav.addObject("destinationOptions", getJobService()
				.getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("create-job");
		return mav;
	}

	@RequestMapping(value = "/job/create", method = RequestMethod.POST)
	public ModelAndView createJob(@ModelAttribute("job") @Valid JobConfig job,
			BindingResult errors){

		boolean jobExists=getJobService().checkJobExists(job.getName());
		boolean destinationExists=false;
		boolean doubleNewDestination=false;
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
				destinationExists = getJobService().checkDestinationExists(sourceSinkElements[0]);
				//check whether any of the new source sinks have the same name (should then throw error)
				boolean testBoolean = uniqueSourceSinks.add(sourceSinkElements[0]);
				if(testBoolean == false){
					doubleNewDestination=true;
				}
				
				try{
					Integer.parseInt(sourceSinkElements[4]);
				}
				catch(NumberFormatException e){
					threadsIsInteger=false;
				}
			}
		}

		if (errors.hasErrors() || notNull == false || threadsIsInteger == false 
					|| jobExists==true || destinationExists==true || doubleNewDestination==true) {
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
			if(threadsIsInteger == true && destinationExists==false && doubleNewDestination==false){
				mav.addObject("destinationInfoList", destinationInfoList);
			}
			mav.addObject("jobExists", jobExists);
			mav.addObject("destinationExists", destinationExists);
			mav.addObject("doubleNewDestination", doubleNewDestination);
			mav.addObject("notNull", notNull);
			mav.addObject("metadataOptions", getJobService()
					.getActionsByCategory(ConfigurableObject.CAT_METADATA));
			mav.addObject("transformOptions", getJobService()
					.getActionsByCategory(ConfigurableObject.CAT_TRANSFORM));
			mav.addObject("destinationOptions", getJobService()
					.getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
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
		mav.addObject("metadataOptions", getJobService()
				.getActionsByCategory(ConfigurableObject.CAT_METADATA));
		mav.addObject("transformOptions", getJobService()
				.getActionsByCategory(ConfigurableObject.CAT_TRANSFORM));
		mav.addObject("destinationOptions", getJobService()
				.getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("edit-job");
		return mav;
	}

	@RequestMapping(value = "/job/{id}/edit", method = RequestMethod.POST)
	public ModelAndView editJob(@PathVariable int id,
			@ModelAttribute("job") @Valid JobConfig job, BindingResult errors) {

		boolean jobExists = false;
		if(!getJobService().getJob(id).getName().equals(job.getName())){
			jobExists=getJobService().checkJobExists(job.getName());
		}
		
		List<String> sourceSinks = job.getSourceSink();
		Set<String> uniqueSourceSinks = new HashSet();
		boolean notNull=true;
		boolean destinationExists=false;
		boolean threadsIsInteger=true;
		boolean doubleNewDestination=false;
		if(sourceSinks!= null){
			for(int i=0; i<sourceSinks.size(); i++){
				String[] sourceSinkElements = sourceSinks.get(i).split("\\|");
				for (int j = 0; j < sourceSinkElements.length; j++) {
					if ("".equals(sourceSinkElements[j])
							|| null == sourceSinkElements[j])
						notNull = false;
				}
				destinationExists = getJobService().checkDestinationExists(sourceSinkElements[0]);
				
				//check whether any of the new source sinks have the same name (should then throw error)
				boolean testBoolean = uniqueSourceSinks.add(sourceSinkElements[0]);
				if(testBoolean == false){
					doubleNewDestination=true;
				}
				
				try{
					Integer.parseInt(sourceSinkElements[4]);
				}
				catch(NumberFormatException e){
					threadsIsInteger=false;
				}
			}
		}
		
		if (errors.hasErrors() || notNull==false || threadsIsInteger == false 
					|| jobExists==true || destinationExists==true || doubleNewDestination==true) {
			System.out.println("THE ERRORS: "+errors.toString());
			
			List<DestinationInfo> destinationInfoList = new ArrayList();
			
			if(sourceSinks!= null){
				for(int i=0; i<sourceSinks.size(); i++){
					String[] sourceSinkElements = sourceSinks.get(i).split("\\|");
					DestinationInfo destinationInfo = new DestinationInfo();
					
					destinationInfo.setDestinationName(sourceSinkElements[0]);
					destinationInfo.setDestinationUrl(sourceSinkElements[1]);
					destinationInfo.setDestinationValue(sourceSinks.get(i));
					
					destinationInfoList.add(destinationInfo);
				}
			}
			ModelAndView mav = new ModelAndView("edit-job");
			mav.addObject("job",job);
			mav.addObject("schedules", getJobService().getSchedulesForJob(id));
			mav.addObject("destinations", getJobService()
					.getAllConfiguredSourceSinks());
			mav.addObject("threadsIsInteger", threadsIsInteger);
			if(threadsIsInteger == true && destinationExists == false && doubleNewDestination==false){
				mav.addObject("destinationInfoList", destinationInfoList);
			}
			mav.addObject("jobExists", jobExists);
			mav.addObject("destinationExists", destinationExists);
			mav.addObject("doubleNewDestination", doubleNewDestination);
			mav.addObject("metadataOptions", getJobService()
					.getActionsByCategory(ConfigurableObject.CAT_METADATA));
			mav.addObject("transformOptions", getJobService()
					.getActionsByCategory(ConfigurableObject.CAT_TRANSFORM));
			mav.addObject("destinationOptions", getJobService()
					.getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
			mav.addObject("notNull", notNull);
			mav.addObject("roles", getUserService().getCurrentUser()
					.getUserRoleSet());

            return mav;
		}
		
		ModelAndView mav = new ModelAndView();
		Job editedJob = getJobService().editJob(id, job.getName(), job.getDescription());
		int jobId = editedJob.getId();
		boolean deleteSchedule = true;
		List<String> cronJobs = job.getCron();

		List<String> existingCronJobs = getJobService().getCronjobsForJob(id);

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
				getJobService().deleteSchedule(scheduleId);
			}
			deleteSchedule = true;
		}

		for (int i = 0; i < listSize; i++) {
			getJobService().createSchedule(id, cronJobs.get(i));
		}

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
		if(firstAction!=null){
			getJobService().deleteAction(firstAction.getId());
		}

		mav.setViewName("redirect:/job/dashboard");
		return mav;
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
		mav.setViewName("edit-schedule");
		return mav;
	}

	@RequestMapping(value = "/job/{id}/edit/schedule", method = RequestMethod.POST)
	public ModelAndView editSchedule(@PathVariable int id,
			@ModelAttribute("job") @Valid ScheduleConfig job,
			BindingResult errors) {

		if (errors.hasErrors()) {
			System.out.println("THE ERRORS: "+errors.toString());
			
			ModelAndView mav = new ModelAndView("edit-schedule");
			mav.addObject("job",job);
			mav.addObject("schedules", getJobService().getSchedulesForJob(id));
			mav.addObject("roles", getUserService().getCurrentUser()
					.getUserRoleSet());

            return mav;
		}
		
		ModelAndView mav = new ModelAndView();

		boolean deleteSchedule = true;
		List<String> cronJobs = job.getCron();

		List<String> existingCronJobs = getJobService().getCronjobsForJob(id);

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
				getJobService().deleteSchedule(scheduleId);
			}
			deleteSchedule = true;
		}

		for (int i = 0; i < listSize; i++) {
			getJobService().createSchedule(id, cronJobs.get(i));
		}
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
			SourceSink sourceSink = getSourceSinkFactory().getObject(destination.getClassName());
			sourceSinkNames.put(destination.getClassName(), sourceSink.getName());
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
		mav.addObject("showDestinations","false");
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
		boolean notNull=true;
		boolean threadsIsInteger=true;
		boolean doubleNewDestination = false;
		if(sourceSinks!= null){
			for(int i=0; i<sourceSinks.size(); i++){
				String[] sourceSinkElements = sourceSinks.get(i).split("\\|");
				for (int j = 0; j < sourceSinkElements.length; j++) {
					if ("".equals(sourceSinkElements[j])
							|| null == sourceSinkElements[j])
						notNull = false;
				}
				destinationExists = getJobService().checkDestinationExists(sourceSinkElements[0]);
				
				//check whether any of the new source sinks have the same name (should then throw error)
				boolean testBoolean = uniqueSourceSinks.add(sourceSinkElements[0]);
				if(testBoolean == false){
					doubleNewDestination=true;
				}
				
				try{
					Integer.parseInt(sourceSinkElements[4]);
				}
				catch(NumberFormatException e){
					threadsIsInteger=false;
				}
			}
		}
		
		if (errors.hasErrors() || notNull==false || threadsIsInteger == false 
					|| destinationExists==true || doubleNewDestination == true) {
			System.out.println("THE ERRORS: "+errors.toString());
			
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
			if(threadsIsInteger == true && destinationExists==false && doubleNewDestination==false){
				mav.addObject("createDestinationInfoList", destinationInfoList);
			}
			mav.addObject("destinationExists",destinationExists);
			mav.addObject("doubleNewDestination",doubleNewDestination);
			mav.addObject("destination",destination);
			mav.addObject("destinationOptions", getJobService()
					.getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
			mav.addObject("notNull", notNull);
			mav.addObject("showDestinations","false");
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
		if(!getJobService().getConfiguredSourceSink(id).getParameter("name").equals(destinations.getDestinationName())){
			destinationExists = getJobService().checkDestinationExists(destinations.getDestinationName());
		}
		

		if (errors.hasErrors() || destinationExists == true) {
			System.out.println("THE ERRORS: "+errors.toString());
			
			ModelAndView mav = new ModelAndView("edit-destination");
			mav.addObject("destinations",destinations);
			mav.addObject("destination", getJobService()
					.getConfiguredSourceSink(id));
			mav.addObject("destinationExists", destinationExists);
			mav.addObject("destinationOptions", getJobService()
					.getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
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
		
		//don't delete if the destination is in use by a job
		if(getJobService().getActionRelatedToConfiguredSourceSink(id) != null){
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
		ModelAndView mav = new ModelAndView();
		mav.addObject("job", getJobService().getJob(jobId));
		//Cycle cycle = getJobService().getCycle(cycleId);
		Cycle cycle = getJobService().getLastCycleForJob(getJobService().getJob(jobId));
		int cycleId = cycle.getId();
		Date startDateTime = cycle.getStartDateTime();
		Date endDateTime = cycle.getEndDateTime();
		String duration = getJobService().getDuration(startDateTime,
				endDateTime);
		//get total seconds to calculate docs/s
		String[] durationSplit = duration.split(":");
		Integer hours = new Integer(durationSplit[0]).intValue();
		Integer minutes = new Integer(durationSplit[1]).intValue();
		Integer seconds = new Integer(durationSplit[2]).intValue();
		int totalTimeInSeconds = hours * 60*60+minutes*60+seconds;
		String docsPerSecond;
		
		List<ProcessedDocument> processedDocuments = getJobService().getProcessedDocuments(cycleId);
		Integer documentListSize = processedDocuments.size();
		if(processedDocuments == null || "".equals(processedDocuments)){
			documentListSize = 0;
		}
		if(documentListSize==0){
			docsPerSecond="";
		}else{
			if(totalTimeInSeconds==0){
				docsPerSecond = ""+documentListSize;
			}else{
				docsPerSecond = ""+documentListSize/totalTimeInSeconds;
			}
		}
		PagedListHolder pagedListHolder = new PagedListHolder(processedDocuments);
		int page = ServletRequestUtils.getIntParameter(request, "p", 0);
		pagedListHolder.setPage(page);
		int pageSize = 10;
		pagedListHolder.setPageSize(pageSize);
		
		mav.addObject("cycle", cycle);
		mav.addObject("duration", duration);
		mav.addObject("pagedListHolder", pagedListHolder);
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.addObject("documentListSize", documentListSize);
		mav.addObject("docsPerSecond", docsPerSecond);
		mav.setViewName("report");
		return mav;
	}
	
	@RequestMapping("/job/{jobId}/{cycleId}/report")
	public ModelAndView report(@PathVariable int jobId,
			@PathVariable int cycleId, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("job", getJobService().getJob(jobId));
		Cycle cycle = getJobService().getCycle(cycleId);
		Date startDateTime = cycle.getStartDateTime();
		Date endDateTime = cycle.getEndDateTime();
		String duration = getJobService().getDuration(startDateTime,
				endDateTime);
		//get total seconds to calculate docs/s
		String[] durationSplit = duration.split(":");
		Integer hours = new Integer(durationSplit[0]).intValue();
		Integer minutes = new Integer(durationSplit[1]).intValue();
		Integer seconds = new Integer(durationSplit[2]).intValue();
		int totalTimeInSeconds = hours * 60*60+minutes*60+seconds;
		String docsPerSecond;
		
		List<ProcessedDocument> processedDocuments = getJobService().getProcessedDocuments(cycleId);
		Integer documentListSize = processedDocuments.size();
		if(processedDocuments == null || "".equals(processedDocuments)){
			documentListSize = 0;
		}
		if(documentListSize==0){
			docsPerSecond="";
		}else{
			if(totalTimeInSeconds==0){
				docsPerSecond = ""+documentListSize;
			}else{
				docsPerSecond = ""+documentListSize/totalTimeInSeconds;
			}
		}
		PagedListHolder pagedListHolder = new PagedListHolder(processedDocuments);
		int page = ServletRequestUtils.getIntParameter(request, "p", 0);
		pagedListHolder.setPage(page);
		int pageSize = 10;
		pagedListHolder.setPageSize(pageSize);
		
		mav.addObject("cycle", cycle);
		mav.addObject("duration", duration);
		mav.addObject("pagedListHolder", pagedListHolder);
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.addObject("documentListSize", documentListSize);
		mav.addObject("docsPerSecond", docsPerSecond);
		mav.setViewName("report");
		return mav;
	}
	
	@RequestMapping("/job/{jobId}/{cycleId}/report/exportcsv")
	public ModelAndView exportReportToCSV(@PathVariable int jobId,
			@PathVariable int cycleId) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("job", getJobService().getJob(jobId));
		Cycle cycle = getJobService().getCycle(cycleId);
		Date startDateTime = cycle.getStartDateTime();
		Date endDateTime = cycle.getEndDateTime();
		String duration = getJobService().getDuration(startDateTime,
				endDateTime);
		//get total seconds to calculate docs/s
		String[] durationSplit = duration.split(":");
		Integer hours = new Integer(durationSplit[0]).intValue();
		Integer minutes = new Integer(durationSplit[1]).intValue();
		Integer seconds = new Integer(durationSplit[2]).intValue();
		int totalTimeInSeconds = hours * 60*60+minutes*60+seconds;
		String docsPerSecond;
		
		List<ProcessedDocument> processedDocuments = getJobService().getProcessedDocuments(cycleId);
		Integer documentListSize = processedDocuments.size();
		
		if(processedDocuments == null || "".equals(processedDocuments)){
			documentListSize = 0;
		}
		if(documentListSize==0){
			docsPerSecond="0";
		}else{
			if(totalTimeInSeconds==0){
				docsPerSecond = ""+documentListSize;
			}else{
				docsPerSecond = ""+documentListSize/totalTimeInSeconds;
			}
		}
		
		mav.addObject("cycle", cycle);
		mav.addObject("duration", duration);
		mav.addObject("processedDocuments", processedDocuments);
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.addObject("documentListSize", documentListSize);
		mav.addObject("docsPerSecond", docsPerSecond);
		mav.setViewName("export-report-csv");
		return mav;
	}
	
	@RequestMapping("/job/{jobId}/{cycleId}/report/exportpdf")
	public ModelAndView exportReportToPDF(@PathVariable int jobId,
			@PathVariable int cycleId, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("job", getJobService().getJob(jobId));
		Cycle cycle = getJobService().getCycle(cycleId);
		Date startDateTime = cycle.getStartDateTime();
		Date endDateTime = cycle.getEndDateTime();
		String duration = getJobService().getDuration(startDateTime,
				endDateTime);
		//get total seconds to calculate docs/s
		String[] durationSplit = duration.split(":");
		Integer hours = new Integer(durationSplit[0]).intValue();
		Integer minutes = new Integer(durationSplit[1]).intValue();
		Integer seconds = new Integer(durationSplit[2]).intValue();
		int totalTimeInSeconds = hours * 60*60+minutes*60+seconds;
		String docsPerSecond;
		
		List<ProcessedDocument> processedDocuments = getJobService().getProcessedDocuments(cycleId);
		Integer documentListSize = processedDocuments.size();
		
		if(processedDocuments == null || "".equals(processedDocuments)){
			documentListSize = 0;
		}
		if(documentListSize==0){
			docsPerSecond="0";
		}else{
			if(totalTimeInSeconds==0){
				docsPerSecond = ""+documentListSize;
			}else{
				docsPerSecond = ""+documentListSize/totalTimeInSeconds;
			}
		}
		
		mav.addObject("cycle", cycle);
		mav.addObject("duration", duration);
		mav.addObject("processedDocuments", processedDocuments);
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.addObject("documentListSize", documentListSize);
		mav.addObject("docsPerSecond", docsPerSecond);
		mav.setViewName("export-report-pdf");
		return mav;
	}

	@RequestMapping("/job/{jobId}/history")
	public ModelAndView history(@PathVariable int jobId,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		
		List<Cycle> cycles = getJobService().getCyclesForJobDesc(jobId);
		List<HistoryInfo> historyInfoList = new ArrayList();
		//Store info in HistoryInfo class
		if(cycles != null){
			for(int i=0; i<cycles.size(); i++){
				int cycleId = cycles.get(i).getId();
				
				List<ProcessedDocument> processedDocuments = getJobService().getProcessedDocuments(cycleId);
				int documentListSize = processedDocuments.size();
				
				if(processedDocuments == null || "".equals(processedDocuments)){
					documentListSize = 0;
				}
				
		/*		String scheduleState = getJobService()
				.getLastCycleForJob(getJobService().getJob(jobId)).getSchedule()
				.getState().getDisplayName();
				*/
				String scheduleState = cycles.get(i).getSchedule()
				.getState().getDisplayName();
				
				HistoryInfo historyInfo = new HistoryInfo(cycleId, cycles.get(i).getStartDateTime(), scheduleState, documentListSize);
				
				historyInfoList.add(historyInfo);
			}
		}
		
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
		
		String cronJob = getJobService().getInstantCronJob();
		getJobService().createSchedule(jobId, cronJob);
		
		mav.addObject("job", getJobService().getJob(jobId));
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("confirm-run");
		return mav;
	}

}
