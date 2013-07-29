package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.AbstractClassInfoService;
import eu.xenit.move2alf.pipeline.actions.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * User: Thijs Lemmens
 * Date: 5/16/13
 * Time: 11:34 AM
 * This class keeps a register of all Move2AlfAction subclasses and keeps them in the right categories.
 */
@Service
public class ActionClassInfoService extends AbstractClassInfoService {

    private static final Logger logger = LoggerFactory.getLogger(ActionClassInfoService.class);
    public ActionClassInfoService(){

    }


    @Override
    protected void addFilters(ClassPathScanningCandidateComponentProvider provider) {
        provider.addIncludeFilter(new AssignableTypeFilter(Action.class));
    }

    @PostConstruct
    public void init() {
        scanForClasses("eu.xenit");
    }

}
