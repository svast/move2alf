package eu.xenit.move2alf.core.simpleaction.execution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import eu.xenit.move2alf.core.cyclestate.CycleStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.simpleaction.SimpleAction;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.logic.ErrorHandler;
import eu.xenit.move2alf.logic.SuccessHandler;
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

	public ActionExecutor(final ExecutorService executorService) {
		this.completionService = new ExecutorCompletionService<List<FileInfo>>(
				executorService);
	}

	public List<FileInfo> execute(final List<FileInfo> input,
			final JobConfig jobConfig, final Cycle cycle,
			final SimpleAction action, final ActionConfig config,
			final SuccessHandler successHandler, final ErrorHandler errorHandler, final Map<String, Serializable> state) {
		final List<FileInfo> output = new ArrayList<FileInfo>();
		for (final FileInfo parameterMap : input) {
			parameterMap.put(Parameters.PARAM_CYCLE, cycle.getId());
			completionService.submit(new ActionCallable(action, parameterMap,
					config, errorHandler, jobConfig, cycle, state));
		}
		final List<FileInfo> stateInitializationOutput = action.initializeState(config, state);
		if (stateInitializationOutput != null) {
			output.addAll(stateInitializationOutput);
		}
		for (int i = 0; i < input.size(); i++) {
			try {
				if ((i + 1) % 100 == 0) {
					logger.info("Processed " + (i + 1) + " files out of "
							+ input.size() + " (" + 100 * (float) (i + 1)
							/ input.size() + "%)");
				}
				final Future<List<FileInfo>> futureOutput = completionService
						.take();
				final List<FileInfo> fileInfos = futureOutput.get();
				if (fileInfos != null) {
					output.addAll(fileInfos);
				}
				for (final FileInfo fileInfo : fileInfos) {
					if (successHandler != null) {
						successHandler
								.handleSuccess(fileInfo, jobConfig, cycle);
					}
				}
			} catch (final InterruptedException e) {
				logger.error("Thread interrupted", e);
			} catch (final ExecutionException e) {
				final Throwable t = e.getCause();
				if (t instanceof Move2AlfException) {
					// action failed, no output
				} else {
					throw Util.launderThrowable(t);
				}
			}
		}
		final List<FileInfo> stateCleanupOutput = action.cleanupState(config, state);
		if (stateCleanupOutput != null) {
			output.addAll(stateCleanupOutput);
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
        private final Map<String, Serializable> state;

		public ActionCallable(final SimpleAction action,
				final FileInfo fileInfo, final ActionConfig config,
				final ErrorHandler errorHandler, final JobConfig jobConfig,
				final Cycle cycle, final Map<String, Serializable> state) {
			this.simpleAction = action;
			this.fileInfo = fileInfo;
			this.actionConfig = config;
			this.errorHandler = errorHandler;
			this.jobConfig = jobConfig;
			this.cycle = cycle;
            this.state = state;
		}

		@Override
		public List<FileInfo> call() {
			try {
				return simpleAction.execute(fileInfo, actionConfig, state);
			} catch (final Exception e) {
				if (errorHandler != null) {
					errorHandler.handleError(fileInfo, jobConfig, cycle, e);
				}
				logger.error("Error in action "
						+ simpleAction.getClass().getName(), e);
				throw new Move2AlfException();
			}
		}
	}
}
