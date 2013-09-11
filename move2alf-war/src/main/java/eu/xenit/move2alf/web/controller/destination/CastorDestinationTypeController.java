package eu.xenit.move2alf.web.controller.destination;

import eu.xenit.move2alf.core.action.ActionClassInfoService;
import eu.xenit.move2alf.core.action.ClassInfo;
import eu.xenit.move2alf.core.action.resource.CastorResourceAction;
import eu.xenit.move2alf.core.action.resource.CastorResourceAction$;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSharedResource;
import eu.xenit.move2alf.core.dto.Resource;
import eu.xenit.move2alf.core.sharedresource.SharedResourceClassInfoService;
import eu.xenit.move2alf.core.sharedresource.SharedResourceService;
import eu.xenit.move2alf.core.sharedresource.castor.CastorSharedResource;
import eu.xenit.move2alf.logic.DestinationService;
import eu.xenit.move2alf.logic.PipelineAssemblerImpl;
import eu.xenit.move2alf.web.controller.AbstractController;
import eu.xenit.move2alf.web.controller.destination.model.CastorDestinationModel;
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

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 9/10/13
 * Time: 11:19 AM
 */
@Controller
@ClassInfo(classId = CastorDestinationTypeController.CLASS_ID, category = ResourceTypeClassInfoService.CATEGORY_DESTINATION, description = "Controller that handles castor configurations")
public class CastorDestinationTypeController extends AbstractController implements DestinationTypeController{

    public static final String CLASS_ID = "Castor";

    @Override
    public String getName() {
        return "Castor";
    }

    @Override
    public DestinationInfo getDestinationInfo(Resource resource) {
        DestinationInfo info = new DestinationInfo();
        info.setName(resource.getName());
        ConfiguredAction first = resource.getFirstConfiguredAction();
        int castorSharedResource = Integer.parseInt(first.getParameter(CastorResourceAction$.MODULE$.PARAM_CASTORSHAREDRESOURCE()));
        ConfiguredSharedResource configuredSharedResource = sharedResourceService.getConfiguredSharedResource(castorSharedResource);
        if(configuredSharedResource==null)
            return null;
        info.setType(getName());
        info.setUrl(configuredSharedResource.getParameter(CastorSharedResource.PARAM_NODES).split("\\|")[0]);
        info.setUserName(" ");
        info.setId(resource.getId());
        info.setThreads(first.getNmbOfWorkers());

        return info;
    }

    @RequestMapping(value = "/destinations/Castor/create", method = RequestMethod.GET)
    public ModelAndView createDestination(){
        ModelAndView mav = getModelAndView();

        mav.addObject("destination", new CastorDestinationModel());
        mav.setViewName("destinationtypes/castor/create");
        return mav;
    }

    @Autowired
    private DestinationService destinationService;

    @RequestMapping(value = "/destinations/Castor/create", method = RequestMethod.POST)
    public ModelAndView createDestination(@ModelAttribute("castorDestination") @Valid CastorDestinationModel destination, BindingResult errors){

        if(errors.hasErrors()){
            ModelAndView mav = new ModelAndView("destinationtypes/castor/create");
            mav.addObject("destination", destination);

            mav.addObject("role", getRole());
            mav.addObject("errors", errors.getFieldErrors());
            return mav;
        }
        Resource resource = populateResource(destination, new Resource(), false);

        destinationService.saveDestination(resource);

        return new ModelAndView("redirect:/destinations");
    }

    @Autowired
    private SharedResourceService sharedResourceService;

    @Autowired
    private ActionClassInfoService actionClassInfoService;

    @Autowired
    private SharedResourceClassInfoService sharedResourceClassInfoService;

    private Resource populateResource(CastorDestinationModel destination, Resource resource, boolean update) {
        ConfiguredAction action;
        ConfiguredSharedResource castorResource;
        if(update){
            action =  resource.getFirstConfiguredAction();
            int castorResourceId = Integer.parseInt(action.getParameter(CastorResourceAction$.MODULE$.PARAM_CASTORSHAREDRESOURCE()));
            castorResource = sharedResourceService.getConfiguredSharedResource(castorResourceId);
            if(castorResource==null)
                return null;
        } else {
            action = new ConfiguredAction();
            castorResource = new ConfiguredSharedResource();
        }
        action.setActionId(destination.getName().replace(" ","_")+"_action");
        action.setClassId(actionClassInfoService.getClassId(CastorResourceAction.class));
        action.setNmbOfWorkers(destination.getNbrThreads());
        action.setDispatcher(PipelineAssemblerImpl.PINNED_DISPATCHER);



        castorResource.setClassId(sharedResourceClassInfoService.getClassId(CastorSharedResource.class));
        castorResource.setName(destination.getName());
        castorResource.setParameter(CastorSharedResource.PARAM_CLUSTERNAME, destination.getClusterName());
        castorResource.setParameter(CastorSharedResource.PARAM_NODES, destination.getEncodedNodes());
        castorResource.setParameter(CastorSharedResource.PARAM_LIFEPOINT, destination.getLifePoint());

        if(update){
            sharedResourceService.updateConfiguredSharedResource(castorResource);
        } else {
            sharedResourceService.saveConfiguredSharedResource(castorResource);
        }

        action.setParameter(CastorResourceAction$.MODULE$.PARAM_CASTORSHAREDRESOURCE(), Integer.toString(castorResource.getId()));

        resource.setName(destination.getName());
        resource.setClassId(CLASS_ID);
        resource.setFirstConfiguredAction(action);
        return resource;
    }

    protected ModelAndView getModelAndView() {
        ModelAndView mav = new ModelAndView();

        mav.addObject("role", getRole());
        mav.addObject("showDestinations", "false");
        return mav;
    }

    @RequestMapping(value = "/destinations/Castor/{id}/edit", method = RequestMethod.POST)
    public ModelAndView editDestination(@ModelAttribute("castorDestination") @Valid CastorDestinationModel castorDestinationModel, BindingResult errors, @PathVariable int id){
        Resource resource = populateResource(castorDestinationModel, destinationService.getDestination(id), true);
        destinationService.updateDestination(resource);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/destinations");
        return mav;
    }

    @RequestMapping(value = "/destinations/Castor/{id}/edit", method = RequestMethod.GET)
    public ModelAndView editDestination(@PathVariable int id){
        ModelAndView mav = getModelAndView();
        Resource resource = destinationService.getDestination(id);
        DestinationConfig destinationConfig = getDestinationConfig(resource);

        mav.addObject("destination", destinationConfig);
        mav.addObject("destinationId", id);
        mav.setViewName("destinationtypes/castor/edit");
        return mav;
    }

    @RequestMapping(value = "/destination/Castor/{id}/delete", method = RequestMethod.GET)
    public ModelAndView deleteDestination(@PathVariable int id){
        ModelAndView mav = new ModelAndView();
        final Resource destinationResource = destinationService.getDestination(id);
        destinationService.deleteDestination(destinationResource);

        ConfiguredAction action = destinationResource.getFirstConfiguredAction();
        int sourceSink = Integer.parseInt(action.getParameter(CastorResourceAction$.MODULE$.PARAM_CASTORSHAREDRESOURCE()));
        final ConfiguredSharedResource configuredSharedResource = sharedResourceService.getConfiguredSharedResource(sourceSink);
        sharedResourceService.deleteConfiguredSharedResource(configuredSharedResource);
        mav.setViewName("redirect:/destinations");
        return mav;
    }

    private DestinationConfig getDestinationConfig(Resource resource) {
        CastorDestinationModel destinationConfig = new CastorDestinationModel();
        destinationConfig.setName(resource.getName());

        ConfiguredAction first = resource.getFirstConfiguredAction();
        int sourceSink = Integer.parseInt(first.getParameter(CastorResourceAction$.MODULE$.PARAM_CASTORSHAREDRESOURCE()));
        ConfiguredSharedResource configuredSharedResource = sharedResourceService.getConfiguredSharedResource(sourceSink);
        if(configuredSharedResource==null)
            return null;

        destinationConfig.setClusterName(configuredSharedResource.getParameter(CastorSharedResource.PARAM_CLUSTERNAME));
        String[] nodes = configuredSharedResource.getParameter(CastorSharedResource.PARAM_NODES).split("\\|");
        destinationConfig.setNode1(nodes[0]);
        destinationConfig.setNode2(nodes[1]);
        destinationConfig.setNode3(nodes[2]);
        destinationConfig.setNbrThreads(first.getNmbOfWorkers());
        destinationConfig.setLifePoint(configuredSharedResource.getParameter(CastorSharedResource.PARAM_LIFEPOINT));

        return destinationConfig;
    }

}
