package eu.xenit.move2alf.web.controller.destination;

import eu.xenit.move2alf.core.action.ClassInfo;
import eu.xenit.move2alf.core.dto.Resource;
import eu.xenit.move2alf.web.controller.AbstractController;
import eu.xenit.move2alf.web.dto.DestinationConfig;
import eu.xenit.move2alf.web.dto.DestinationInfo;
import org.springframework.stereotype.Controller;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 9/10/13
 * Time: 11:19 AM
 */
@Controller
@ClassInfo(classId = "Castor", category = ResourceTypeClassInfoService.CATEGORY_DESTINATION, description = "Controller that handles castor configurations")
public class CastorDestinationTypeController extends AbstractController implements DestinationTypeController{

    @Override
    public String getName() {
        return "Castor";
    }

    @Override
    public DestinationInfo getDestinationInfo(Resource resource) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DestinationConfig getDestinationConfig(Resource resource) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
