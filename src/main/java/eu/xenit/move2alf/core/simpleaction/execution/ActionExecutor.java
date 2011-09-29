package eu.xenit.move2alf.core.simpleaction.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.simpleaction.SimpleAction;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.logic.JobExecutionServiceImpl.ErrorHandler;
import eu.xenit.move2alf.web.dto.JobConfig;

public class ActionExecutor {

	private final static Logger logger = LoggerFactory
			.getLogger(ActionExecutor.class);

	private final CompletionService<List<FileInfo>> completionService;

	private final static ExecutorService defaultExecutorService = Executors
			.newSingleThreadExecutor();

	public ActionExecutor() {
		this(defaultExecutorService);
	}

	public ActionExecutor(ExecutorService executorService) {
		this.completionService = new ExecutorCompletionService<List<FileInfo>>(
				executorService);
	}

	public List<FileInfo> execute(List<FileInfo> input, JobConfig jobConfig,
			Cycle cycle, SimpleAction action, ActionConfig config,
			ErrorHandler errorHandler) {
		List<FileInfo> output = new ArrayList<FileInfo>();
		for (FileInfo parameterMap : input) {
			completionService.submit(new ActionCallable(action, parameterMap,
					config, errorHandler, jobConfig, cycle));
		}

		for (int i = 0; i < input.size(); i++) {
			try {
				Future<List<FileInfo>> futureOutput = completionService.take();
				output.addAll(futureOutput.get());
			} catch (InterruptedException e) {
				logger.error("Thread interrupted", e);
			} catch (ExecutionException e) {
				Throwable t = e.getCause();
				if (t instanceof Move2AlfException) {
					// action failed, no output
				} else {
					throw Util.launderThrowable(t);
				}
			}
		}
		return output;
	}

	class ActionCallable implements Callable<List<FileInfo>> {

		private final SimpleAction simpleAction;
		private final FileInfo fileInfo;
		private final ActionConfig actionConfig;
		private final ErrorHandler errorHandler;
		private final JobConfig jobConfig;
		private final Cycle cycle;

		public ActionCallable(SimpleAction action, FileInfo fileInfo,
				ActionConfig config, ErrorHandler errorHandler,
				JobConfig jobConfig, Cycle cycle) {
			this.simpleAction = action;
			this.fileInfo = fileInfo;
			this.actionConfig = config;
			this.errorHandler = errorHandler;
			this.jobConfig = jobConfig;
			this.cycle = cycle;
		}

		@Override
		public List<FileInfo> call() {
			try {
				return simpleAction.execute(fileInfo, actionConfig);
			} catch (Exception e) {
				errorHandler.handleError(fileInfo, jobConfig, cycle, e);
				logger.error("Error in action " + simpleAction.getClass().getName(), e);
				throw new Move2AlfException();
			}
		}
	}
}
