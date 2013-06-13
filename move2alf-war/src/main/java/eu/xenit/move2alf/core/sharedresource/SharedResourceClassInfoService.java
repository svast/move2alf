package eu.xenit.move2alf.core.sharedresource;

import eu.xenit.move2alf.core.AbstractClassInfoService;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 2:04 PM
 */
@Service
public class SharedResourceClassInfoService extends AbstractClassInfoService{


    @Override
    protected void addFilters(ClassPathScanningCandidateComponentProvider provider) {
        provider.addIncludeFilter(new AssignableTypeFilter(SharedResource.class));
    }
}
