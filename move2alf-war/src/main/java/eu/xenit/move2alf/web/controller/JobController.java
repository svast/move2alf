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

import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;
import eu.xenit.move2alf.logic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.ConfiguredObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.ProcessedDocument;
import eu.xenit.move2alf.core.enums.EDestinationParameter;
import eu.xenit.move2alf.core.sourcesink.SourceSink;
import eu.xenit.move2alf.core.sourcesink.SourceSinkFactory;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.logic.PipelineAssembler;
import eu.xenit.move2alf.logic.usageservice.UsageService;
import eu.xenit.move2alf.web.dto.DestinationConfig;
import eu.xenit.move2alf.web.dto.HistoryInfo;
import eu.xenit.move2alf.web.dto.JobConfig;
import eu.xenit.move2alf.web.dto.ScheduleConfig;

@Controller
public class JobController extends AbstractController{

	private static final Logger logger = LoggerFactory
			.getLogger(JobController.class);

	private JobService jobService;
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
		mav.addObject("role", getRole());
			
		// license info
		mav.addObject("licenseIsValid", getUsageService().isValid());
		mav.addObject("licenseValidationFailureCause", getUsageService().getValidationFailureCause());
		mav.addObject("licensee", getUsageService().getLicensee());
		mav.addObject("expirationDate", getUsageService().getExpirationDate());
		mav.addObject("documentCounter", getUsageService().getDocumentCounter());
		mav.addObject("totalNumberOfDocuments", getUsageService().getTotalNumberOfDocuments());
		
		mav.setViewName("dashboard");
		return mav;
	}

	@RequestMapping(value = "/job/create", method = RequestMethod.GET)
	public ModelAndView createJobForm() {
		ModelAndView mav = new ModelAndView("create-job");
		mav.addObject("job", new JobConfig());
		
		jobModel(mav);

		return mav;
	}

	@RequestMapping(value = "/job/create", method = RequestMethod.POST)
	public ModelAndView createJob(@ModelAttribute("job") @Valid JobConfig job,
			BindingResult errors) {
		
		if(getJobService().checkJobExists(job.getName())){
			errors.addError(new FieldError("job", "name", "The name you entered already exists."));
		}
		

		jobValidation(job, errors);

		if (errors.hasErrors()) {
			System.out.println("THE ERRORS: " + errors.toString());
			ModelAndView mav = new ModelAndView("create-job");
			mav.addObject("job", job);
			mav.addObject("errors", errors.getFieldErrors());
			
			jobModel(mav);

			return mav;
		}

		ModelAndView mav = new ModelAndView();
		List<String> cronJobs = job.getCron();
		int jobId = getJobService().createJob(job.getName(),
				job.getDescription()).getId();

		for (int i = 0; i < cronJobs.size(); i++) {
			getJobService().createSchedule(jobId, cronJobs.get(i));
		}

		int destId = job.getDest();

		job.setId(jobId);
		job.setDest(destId);
		getPipelineAssembler().getPipeline(job);

        //TODO START AND CREATE JOB

		mav.setViewName("redirect:/job/dashboard");
		return mav;
	}

	private void jobModel(ModelAndView mav) {
		mav.addObject("destinations", getJobService()
				.getAllDestinationConfiguredSourceSinks());
		mav.addObject("metadataOptions", getJobService()
				.getActionsByCategory(ConfigurableObject.CAT_METADATA));
		mav.addObject("transformOptions", getJobService()
				.getActionsByCategory(ConfigurableObject.CAT_TRANSFORM));
		mav.addObject("destinationOptions", getJobService()
				.getSourceSinksByCategory(
						ConfigurableObject.CAT_DESTINATION));
		mav.addObject("role", getRole());
	}

	private void jobValidation(JobConfig job, BindingResult errors) {
		List<String> metadata = job.getParamMetadata();
		Set<String> uniqueMetadataNames = new HashSet<String>();
		if (metadata != null) {
			for (int i = 0; i < metadata.size(); i++) {
				String[] metadataElements = metadata.get(i).split("\\|");
				if (metadataElements != null) {
					boolean metadataUnique = uniqueMetadataNames
							.add(metadataElements[0]);
					if (metadataUnique == false) {
						errors.addError(new FieldError("job", "paramMetadata", "The metadata parameters you entered are not unique."));
					}
				}
			}
		}
		

		List<String> transform = job.getParamTransform();
		Set<String> uniqueTransformNames = new HashSet<String>();
		if (transform != null) {
			for (int i = 0; i < transform.size(); i++) {
				String[] transformElements = transform.get(i).split("\\|");
				if (transformElements != null) {
					boolean transformUnique = uniqueTransformNames
							.add(transformElements[0]);
					if (transformUnique == false) {
						errors.addError(new FieldError("job", "paramTransform", "The transformation parameters you entered are not unique."));
					}
				}
			}
		}

		List<String> inputFolder = job.getInputFolders();
		Set<String> uniqueInputFolders = new HashSet<String>();
		if (inputFolder != null) {
			for (int i = 0; i < inputFolder.size(); i++) {
				boolean inputFolderUnique = uniqueInputFolders.add(inputFolder
						.get(i));
				if (inputFolderUnique == false) {
					errors.addError(new FieldError("job", "inputFolder", "The inputfolders you entered are not unique."));
				}
			}
		}
	}

	@RequestMapping(value = "/job/{id}/edit", method = RequestMethod.GET)
	public ModelAndView editJobForm(@PathVariable int id) {
		ModelAndView mav = new ModelAndView("edit-job");
		JobConfig jobConfig = getPipelineAssembler().getJobConfigForJob(id);
		
		mav.addObject("job", jobConfig);
		
		jobModel(mav);
		
		return mav;
	}

	@RequestMapping(value = "/job/{id}/edit", method = RequestMethod.POST)
	public ModelAndView editJob(@PathVariable int id,
			@ModelAttribute("job") @Valid JobConfig job, BindingResult errors) {

		if (!getJobService().getJob(id).getName().equals(job.getName()) && getJobService().checkJobExists(job.getName())) {
			errors.addError(new FieldError("job", "name", "The name you entered is the name of another job!"));
		}
		
		jobValidation(job, errors);

		if (errors.hasErrors()) {
			System.out.println("THE ERRORS: " + errors.toString());
			ModelAndView mav = new ModelAndView("edit-job");
			mav.addObject("job", job);
			mav.addObject("errors", errors.getFieldErrors());
			
			jobModel(mav);

			return mav;
		}
		
		ModelAndView mav = new ModelAndView();
		Job editedJob = getJobService().editJob(id, job.getName(),
				job.getDescription());
		int jobId = editedJob.getId();

		handleSchedule(id, job.getCron());

		int destId = job.getDest();

		/*
		 * Create new action pipeline and remove old one by deleting first
		 * action. The delete will be cascaded by Hibernate.
		 * 
		 * TODO: delete FileSourceSink
		 */

		job.setId(jobId);
		job.setDest(destId);
		getPipelineAssembler().getPipeline(job);
        //TODO SAVE AND CREATE JOB

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
		mav.addObject("role", getRole());
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
			mav.addObject("role", getRole());
			return mav;
		}

		ModelAndView mav = new ModelAndView();

		List<String> cronJobs = job.getCron();
		handleSchedule(id, cronJobs);
		mav.setViewName("redirect:/job/dashboard");
		return mav;
	}

	
	@RequestMapping(value = "/job/{id}/delete", method = RequestMethod.GET)
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
					destination.getClassId());
			sourceSinkNames.put(destination.getClassId(), sourceSink
					.getName());
		}

		mav.addObject("destinations", destinations);
		mav.addObject("typeNames", sourceSinkNames);
		mav.addObject("role", getRole());
		mav.setViewName("manage-destinations");
		return mav;
	}

	@RequestMapping(value = "/destination/create", method = RequestMethod.GET)
	public ModelAndView createDestinationsForm() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("destination", new DestinationConfig());
		mav.addObject("destinationOptions", getJobService()
				.getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
		mav.addObject("role", getRole());
		mav.addObject("showDestinations", "false");
		mav.setViewName("create-destination");
		return mav;
	}

	@RequestMapping(value = "/destination/create", method = RequestMethod.POST)
	public ModelAndView createDestination(
			@ModelAttribute("destination") @Valid DestinationConfig destination,
			BindingResult errors) {

		if(errors.hasErrors()){
			ModelAndView mav = new ModelAndView("create-destination");
			mav.addObject("destination", destination);
			mav.addObject("destinationOptions", getJobService()
					.getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
			mav.addObject("role", getRole());
			mav.addObject("errors", errors.getFieldErrors());
			return mav;	
		}
		ModelAndView mav = new ModelAndView();
		
		HashMap<EDestinationParameter, Object> destinationParams = new HashMap<EDestinationParameter, Object>();
		destinationParams.put(EDestinationParameter.NAME, destination.getDestinationName());
		destinationParams.put(EDestinationParameter.URL, destination.getDestinationURL());
		destinationParams.put(EDestinationParameter.USER, destination.getAlfUser());
		destinationParams.put(EDestinationParameter.PASSWORD, destination.getAlfPswd());
		destinationParams.put(EDestinationParameter.THREADS, Integer.toString(destination.getNbrThreads()));
		getJobService().createDestination(destination.getDestinationType(), destinationParams);

		mav.setViewName("redirect:/destinations");
		return mav;
	}

	@RequestMapping(value = "/destination/{id}/edit", method = RequestMethod.GET)
	public ModelAndView editDestinationForm(@PathVariable int id) {
		ModelAndView mav = makeEditDestinationModelAndView(id);
		return mav;
	}

	private ModelAndView makeEditDestinationModelAndView(int id) {
		ModelAndView mav = new ModelAndView();
		ConfiguredObject destination = getJobService().getConfiguredSourceSink(id);
		DestinationConfig destinationConfig = new DestinationConfig();
		
		destinationConfig.setDestinationName(destination.getParameter("name"));
		destinationConfig.setDestinationType(destination.getClassId());
		destinationConfig.setDestinationURL(destination.getParameter("url"));
		destinationConfig.setAlfUser(destination.getParameter("user"));
		destinationConfig.setAlfPswd(destination.getParameter("password"));
		destinationConfig.setNbrThreads(Integer.parseInt(destination.getParameter("threads")));
		
		mav.addObject("destination", destinationConfig);
		mav.addObject("destinationId", id);
		mav.addObject("destinationOptions", getJobService()
				.getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
		mav.addObject("role", getRole());
		mav.setViewName("edit-destination");
		return mav;
	}

	@RequestMapping(value = "/destination/{id}/edit", method = RequestMethod.POST)
	public ModelAndView editDestination(
			@PathVariable int id,
			@ModelAttribute("destination") @Valid DestinationConfig destination,
			BindingResult errors) {

		boolean destinationExists = false;
		if (!getJobService().getConfiguredSourceSink(id).getParameter("name")
				.equals(destination.getDestinationName())) {
			destinationExists = getJobService().checkDestinationExists(
					destination.getDestinationName());
		}

		if (errors.hasErrors() || destinationExists == true) {
			System.out.println("THE ERRORS: " + errors.toString());

			ModelAndView mav = makeEditDestinationModelAndView(id);
			
			mav.addObject("destinationExists", destinationExists);
			mav.addObject("errors", errors.getFieldErrors());
			return mav;
		}

		ModelAndView mav = new ModelAndView();

		HashMap<EDestinationParameter, Object> destinationParams = new HashMap<EDestinationParameter, Object>();

		destinationParams.put(EDestinationParameter.NAME, destination
				.getDestinationName());
		destinationParams.put(EDestinationParameter.URL, destination
				.getDestinationURL());
		destinationParams.put(EDestinationParameter.USER, destination
				.getAlfUser());
		destinationParams.put(EDestinationParameter.PASSWORD, destination
				.getAlfPswd());
		destinationParams.put(EDestinationParameter.THREADS, destination
				.getNbrThreads());
		getJobService().editDestination(id, destination.getDestinationType(),
				destinationParams);

		mav.setViewName("redirect:/destinations");
		return mav;
	}

	@RequestMapping(value = "/destination/{id}/delete", method = RequestMethod.GET)
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
				.getIntParameter(request, "count", 50);
		return reportWithView(jobId, cycleId, page * pageSize, pageSize,
				"report");
	}

	@RequestMapping("/job/{jobId}/{cycleId}/report/exportcsv")
	public ModelAndView exportReportToCSV(@PathVariable int jobId,
			@PathVariable int cycleId, HttpServletResponse response) {
		response.setHeader("Content-Disposition", "attachment;filename=\"report.csv\"" );
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

		final Long documentListSize = getJobService().countProcessedDocuments(cycleId);
		final Long nrOfFailedDocuments = getJobService().countProcessedDocumentsWithStatus(cycleId,
				EProcessedDocumentStatus.FAILED);
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

		//final List<PipelineStepProgress> progress = getJobExecutionService().getProgress(cycleId);

		// easiest way to keep the pagination links, not the cleanest
		PagedListHolder<ProcessedDocument> pagedListHolder = new PagedListHolder<ProcessedDocument>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

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
				if(getPage()-5 <= 0)
					return 0;
				return getPage()-5;
			}
			
			@Override
			public int getLastLinkedPage() {
				if(getPage()+4 > getPageCount()-1)
					return getPageCount() - 1;
				return getPage()+4;
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
		mav.addObject("role", getRole());
		mav.addObject("documentListSize", documentListSize);
		mav.addObject("docsPerSecond", docsPerSecond);
		mav.addObject("nrOfFailedDocuments", nrOfFailedDocuments);
		//mav.addObject("progress", progress);
		mav.setViewName(viewName);
		return mav;
	}

	@RequestMapping("/job/{jobId}/history")
	public ModelAndView history(@PathVariable int jobId,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();

		List<HistoryInfo> historyInfoList = new ArrayList<HistoryInfo>();
		historyInfoList = getJobService().getHistory(jobId);

		PagedListHolder pagedListHolder = new PagedListHolder(historyInfoList);
		int page = ServletRequestUtils.getIntParameter(request, "p", 0);
		pagedListHolder.setPage(page);
		int pageSize = 10;
		pagedListHolder.setPageSize(pageSize);

		mav.addObject("job", getJobService().getJob(jobId));
		mav.addObject("pagedListHolder", pagedListHolder);
		mav.addObject("historyInfoList", historyInfoList);
		mav.addObject("role", getRole());
		mav.setViewName("history");
		return mav;
	}

	@RequestMapping("/job/{jobId}/cycle/run")
	public ModelAndView runPoller(@PathVariable int jobId) {
		ModelAndView mav = new ModelAndView();

		getJobService().scheduleNow(jobId);

		mav.setViewName("redirect:/job/dashboard");
		return mav;
	}
}
