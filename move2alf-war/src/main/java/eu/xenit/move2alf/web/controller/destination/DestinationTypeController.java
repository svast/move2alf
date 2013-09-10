package eu.xenit.move2alf.web.controller.destination;

import eu.xenit.move2alf.core.dto.Resource;
import eu.xenit.move2alf.web.dto.DestinationConfig;
import eu.xenit.move2alf.web.dto.DestinationInfo;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/2/13
 * Time: 2:33 PM
 */
public interface DestinationTypeController {

    public String getName();

    public DestinationInfo getDestinationInfo(Resource resource);

    public DestinationConfig getDestinationConfig(Resource resource);
}
