package eu.xenit.move2alf.web.controller.destination;

import eu.xenit.move2alf.core.AbstractClassInfoService;
import eu.xenit.move2alf.core.action.ClassInfoModel;
import eu.xenit.move2alf.logic.DestinationService;
import eu.xenit.move2alf.logic.ObjectFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/3/13
 * Time: 11:42 AM
 */
@Service
public class ResourceTypeClassInfoService extends AbstractClassInfoService implements ApplicationContextAware{

    public static final String CATEGORY_DESTINATION = "Destination";
    private Map<String, DestinationTypeController> destinationTypeMap;

    @Autowired
    private DestinationService destinationService;

    @Override
    protected void addFilters(ClassPathScanningCandidateComponentProvider provider) {
        provider.addIncludeFilter(new AssignableTypeFilter(DestinationTypeController.class));
    }

    @PostConstruct
    public void init(){
        scanForClasses("eu.xenit");
        destinationTypeMap = new HashMap<String, DestinationTypeController>();
        for(ClassInfoModel model: getAllClasInfoModels()){
            ObjectFactory objectFactory = new ObjectFactory(model.getClazz(), new HashMap<String, String>(), beanFactory);
            destinationTypeMap.put(model.getClassId(), (DestinationTypeController) objectFactory.createObject());
        }
    }

    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    public DestinationTypeController getDestinationType(String classId){
        return destinationTypeMap.get(classId);
    }
}
