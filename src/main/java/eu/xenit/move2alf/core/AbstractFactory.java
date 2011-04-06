package eu.xenit.move2alf.core;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

public abstract class AbstractFactory<T extends ConfigurableObject> {

	private static final Logger logger = LoggerFactory
			.getLogger(AbstractFactory.class);
	
	private Map<String, T> objectMap = new HashMap<String, T>();
	
	private Map<String, List<T>> objectMapByCategory = new HashMap<String, List<T>>();

	public AbstractFactory() {
		super();
	}

	public void rescan() {
		logger.info("Scanning for objects (" + this.getClass() + ")");
		scanForClasses("eu.xenit");
		scanForClasses("be.pv");
	}

	private void scanForClasses(String basePackage) {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
				false);
		provider.addIncludeFilter(getTypeFilter());
		Set<BeanDefinition> components = provider.findCandidateComponents(basePackage);
		for (BeanDefinition component : components) {
			try {
				logger.debug("Loading " + component.getBeanClassName());
				T action = (T) Class.forName(component.getBeanClassName())
						.getConstructor().newInstance();
				initializeObject(action);
				objectMap.put(component.getBeanClassName(), action);
				
				String category = action.getCategory();
				List<T> categoryList = objectMapByCategory.get(category);
				if (categoryList == null) {
					categoryList = new ArrayList<T>();
					objectMapByCategory.put(category, categoryList);
				}
				categoryList.add(action);
				logger.debug("Adding object \"" + action.getName() + "\" to category \"" + category + "\"");
				
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

	protected abstract AssignableTypeFilter getTypeFilter();

	protected abstract void initializeObject(T object);

	public Collection<T> getCollection() {
		return objectMap.values();
	}

	public Set<String> getClassNames() {
		return objectMap.keySet();
	}

	public T getObject(String className) {
		T object = objectMap.get(className);
		if (object == null) {
			logger.error("Object for class " + className + " not found");
			throw new IllegalArgumentException("Object for class " + className + " not found");
		}
		return object;
	}
	
	public List<T> getObjectsByCategory(String category) {
		return objectMapByCategory.get(category);
	}

	@PostConstruct
	public void init() {
		rescan();
	}

}