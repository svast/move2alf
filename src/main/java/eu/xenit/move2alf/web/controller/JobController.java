package eu.xenit.move2alf.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import eu.xenit.move2alf.logic.JobService;

@Controller
public class JobController {

	private static final Logger logger = LoggerFactory
			.getLogger(JobController.class);

	private JobService jobService;

	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	public JobService getJobService() {
		return jobService;
	}

	@RequestMapping("/job/dashboard")
	public ModelAndView dashboard() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("jobs", getJobService().getAllJobs());
		mav.setViewName("dashboard");
		return mav;
	}
}
