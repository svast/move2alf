package eu.xenit.move2alf.web.controller;

import eu.xenit.move2alf.core.action.ClassInfoModel;
import eu.xenit.move2alf.core.dto.Resource;
import eu.xenit.move2alf.logic.DestinationService;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.web.controller.destination.DestinationTypeController;
import eu.xenit.move2alf.web.controller.destination.ResourceTypeClassInfoService;
import eu.xenit.move2alf.web.dto.DestinationConfig;
import eu.xenit.move2alf.web.dto.DestinationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
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
        mav.addObject("role", getRole());
        mav.setViewName("manage-destinations");
        return mav;
    }

    @RequestMapping(value = "/destination/create", method = RequestMethod.GET)
    public ModelAndView createDestinationsForm() {
        ModelAndView mav = getModelAndView();

        //Default is Alfresco. If we add something else, we should change this.
        mav.addObject("destination", resourceTypeClassInfoService.getDestinationType("Alfresco").getModel());
        mav.setViewName("create-destination");
        return mav;
    }

    protected ModelAndView getModelAndView() {
        ModelAndView mav = new ModelAndView();
        Map<String, DestinationTypeController> destinationTypeMap = new HashMap<String, DestinationTypeController>();
        for(ClassInfoModel model: resourceTypeClassInfoService.getClassesForCategory(ResourceTypeClassInfoService.CATEGORY_DESTINATION)){
            DestinationTypeController type = resourceTypeClassInfoService.getDestinationType(model.getClassId());
            destinationTypeMap.put(model.getClassId(), type);
        }

        mav.addObject("destinationOptions", destinationTypeMap);
        mav.addObject("role", getRole());
        mav.addObject("showDestinations", "false");
        return mav;
    }

    @RequestMapping(value = "/destination/create", method = RequestMethod.POST)
    public ModelAndView createDestination(
            @ModelAttribute("destination") @Valid DestinationConfig destination,
            BindingResult errors) {

        if(errors.hasErrors()){
            ModelAndView mav = new ModelAndView("create-destination");
            mav.addObject("destination", destination);

            mav.addObject("role", getRole());
            mav.addObject("errors", errors.getFieldErrors());
            return mav;
        }
        ModelAndView mav = new ModelAndView();

        resourceTypeClassInfoService.getDestinationType(destination.getType()).processModel(destination);

        mav.setViewName("redirect:/destinations");
        return mav;
    }

    @RequestMapping(value = "/destination/{id}/edit", method = RequestMethod.GET)
    public ModelAndView editDestinationForm(@PathVariable int id) {
        ModelAndView mav = makeEditDestinationModelAndView(id);
        return mav;
    }

    private ModelAndView makeEditDestinationModelAndView(int id) {
        ModelAndView mav = getModelAndView();
        Resource resource = destinationService.getDestination(id);

        DestinationTypeController destinationTypeController = resourceTypeClassInfoService.getDestinationType(resource.getClassId());
        DestinationConfig destinationConfig = destinationTypeController.getDestinationConfig(resource);

        mav.addObject("destination", destinationConfig);
        mav.addObject("destinationId", id);
        mav.setViewName("edit-destination");
        return mav;
    }
}
