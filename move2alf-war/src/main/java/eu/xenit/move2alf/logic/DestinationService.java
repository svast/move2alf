package eu.xenit.move2alf.logic;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import eu.xenit.move2alf.core.dto.Resource;
import eu.xenit.move2alf.pipeline.JobHandle;
import eu.xenit.move2alf.pipeline.actions.ActionConfig;
import eu.xenit.move2alf.pipeline.actions.JobConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/2/13
 * Time: 5:12 PM
 */

@Service("destinationService")
public class DestinationService extends AbstractHibernateService{

    private static final Logger logger = LoggerFactory.getLogger(DestinationService.class);

    private Map<Integer, JobHandle> jobHandleMap = new HashMap<Integer, JobHandle>();
    
    @Autowired
    private ActorSystem system;

    @Autowired
    private PipelineAssembler pipelineAssembler;

    public synchronized void startDestination(int id){
        if(jobHandleMap.containsKey(id)){
            return;
        }
        List<Resource> destinations = sessionFactory.getCurrentSession().createQuery("from Resource as d where d.id=?").setLong(0, id).list();
        if(destinations.size()<1){
            logger.warn("No destination for id: {}", id);
        } else {
            startResource(destinations.get(0));
        }
    }

    public List<Resource> getAllDestinations(){
        return sessionFactory.getCurrentSession().createQuery("from Resource").list();
    }

    public List<Resource> getDestinationsForClassId(String classId){
        return sessionFactory.getCurrentSession().createQuery("from Resource where classId= :classId").setParameter("classId", classId).list();
    }

    private void startResource(Resource destination) {
        String name = destination.getName().replace(" ", "_");
        ActionConfig actionConfig = pipelineAssembler.getActionConfig(destination.getFirstConfiguredAction());
        JobHandle jobHandle = new JobHandle(system, name, new JobConfig(actionConfig, false));
        jobHandle.startJob(new HashMap<>());
        jobHandleMap.put(destination.getId(), jobHandle);
    }

    public void sendTaskToDestination(int id, String key, Object task, ActorRef replyTo){
        if(!jobHandleMap.containsKey(id)){
            logger.info("Creating destination with id {} on first send of task", id);
            startDestination(id);
        }
        jobHandleMap.get(id).sendTask(key, task, replyTo);

    }

    public List<Resource>   getDestinations(){
        return sessionFactory.getCurrentSession().createQuery("from Resource").list();
    }

    public Resource getDestination(int id){
        List<Resource> results = sessionFactory.getCurrentSession().createQuery("from Resource where id=?").setInteger(0, id).list();
        if(results!=null && !results.isEmpty())
            return results.get(0);
        return null;
    }

    @PreAuthorize("hasRole('ROLE_JOB_ADMIN')")
    public void saveDestination(Resource resource){
        sessionFactory.getCurrentSession().save(resource);
    }

    @PreAuthorize("hasRole('ROLE_JOB_ADMIN')")
    public void updateDestination(Resource resource) {
        sessionFactory.getCurrentSession().update(resource);
        int id = resource.getId();
        if(jobHandleMap.containsKey(id)){
            jobHandleMap.get(id).destroy();
            jobHandleMap.remove(id);
        }
        startDestination(id);
    }

    @PreAuthorize("hasRole('ROLE_JOB_ADMIN')")
    public void deleteDestination(Resource resource) {
        sessionFactory.getCurrentSession().delete(resource.getFirstConfiguredAction());
        sessionFactory.getCurrentSession().delete(resource);
    }
}
