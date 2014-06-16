package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.AbstractClassInfoService;
import eu.xenit.move2alf.pipeline.actions.Action;
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
    @PostConstruct
    public void init() {
      scanForClasses("eu.xenit");
    }

    @Override
    protected Class<?> getTargetType() {
        return Action.class;
    }
}
