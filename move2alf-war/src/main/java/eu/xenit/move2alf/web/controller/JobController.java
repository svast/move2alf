package eu.xenit.move2alf.web.controller;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.action.ActionClassInfoService;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.ProcessedDocument;
import eu.xenit.move2alf.core.dto.Resource;
import eu.xenit.move2alf.core.enums.ECycleState;
import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;
import eu.xenit.move2alf.core.sharedresource.alfresco.InputSource;
import eu.xenit.move2alf.logic.DestinationService;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.web.controller.destination.AlfrescoDestinationTypeController;
import eu.xenit.move2alf.web.controller.destination.CastorDestinationTypeController;
import eu.xenit.move2alf.web.controller.destination.model.ContentStoreModel;
import eu.xenit.move2alf.web.dto.HistoryInfo;
import eu.xenit.move2alf.web.dto.JobModel;
import eu.xenit.move2alf.web.dto.ScheduleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

@Controller
public class JobController extends AbstractController{

	private static final Logger logger = LoggerFactory
			.getLogger(JobController.class);

	private JobService jobService;
    private ActionClassInfoService actionClassService;

    public ActionClassInfoService getActionClassService() {
        return actionClassService;
    }

    @Autowired
    public void setActionClassService(ActionClassInfoService actionClassService) {
        this.actionClassService = actionClassService;
    }

    @Autowired
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	public JobService getJobService() {
		return jobService;
	}


	@RequestMapping("/job/dashboard")
	public ModelAndView dashboard() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("jobInfoList", getJobService().getAllJobInfo());
		mav.addObject("role", getRole());
		mav.setViewName("dashboard");
		return mav;
	}

	@RequestMapping(value = "/job/create", method = RequestMethod.GET)
	public ModelAndView createJobForm() {
		ModelAndView mav = new ModelAndView("create-job");
		mav.addObject("job", new JobModel());
		
		jobModel(mav);

		return mav;
	}

	@RequestMapping(value = "/job/create", method = RequestMethod.POST)
	public ModelAndView createJob(@ModelAttribute("job") @Valid JobModel job,
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
		int jobId = getJobService().createJob(job).getId();

		for (int i = 0; i < cronJobs.size(); i++) {
			getJobService().createSchedule(jobId, cronJobs.get(i));
		}

		mav.setViewName("redirect:/job/dashboard");
		return mav;
	}

    @Autowired
    private DestinationService destinationService;

	private void jobModel(ModelAndView mav) {
        List<ContentStoreModel> contentStores = new ArrayList<ContentStoreModel>();
        contentStores.add(new ContentStoreModel("Default Alfresco content store", -1));
        for (Resource resource: destinationService.getDestinationsForClassId(CastorDestinationTypeController.CLASS_ID)){
            contentStores.add(new ContentStoreModel(resource.getName(), resource.getId()));
        }
        mav.addObject("contentStores", contentStores);
		mav.addObject("destinations", destinationService.getDestinationsForClassId(AlfrescoDestinationTypeController.CLASS_ID));
		mav.addObject("metadataOptions", getActionClassService().getClassesForCategory(ConfigurableObject.CAT_METADATA));
		mav.addObject("transformOptions", getActionClassService().getClassesForCategory(ConfigurableObject.CAT_TRANSFORM));
		mav.addObject("role", getRole());
	}

	private void jobValidation(JobModel job, BindingResult errors) {
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

        if(job.getInputSource() == InputSource.FILESYSTEM){
           if(job.getInputFolder() == null || job.getInputFolder().isEmpty()){
               errors.addError(new FieldError("job", "inputPath", "You should at least enter 1 inputfolder, or use another input source type."));
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

		List<String> inputFolder = job.getInputFolder();
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
	public ModelAndView editJobForm(@PathVariable("id") int id) {
        if(getJobService().getJobState(id) == ECycleState.RUNNING){
            Map<String, Object> model = new HashMap<String, Object>();
            return new ModelAndView("redirect:/job/dashboard", model);
        }
		ModelAndView mav = new ModelAndView("edit-job");
		JobModel jobModel = getJobService().getJobConfigForJob(id);
		
		mav.addObject("job", jobModel);
		
		jobModel(mav);
		
		return mav;
	}

    @RequestMapping(value = "/job/{id}/stop", method = RequestMethod.GET)
    public ModelAndView stopJob(@PathVariable("id") int id){
        getJobService().stopJob(id);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/job/dashboard");
        return mav;
    }

	@RequestMapping(value = "/job/{id}/edit", method = RequestMethod.POST)
	public ModelAndView editJob(@PathVariable int id,
			@ModelAttribute("job") @Valid JobModel job, BindingResult errors) {

		if (!getJobService().getJob(id).getName().equals(job.getName()) && getJobService().checkJobExists(job.getName())) {
			errors.addError(new FieldError("job", "name", "The name you entered is the name of another job!"));
		}

        if (getJobService().getJobState(id) == ECycleState.RUNNING){
            errors.addError(new ObjectError("job", "The job you want to save is currently running. Please wait until the job has finished."));
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
		
		ModelAndView mav = new ModelAndView("redirect:/job/dashboard");
		Job editedJob = getJobService().editJob(job);

		handleSchedule(id, job.getCron());
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
		JobModel jobModel = new JobModel();
		jobModel.setId(id);
		jobModel.setName(getJobService().getJob(id).getName());
		mav.addObject("jobConfig", jobModel);
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
		final Long nrOfWarnDocuments = getJobService().countProcessedDocumentsWithStatus(cycleId,
				EProcessedDocumentStatus.WARN);
		final Long nrOfInfoDocuments = getJobService().countProcessedDocumentsWithStatus(cycleId,
				EProcessedDocumentStatus.INFO);
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
		mav.addObject("nrOfInfoDocuments", nrOfInfoDocuments);
		mav.addObject("nrOfWarnDocuments", nrOfWarnDocuments);
		//mav.addObject("progress", progress);
		mav.setViewName(viewName);
		return mav;
	}

	@RequestMapping("/job/{jobId}/history")
	public ModelAndView history(@PathVariable int jobId,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();

		List<HistoryInfo> historyInfoList = getJobService().getHistory(jobId);

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
	public ModelAndView runPoller(@PathVariable("jobId") int jobId) {
		ModelAndView mav = new ModelAndView();

		getJobService().scheduleNow(jobId);

		mav.setViewName("redirect:/job/dashboard");
		return mav;
	}
}
