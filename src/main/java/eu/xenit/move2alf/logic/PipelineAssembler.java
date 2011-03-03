package eu.xenit.move2alf.logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import eu.xenit.move2alf.core.ConfiguredObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.web.dto.JobConfig;

@Transactional
public abstract class PipelineAssembler extends AbstractHibernateService {
	
	public abstract void assemblePipeline(JobConfig jobConfig);
	
	protected void assemble(ActionBuilder... actionBuilders) {
		ConfiguredAction firstAction = null;
		ConfiguredAction prevAction = null;
		for(ActionBuilder actionBuilder : actionBuilders) {
			ConfiguredAction action = actionBuilder.build();
			if (prevAction != null) {
				action.setAppliedConfiguredActionOnSuccess(prevAction);
				prevAction = action;
			} else {
				firstAction = action;
			}
		}
		getSessionFactory().getCurrentSession().save(firstAction);
	}

	protected ActionBuilder action(String className) {
		return new ActionBuilder().setClassName(className);
	}

	protected SourceSinkBuilder sourceSink(String className) {
		return new SourceSinkBuilder().setClassName(className);
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

	protected class ActionBuilder extends ConfiguredObjectBuilder<ActionBuilder> {
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

	protected class SourceSinkBuilder extends ConfiguredObjectBuilder<SourceSinkBuilder> {
		@Override
		ConfiguredSourceSink build() {
			ConfiguredSourceSink sourceSink = new ConfiguredSourceSink();
			sourceSink.setClassName(this.className);
			sourceSink.setParameters(this.params);
			return sourceSink;
		}
	}
}
