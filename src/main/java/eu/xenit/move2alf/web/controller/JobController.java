package eu.xenit.move2alf.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import eu.xenit.move2alf.web.dto.JobConfig;
import eu.xenit.move2alf.core.enums.EDestinationParameter;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.logic.UserService;

@Controller
public class JobController {

	private static final Logger logger = LoggerFactory
			.getLogger(JobController.class);

	private JobService jobService;
	private UserService userService;

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

	@RequestMapping("/job/dashboard")
	public ModelAndView dashboard() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("jobs", getJobService().getAllJobs());
		mav.addObject("roles", getUserService().getCurrentUser().getUserRoleSet());
		mav.addObject("cycles", getJobService().getLastCycleForJobs());
		mav.setViewName("dashboard");
		return mav;
	}

	@RequestMapping(value = "/job/create", method = RequestMethod.GET)
	public ModelAndView createJobForm() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("job", new JobConfig());
		mav.addObject("destinations", getJobService().getAllConfiguredSourceSinks());
		mav.setViewName("create-job");
		return mav;
	}

	@RequestMapping(value = "/job/create", method = RequestMethod.POST)
	public ModelAndView createJob(JobConfig job) {
		ModelAndView mav = new ModelAndView();
		List<String> cronJobs = job.getCron();
		int jobId = getJobService().createJob(job.getName(), job.getDescription()).getId();
		for(int i = 0; i< cronJobs.size(); i++){
			getJobService().createSchedule(jobId, cronJobs.get(i));
		}
		
		List<String> sourceSink = job.getSourceSink();
		
		for(int j =0; j<sourceSink.size(); j++){
			
			String createdSourceSink = sourceSink.get(j);
			String[] parameters = createdSourceSink.split("\\|");
			
			HashMap destinationParams = new HashMap();
			
			destinationParams.put(EDestinationParameter.NAME, parameters[0]);
			destinationParams.put(EDestinationParameter.URL, parameters[1]);
			destinationParams.put(EDestinationParameter.USER, parameters[2]);
			destinationParams.put(EDestinationParameter.PASSWORD, parameters[3]);
			destinationParams.put(EDestinationParameter.THREADS, parameters[4]);
			getJobService().createDestination(parameters[5], destinationParams);
			
		}
		mav.setViewName("redirect:/job/dashboard");
		return mav;
	}
	
	@RequestMapping(value = "/job/{id}/edit", method = RequestMethod.GET)
	public ModelAndView editJobForm(@PathVariable int id) {
		ModelAndView mav = new ModelAndView();
		JobConfig jobConfig = new JobConfig();
		jobConfig.setId(id);
		jobConfig.setName(getJobService().getJob(id).getName());
		jobConfig.setDescription(getJobService().getJob(id).getDescription());
		mav.addObject("job",jobConfig);
		mav.addObject("schedules", getJobService().getSchedulesForJob(id));
		mav.addObject("destinations", getJobService().getAllConfiguredSourceSinks());
		mav.setViewName("edit-job");
		return mav;
	}

	@RequestMapping(value = "/job/{id}/edit", method = RequestMethod.POST)
	public ModelAndView editJob(@PathVariable int id, JobConfig job) {
		ModelAndView mav = new ModelAndView();
		getJobService().editJob(id, job.getName(), job.getDescription());
		boolean deleteSchedule=true;
		List<String> cronJobs = job.getCron();
		
		List<String> existingCronJobs = getJobService().getCronjobsForJob(id);
		
		int listSize;
		
		try{
			listSize = cronJobs.size();
		}
		catch(Exception e){
			listSize = 0;
		}
		boolean removeEntry=false;
	
		for(int i=0; i<existingCronJobs.size(); i++){
			for(int j=0; j<listSize; j++){
				if(removeEntry==true){
					removeEntry=false;
				}
				
				if(existingCronJobs.get(i).equals(cronJobs.get(j))){
					cronJobs.remove(j);
					j--;
					listSize--;
					deleteSchedule=false;
					removeEntry=true;
				}
				
			}
			if(deleteSchedule == true){
				int scheduleId = getJobService().getScheduleId(id, existingCronJobs.get(i));
				getJobService().deleteSchedule(scheduleId);
			}
			deleteSchedule=true;
		}
		
		for(int i = 0; i< listSize; i++){
			getJobService().createSchedule(id, cronJobs.get(i));
		}
		
		
		List<String> sourceSink = job.getSourceSink();
		
		for(int j =0; j<sourceSink.size(); j++){
			
			String createdSourceSink = sourceSink.get(j);
			String[] parameters = createdSourceSink.split("\\|");
			
			HashMap destinationParams = new HashMap();
			
			destinationParams.put(EDestinationParameter.NAME, parameters[0]);
			destinationParams.put(EDestinationParameter.URL, parameters[1]);
			destinationParams.put(EDestinationParameter.USER, parameters[2]);
			destinationParams.put(EDestinationParameter.PASSWORD, parameters[3]);
			destinationParams.put(EDestinationParameter.THREADS, parameters[4]);
			getJobService().createDestination(parameters[5], destinationParams);	
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
		mav.addObject("job",jobConfig);
		mav.addObject("schedules", getJobService().getSchedulesForJob(id));
		mav.setViewName("edit-schedule");
		return mav;
	}
	
	@RequestMapping(value = "/job/{id}/edit/schedule", method = RequestMethod.POST)
	public ModelAndView editSchedule(@PathVariable int id, JobConfig job) {
		ModelAndView mav = new ModelAndView();
		
		boolean deleteSchedule=true;
		List<String> cronJobs = job.getCron();
		
		List<String> existingCronJobs = getJobService().getCronjobsForJob(id);
		
		int listSize;
		
		try{
			listSize = cronJobs.size();
		}
		catch(Exception e){
			listSize = 0;
		}
		boolean removeEntry=false;
	
		for(int i=0; i<existingCronJobs.size(); i++){
			for(int j=0; j<listSize; j++){
				if(removeEntry==true){
					removeEntry=false;
				}
				
				if(existingCronJobs.get(i).equals(cronJobs.get(j))){
					cronJobs.remove(j);
					j--;
					listSize--;
					deleteSchedule=false;
					removeEntry=true;
				}
				
			}
			if(deleteSchedule == true){
				int scheduleId = getJobService().getScheduleId(id, existingCronJobs.get(i));
				getJobService().deleteSchedule(scheduleId);
			}
			deleteSchedule=true;
		}
		
		for(int i = 0; i< listSize; i++){
			getJobService().createSchedule(id, cronJobs.get(i));
		}
		mav.setViewName("redirect:/job/dashboard");
		return mav;
	}
	
	@RequestMapping(value = "/job/{id}/delete", method = RequestMethod.GET)
	public ModelAndView confirmDeleteJob(@PathVariable int id) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("job", getJobService().getJob(id));
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
		mav.addObject("destinations", getJobService().getAllConfiguredSourceSinks());
		mav.setViewName("manage-destinations");
		return mav;
	}
	
	@RequestMapping(value = "/destinations/create", method = RequestMethod.GET)
	public ModelAndView createDestinationsForm() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("job", new JobConfig());
		mav.setViewName("create-destinations");
		return mav;
	}

	@RequestMapping(value = "/destinations/create", method = RequestMethod.POST)
	public ModelAndView createDestinations(JobConfig job) {
		ModelAndView mav = new ModelAndView();
		
		List<String> sourceSink = job.getSourceSink();
		
		for(int j =0; j<sourceSink.size(); j++){
			
			String createdSourceSink = sourceSink.get(j);
			String[] parameters = createdSourceSink.split("\\|");
			
			HashMap destinationParams = new HashMap();
			
			destinationParams.put(EDestinationParameter.NAME, parameters[0]);
			destinationParams.put(EDestinationParameter.URL, parameters[1]);
			destinationParams.put(EDestinationParameter.USER, parameters[2]);
			destinationParams.put(EDestinationParameter.PASSWORD, parameters[3]);
			destinationParams.put(EDestinationParameter.THREADS, parameters[4]);
			getJobService().createDestination(parameters[5], destinationParams);
			
		}
		mav.setViewName("redirect:/destinations");
		return mav;
	}
	
	@RequestMapping(value = "/destination/{id}/edit", method = RequestMethod.GET)
	public ModelAndView editDestinationForm(@PathVariable int id) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("job", new JobConfig());
		mav.addObject("destination", getJobService().getConfiguredSourceSink(id));
		mav.setViewName("edit-destination");
		return mav;
	}

	@RequestMapping(value = "/destination/{id}/edit", method = RequestMethod.POST)
	public ModelAndView editDestination(@PathVariable int id, JobConfig job) {
		ModelAndView mav = new ModelAndView();

		
		List<String> sourceSink = job.getSourceSink();
		
		for(int j =0; j<sourceSink.size(); j++){
			
			String createdSourceSink = sourceSink.get(j);
			System.out.println(createdSourceSink);
			String[] parameters = createdSourceSink.split("\\|");
			
			HashMap destinationParams = new HashMap();
			
			destinationParams.put(EDestinationParameter.NAME, parameters[0]);
			destinationParams.put(EDestinationParameter.URL, parameters[1]);
			destinationParams.put(EDestinationParameter.USER, parameters[2]);
			destinationParams.put(EDestinationParameter.PASSWORD, parameters[3]);
			destinationParams.put(EDestinationParameter.THREADS, parameters[4]);
			getJobService().editDestination(id, parameters[5], destinationParams);	
		}
		mav.setViewName("redirect:/destinations");
		return mav;
	}
	
	@RequestMapping(value = "/destination/{id}/delete", method = RequestMethod.GET)
	public ModelAndView confirmDeleteDestination(@PathVariable int id) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("destination", getJobService().getConfiguredSourceSink(id));
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

}
