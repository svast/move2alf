package eu.xenit.move2alf.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import eu.xenit.move2alf.web.dto.JobConfig;
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
		mav.setViewName("dashboard");
		return mav;
	}

	@RequestMapping(value = "/job/create", method = RequestMethod.GET)
	public ModelAndView createJobForm() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("job", new JobConfig());
		mav.setViewName("create-job");
		return mav;
	}

	@RequestMapping(value = "/job/create", method = RequestMethod.POST)
	public ModelAndView createJob(JobConfig job) {
		ModelAndView mav = new ModelAndView();
		getJobService().createJob(job.getName(), job.getDescription());
		mav.setViewName("redirect:/job/dashboard");
		return mav;
	}

	@RequestMapping(value = "/job/create/destination", method = RequestMethod.GET)
	public ModelAndView createDestinationForm() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("job", new JobConfig());
		mav.setViewName("create-destination");
		return mav;
	}

	/*
	 * @RequestMapping(value = "/job/create/destination", method =
	 * RequestMethod.POST) public ModelAndView
	 * createDestination(DestinationConfig destination) { ModelAndView mav = new
	 * ModelAndView(); getJobService().createDestination(destination.getName(),
	 * destination.getDescription());
	 * mav.setViewName("redirect:/job/dashboard"); return mav; }
	 */
	@RequestMapping(value = "/job/{id}/edit", method = RequestMethod.GET)
	public ModelAndView editJobForm(@PathVariable int id) {
		ModelAndView mav = new ModelAndView();

		JobConfig jobConfig = new JobConfig(id, getJobService().getJob(id).getName(),getJobService().getJob(id).getDescription());
		mav.addObject("job",jobConfig);
	//	mav.addObject("schedules", getScheduleService().getSchedules(jobConfig.getName()));
		mav.setViewName("edit-job");
		return mav;
	}

	@RequestMapping(value = "/job/{id}/edit", method = RequestMethod.POST)
	public ModelAndView editJob(@PathVariable int id, JobConfig job) {
		ModelAndView mav = new ModelAndView();
		getJobService().editJob(id, job.getName(), job.getDescription());
		mav.setViewName("redirect:/job/dashboard");
		return mav;
	}

	@RequestMapping(value = "/job/{id}/edit/schedule", method = RequestMethod.GET)
	public ModelAndView editScheduleForm(@PathVariable int id) {
		ModelAndView mav = new ModelAndView();
		JobConfig jobConfig = new JobConfig(id, getJobService().getJob(id).getName(),getJobService().getJob(id).getDescription());
		mav.addObject("job",jobConfig);
	//	mav.addObject("schedules", getScheduleService().getSchedules(jobConfig.getName()));
		mav.setViewName("edit-job");
		return mav;
	}
	
	@RequestMapping(value = "/job/{id}/edit/schedule", method = RequestMethod.POST)
	public ModelAndView editSchedule(@PathVariable int id, JobConfig job) {
		ModelAndView mav = new ModelAndView();
		getJobService().editJob(id, job.getName(), job.getDescription());
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

	@RequestMapping(value = "/job/add/schedule", method = RequestMethod.GET)
	public ModelAndView addSchedule() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("job", new JobConfig());
		mav.setViewName("add-schedule");
		return mav;
	}
}
