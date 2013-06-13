package eu.xenit.move2alf.core.sharedresource;

import eu.xenit.move2alf.core.dto.ConfiguredSharedResource;
import eu.xenit.move2alf.logic.AbstractHibernateService;
import eu.xenit.move2alf.logic.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 10:45 AM
 */
@Service
public class SharedResourceService extends AbstractHibernateService{

    private Map<Integer, SharedResource> sharedResources = new HashMap<Integer, SharedResource>();

    @Autowired
    SharedResourceClassInfoService sharedResourceClassInfoService;

    public SharedResource getSharedResource(int id){
        if(sharedResources.containsKey(id)){
            return sharedResources.get(id);
        } else {
            ConfiguredSharedResource configuredSharedResource = (ConfiguredSharedResource) sessionFactory.getCurrentSession().createQuery("select from ConfiguredSharedResource as d where d.id=?").setLong(0, id).list().get(0);
            SharedResource resource = new ObjectFactory<SharedResource>(sharedResourceClassInfoService.getClassInfoModel(configuredSharedResource.getClassId()).getClazz(), configuredSharedResource.getParameters()).createObject();
            sharedResources.put(id, resource);
            return resource;
        }
    }
}
