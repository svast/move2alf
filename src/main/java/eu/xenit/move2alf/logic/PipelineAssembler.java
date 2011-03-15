package eu.xenit.move2alf.logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import eu.xenit.move2alf.core.ConfiguredObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.web.dto.JobConfig;

@Transactional
public abstract class PipelineAssembler extends AbstractHibernateService {

	private static final Logger logger = LoggerFactory
			.getLogger(PipelineAssembler.class);

	private JobService jobService;

	public abstract void assemblePipeline(JobConfig jobConfig);

	protected void assemble(JobConfig jobConfig,
			ActionBuilder... actionBuilders) {
		logger.debug("Assembling pipeline: " + actionBuilders.length + " actions");
		ConfiguredAction firstAction = null;
		ConfiguredAction prevAction = null;
		for (ActionBuilder actionBuilder : actionBuilders) {
			ConfiguredAction action = actionBuilder.build();
			if (firstAction == null) {
				firstAction = action;
			}
			if (prevAction != null) {
				logger.debug("\tSetting configured action on success: " + prevAction.getClassName() + " -> " + action.getClassName());
				prevAction.setAppliedConfiguredActionOnSuccess(action);
			}
			prevAction = action;
		}
		getSessionFactory().getCurrentSession().save(firstAction);
		Job job = getJobService().getJob(jobConfig.getId());
		job.setFirstConfiguredAction(firstAction);
		getSessionFactory().getCurrentSession().update(job);
	}

	protected ActionBuilder action(String className) {
		return new ActionBuilder().setClassName(className);
	}

	protected SourceSinkBuilder sourceSink(String className) {
		return new SourceSinkBuilder().setClassName(className);
	}
	
	protected SourceSinkBuilder sourceSinkById(final int id) {
		return new SourceSinkBuilder() {
			@Override
			ConfiguredSourceSink build() {
				return getJobService().getDestination(id);
			}
		};
	}

	@Autowired
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	public JobService getJobService() {
		return jobService;
	}

	protected abstract class ConfiguredObjectBuilder<T extends ConfiguredObjectBuilder<T>> {
		protected String className;
		protected Map<String, String> params = new HashMap<String, String>();

		@SuppressWarnings("unchecked")
		T setClassName(String className) {
			this.className = className;
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		T param(String key, String value) {
			params.put(key, value);
			return (T) this;
		}

		abstract ConfiguredObject build();
	}

	protected class ActionBuilder extends
			ConfiguredObjectBuilder<ActionBuilder> {
		protected Set<ConfiguredSourceSink> configuredSourceSinkSet = new HashSet<ConfiguredSourceSink>();

		@Override
		ConfiguredAction build() {
			ConfiguredAction action = new ConfiguredAction();
			action.setClassName(this.className);
			action.setParameters(this.params);
			action.setConfiguredSourceSinkSet(this.configuredSourceSinkSet);
			return action;
		}

		ActionBuilder sourceSink(SourceSinkBuilder sourceSinkBuilder) {
			this.configuredSourceSinkSet.add(sourceSinkBuilder.build());
			return this;
		}

	}

	protected class SourceSinkBuilder extends
			ConfiguredObjectBuilder<SourceSinkBuilder> {
		@Override
		ConfiguredSourceSink build() {
			ConfiguredSourceSink sourceSink = new ConfiguredSourceSink();
			sourceSink.setClassName(this.className);
			sourceSink.setParameters(this.params);
			return sourceSink;
		}
	}

	public abstract JobConfig getJobConfigForJob(int id);
}
