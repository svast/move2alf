package eu.xenit.move2alf.core;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.core.dto.ConfiguredAction;

@Service("actionFactory")
public class ActionFactory extends AbstractFactory<Action> {

	private SourceSinkFactory sourceSinkFactory;
	
	@Autowired
	public void setSourceSinkFactory(SourceSinkFactory sourceSinkFactory) {
		this.sourceSinkFactory = sourceSinkFactory;
	}

	public SourceSinkFactory getSourceSinkFactory() {
		return sourceSinkFactory;
	}

	@Override
	protected AssignableTypeFilter getTypeFilter() {
		return new AssignableTypeFilter(Action.class);
	}

	@Override
	protected void initializeObject(Action object) {
		object.setActionFactory(this);
		object.setSourceSinkFactory(getSourceSinkFactory());
	}

	public void execute(ConfiguredAction nextAction,
			Map<String, Object> parameterMap) {
		getObject(nextAction.getClassName()).execute(nextAction, parameterMap);
	}
}
