package eu.xenit.move2alf.web.controller.destination;

import eu.xenit.move2alf.classloading.ClasspathScanner;
import eu.xenit.move2alf.core.AbstractClassInfoService;
import eu.xenit.move2alf.core.action.ClassInfoModel;
import eu.xenit.move2alf.logic.DestinationService;
import eu.xenit.move2alf.logic.ObjectFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/3/13
 * Time: 11:42 AM
 */
@Service
public class ResourceTypeClassInfoService extends AbstractClassInfoService implements ApplicationContextAware {

    public static final String CATEGORY_DESTINATION = "Destination";
    @Autowired
    private DestinationService destinationService;
    private AutowireCapableBeanFactory beanFactory;

    @Override
    protected Class<?> getTargetType() {
        return DestinationTypeController.class;
    }

    @PostConstruct
    public void init() {
        scanForClasses("eu.xenit");
        instantiateClassInfoModels();
    }

    protected void instantiateClassInfoModels() {
        for (ClassInfoModel model : getAllClassInfoModels()) {
            ObjectFactory objectFactory = new ObjectFactory(model.getClazz(), new HashMap<String, String>(), beanFactory);
            destinationTypeMap.put(model.getClassId(), (DestinationTypeController) objectFactory.createObject());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    @Autowired
    @Override
    public void setClasspathScanner(ClasspathScanner classpathScanner) {}
}
