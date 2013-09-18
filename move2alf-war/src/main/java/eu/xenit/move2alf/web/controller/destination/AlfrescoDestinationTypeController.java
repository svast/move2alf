package eu.xenit.move2alf.web.controller.destination;

import eu.xenit.move2alf.core.action.ActionClassInfoService;
import eu.xenit.move2alf.core.action.ClassInfo;
import eu.xenit.move2alf.core.action.resource.AlfrescoResourceAction;
import eu.xenit.move2alf.core.action.resource.AlfrescoResourceAction$;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSharedResource;
import eu.xenit.move2alf.core.dto.Resource;
import eu.xenit.move2alf.core.sharedresource.SharedResourceClassInfoService;
import eu.xenit.move2alf.core.sharedresource.SharedResourceService;
import eu.xenit.move2alf.core.sharedresource.alfresco.AlfrescoSharedResource;
import eu.xenit.move2alf.logic.DestinationService;
import eu.xenit.move2alf.logic.PipelineAssemblerImpl;
import eu.xenit.move2alf.web.controller.AbstractController;
import eu.xenit.move2alf.web.controller.destination.model.AlfrescoDestinationModel;
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
 * Date: 7/2/13
 * Time: 2:38 PM
 */
@Controller
@ClassInfo(classId = AlfrescoDestinationTypeController.CLASS_ID,
            category = ResourceTypeClassInfoService.CATEGORY_DESTINATION,
            description = "Alfresco destination type")
public class AlfrescoDestinationTypeController extends AbstractController implements DestinationTypeController {

    public static final String CLASS_ID = "Alfresco";

    @Autowired
    private DestinationService destinationService;

    @Autowired
    private ActionClassInfoService actionClassInfoService;

    @Autowired
    private SharedResourceClassInfoService sharedResourceClassInfoService;

    @Autowired
    private SharedResourceService sharedResourceService;

    public String getName(){
        return "Alfresco";
    }

    @Override
    public DestinationInfo getDestinationInfo(Resource resource) {
        DestinationInfo info = new DestinationInfo();
        info.setName(resource.getName());
        ConfiguredAction first = resource.getFirstConfiguredAction();
        int sourceSink = Integer.parseInt(first.getParameter(AlfrescoResourceAction$.MODULE$.PARAM_ALFRESCOSHAREDRESOURCE()));
        ConfiguredSharedResource configuredSharedResource = sharedResourceService.getConfiguredSharedResource(sourceSink);
        if(configuredSharedResource==null)
            return null;
        info.setType(getName());
        info.setUrl(configuredSharedResource.getParameter(AlfrescoSharedResource.PARAM_URL));
        info.setUserName(configuredSharedResource.getParameter(AlfrescoSharedResource.PARAM_USER));
        info.setId(resource.getId());
        info.setThreads(first.getNmbOfWorkers());

        return info;
    }

    private DestinationConfig getDestinationConfig(Resource resource) {
        AlfrescoDestinationModel destinationConfig = new AlfrescoDestinationModel();
        destinationConfig.setName(resource.getName());

        ConfiguredAction first = resource.getFirstConfiguredAction();
        int sourceSink = Integer.parseInt(first.getParameter(AlfrescoResourceAction$.MODULE$.PARAM_ALFRESCOSHAREDRESOURCE()));
        ConfiguredSharedResource configuredSharedResource = sharedResourceService.getConfiguredSharedResource(sourceSink);
        if(configuredSharedResource==null)
            return null;

        destinationConfig.setDestinationURL(configuredSharedResource.getParameter(AlfrescoSharedResource.PARAM_URL));
        destinationConfig.setAlfUser(configuredSharedResource.getParameter(AlfrescoSharedResource.PARAM_USER));
        destinationConfig.setAlfPswd(configuredSharedResource.getParameter(AlfrescoSharedResource.PARAM_PASSWORD));
        destinationConfig.setNbrThreads(first.getNmbOfWorkers());
        return destinationConfig;
    }

    @RequestMapping(value = "/destinations/Alfresco/create", method = RequestMethod.POST)
    public ModelAndView createDestination(@ModelAttribute("alfrescoDestination") @Valid AlfrescoDestinationModel destination, BindingResult errors){

        if(errors.hasErrors()){
            ModelAndView mav = new ModelAndView("destinationtypes/alfresco/create");
            mav.addObject("destination", destination);

            mav.addObject("role", getRole());
            mav.addObject("errors", errors.getFieldErrors());
            return mav;
        }
        Resource resource = populateResource(destination, new Resource(), false);

        destinationService.saveDestination(resource);

        return new ModelAndView("redirect:/destinations");
    }

    @RequestMapping(value = "/destinations/Alfresco/create", method = RequestMethod.GET)
    public ModelAndView createDestination(){
        ModelAndView mav = getModelAndView();

        //Default is Alfresco. If we add something else, we should change this.
        mav.addObject("destination", new AlfrescoDestinationModel());
        mav.setViewName("destinationtypes/alfresco/create");
        return mav;
    }

    private Resource populateResource(AlfrescoDestinationModel destination, Resource resource, boolean update) {
        ConfiguredAction action;
        ConfiguredSharedResource alfrescoResource;
        if(update){
            action =  resource.getFirstConfiguredAction();
            int alfrescoResourceId = Integer.parseInt(action.getParameter(AlfrescoResourceAction$.MODULE$.PARAM_ALFRESCOSHAREDRESOURCE()));
            alfrescoResource = sharedResourceService.getConfiguredSharedResource(alfrescoResourceId);
            if(alfrescoResource==null)
                return null;
        } else {
            action = new ConfiguredAction();
            alfrescoResource = new ConfiguredSharedResource();
        }
        action.setActionId(destination.getName()+"_action");
        action.setClassId(actionClassInfoService.getClassId(AlfrescoResourceAction.class));
        action.setNmbOfWorkers(destination.getNbrThreads());
        action.setDispatcher(PipelineAssemblerImpl.PINNED_DISPATCHER);



        alfrescoResource.setClassId(sharedResourceClassInfoService.getClassId(AlfrescoSharedResource.class));
        alfrescoResource.setName(destination.getName());
        alfrescoResource.setParameter(AlfrescoSharedResource.PARAM_URL, destination.getDestinationURL());
        alfrescoResource.setParameter(AlfrescoSharedResource.PARAM_USER, destination.getAlfUser());
        alfrescoResource.setParameter(AlfrescoSharedResource.PARAM_PASSWORD, destination.getAlfPswd());

        if(update){
            sharedResourceService.updateConfiguredSharedResource(alfrescoResource);
        } else {
            sharedResourceService.saveConfiguredSharedResource(alfrescoResource);
        }

        action.setParameter(AlfrescoResourceAction$.MODULE$.PARAM_ALFRESCOSHAREDRESOURCE(), Integer.toString(alfrescoResource.getId()));

        resource.setName(destination.getName());
        resource.setClassId(CLASS_ID);
        resource.setFirstConfiguredAction(action);
        return resource;
    }

    @RequestMapping(value = "/destinations/Alfresco/{id}/edit", method = RequestMethod.POST)
    public ModelAndView editDestination(@ModelAttribute("alfrescoDestination") @Valid AlfrescoDestinationModel alfrescoDestinationModel, BindingResult errors, @PathVariable int id){
        Resource resource = populateResource(alfrescoDestinationModel, destinationService.getDestination(id), true);
        destinationService.updateDestination(resource);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/destinations");
        return mav;
    }

    protected ModelAndView getModelAndView() {
        ModelAndView mav = new ModelAndView();

        mav.addObject("role", getRole());
        mav.addObject("showDestinations", "false");
        return mav;
    }

    @RequestMapping(value = "/destinations/Alfresco/{id}/edit", method = RequestMethod.GET)
    public ModelAndView editDestination(@PathVariable int id){
        ModelAndView mav = getModelAndView();
        Resource resource = destinationService.getDestination(id);
        DestinationConfig destinationConfig = getDestinationConfig(resource);

        mav.addObject("destination", destinationConfig);
        mav.addObject("destinationId", id);
        mav.setViewName("destinationtypes/alfresco/edit");
        return mav;
    }

    @RequestMapping(value = "/destination/Alfresco/{id}/delete", method = RequestMethod.GET)
    public ModelAndView deleteDestination(@PathVariable int id){
        ModelAndView mav = new ModelAndView();
        final Resource destinationResource = destinationService.getDestination(id);
        destinationService.deleteDestination(destinationResource);

        ConfiguredAction action = destinationResource.getFirstConfiguredAction();
        int sourceSink = Integer.parseInt(action.getParameter(AlfrescoResourceAction$.MODULE$.PARAM_ALFRESCOSHAREDRESOURCE()));
        final ConfiguredSharedResource configuredSharedResource = sharedResourceService.getConfiguredSharedResource(sourceSink);
        sharedResourceService.deleteConfiguredSharedResource(configuredSharedResource);
        mav.setViewName("redirect:/destinations");
        return mav;
    }
}
