package eu.xenit.move2alf.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

@Service("actionFactory")
public class ActionFactory extends AbstractFactory<Action> {

	private static final Logger logger = LoggerFactory
			.getLogger(ActionFactory.class);
	
	@Override
	protected AssignableTypeFilter getTypeFilter() {
		return new AssignableTypeFilter(Action.class);
	}

	@Override
	protected void initializeObject(Action object) {
		object.setActionFactory(this);
	}
}
