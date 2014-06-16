package eu.xenit.move2alf.web.controller;

import eu.xenit.move2alf.core.action.ClassInfoModel;
import eu.xenit.move2alf.core.dto.Resource;
import eu.xenit.move2alf.logic.DestinationService;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.web.controller.destination.DestinationTypeController;
import eu.xenit.move2alf.web.controller.destination.ResourceTypeClassInfoService;
import eu.xenit.move2alf.web.dto.DestinationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/10/13
 * Time: 4:33 PM
 */
@Controller
public class DestinationController extends AbstractController{

    public DestinationController(){
        super();
    }

    @Autowired
    private JobService jobService;
    private JobService getJobService(){
        return jobService;
    }

    @Autowired
    protected ResourceTypeClassInfoService resourceTypeClassInfoService;

    @Autowired
    private DestinationService destinationService;


    @RequestMapping("/destinations")
    public ModelAndView destinations() {
        ModelAndView mav = new ModelAndView();
        List<DestinationInfo> destinations = new ArrayList<DestinationInfo>();
        for(Resource resource: destinationService.getDestinations()){
            destinations.add(resourceTypeClassInfoService.getDestinationType(resource.getClassId()).getDestinationInfo(resource));
        }
        mav.addObject("destinations", destinations);
        mav.addObject("destinationOptions", getDestinationTypeMap());
        mav.addObject("role", getRole());
        mav.setViewName("manage-destinations");
        return mav;
    }

    private Map<String, DestinationTypeController> getDestinationTypeMap() {
        Map<String, DestinationTypeController> destinationTypeMap = new HashMap<String, DestinationTypeController>();
        for(ClassInfoModel model: resourceTypeClassInfoService.getClassesForCategory(ResourceTypeClassInfoService.CATEGORY_DESTINATION)){
            DestinationTypeController type = resourceTypeClassInfoService.getDestinationType(model.getClassId());
            destinationTypeMap.put(model.getClassId(), type);
        }
        return destinationTypeMap;
    }
}
