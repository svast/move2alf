package eu.xenit.move2alf.core.action;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.core.AbstractFactory;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.sourcesink.SourceSinkFactory;
import eu.xenit.move2alf.logic.JobService;

@Service("actionFactory")
public class ActionFactory extends AbstractFactory<Action> {

	private SourceSinkFactory sourceSinkFactory;
	
	private JobService jobService;
	
	private SessionFactory sessionFactory;
	
	@Autowired
	public void setSourceSinkFactory(SourceSinkFactory sourceSinkFactory) {
		this.sourceSinkFactory = sourceSinkFactory;
	}

	public SourceSinkFactory getSourceSinkFactory() {
		return sourceSinkFactory;
	}

	@Autowired
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	public JobService getJobService() {
		return jobService;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	protected AssignableTypeFilter getTypeFilter() {
		return new AssignableTypeFilter(Action.class);
	}

	@Override
	protected void initializeObject(Action object) {
		object.setActionFactory(this);
		object.setSourceSinkFactory(getSourceSinkFactory());
		object.setJobService(getJobService());
		object.setSessionFactory(getSessionFactory());
	}

	public void execute(ConfiguredAction nextAction,
			Map<String, Object> parameterMap) {
		getObject(nextAction.getClassId()).execute(nextAction, parameterMap);
	}

}
