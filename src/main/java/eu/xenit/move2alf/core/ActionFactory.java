package eu.xenit.move2alf.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

@Service("actionFactory")
public class ActionFactory {
	private static final Logger logger = LoggerFactory
			.getLogger(ActionFactory.class);

	private Map<String, Action> actionMap = new HashMap<String, Action>();

	@PostConstruct
	public void rescanActions() {
		logger.info("Loading actions");
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
				false);
		provider.addIncludeFilter(new AssignableTypeFilter(Action.class));
		Set<BeanDefinition> components = provider.findCandidateComponents("");
		for (BeanDefinition component : components) {
			logger.debug("Action: " + component.getBeanClassName());
			try {
				Action action = (Action) Class
						.forName(component.getBeanClassName()).getConstructor().newInstance();
				action.setActionFactory(this);
				actionMap.put(component.getBeanClassName(), action);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Collection<Action> getActionCollection() {
		return actionMap.values();
	}

	public Set<String> getActionClassNames() {
		return actionMap.keySet();
	}

	public Action getAction(String className) {
		return actionMap.get(className);
	}

}
