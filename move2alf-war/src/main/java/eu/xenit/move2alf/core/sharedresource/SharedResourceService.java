package eu.xenit.move2alf.core.sharedresource;

import eu.xenit.move2alf.core.action.ClassInfoModel;
import eu.xenit.move2alf.core.dto.ConfiguredSharedResource;
import eu.xenit.move2alf.logic.AbstractHibernateService;
import eu.xenit.move2alf.logic.ObjectFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 10:45 AM
 */
@Service
public class SharedResourceService extends AbstractHibernateService implements ApplicationContextAware{

    private Map<Integer, SharedResource> sharedResources = new HashMap<Integer, SharedResource>();

    @Autowired
    SharedResourceClassInfoService sharedResourceClassInfoService;

    public SharedResource getSharedResource(int id){
        if(sharedResources.containsKey(id)){
            return sharedResources.get(id);
        } else {
            ConfiguredSharedResource configuredSharedResource = getConfiguredSharedResource(id);
            ClassInfoModel classInfoModel = sharedResourceClassInfoService.getClassInfoModel(configuredSharedResource.getClassId());
            SharedResource resource = new ObjectFactory<SharedResource>(classInfoModel.getClazz(), configuredSharedResource.getParameters(), beanFactory).createObject();
            sharedResources.put(id, resource);
            return resource;
        }
    }

    public ConfiguredSharedResource getConfiguredSharedResource(int id){
        return (ConfiguredSharedResource) sessionFactory.getCurrentSession().createQuery("from ConfiguredSharedResource as d where d.id=?").setInteger(0, id).list().get(0);
    }

    public void saveConfiguredSharedResource(ConfiguredSharedResource configuredSharedResource){
        sessionFactory.getCurrentSession().save(configuredSharedResource);
    }

    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    public void updateConfiguredSharedResource(ConfiguredSharedResource alfrescoResource) {
        sessionFactory.getCurrentSession().update(alfrescoResource);
    }
}
