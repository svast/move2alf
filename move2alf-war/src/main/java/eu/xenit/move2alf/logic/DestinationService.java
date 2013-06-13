package eu.xenit.move2alf.logic;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import eu.xenit.move2alf.core.dto.Destination;
import eu.xenit.move2alf.pipeline.JobHandle;
import eu.xenit.move2alf.pipeline.actions.ActionConfig;
import eu.xenit.move2alf.pipeline.actions.JobConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void startDestination(int id){
        List<Destination> destinations = sessionFactory.getCurrentSession().createQuery("select from Destination as d where d.id=?").setLong(0, id).list();
        if(destinations.size()<1){
            logger.warn("No destination for id: {}", id);
        } else {
            Destination destination = destinations.get(0);
            ActionConfig actionConfig = pipelineAssembler.getActionConfig(destination.getFirstConfiguredAction());

            JobHandle jobHandle = new JobHandle(system, destination.getName(), new JobConfig(actionConfig, false));
            jobHandle.startJob();
            jobHandleMap.put(id, jobHandle);
        }
    }

    public void sendTaskToDestination(int id, String key, Object task, ActorRef replyTo){
        jobHandleMap.get(id).sendTask(key, task, replyTo);
    }

}
