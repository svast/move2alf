package eu.xenit.move2alf.core.sharedresource;

import eu.xenit.move2alf.core.AbstractClassInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 2:04 PM
 */
@Service
public class SharedResourceClassInfoService extends AbstractClassInfoService {
    @Override
    protected Class<?> getTargetType() {
        return SharedResource.class;
    }

    @PostConstruct
    public void init() {
        scanForClasses("eu.xenit");
    }
}
