package eu.xenit.move2alf.web.controller.destination;

import eu.xenit.move2alf.core.action.ActionClassInfoService;
import eu.xenit.move2alf.core.action.ClassInfo;
import eu.xenit.move2alf.core.action.resource.AlfrescoHttpResourceAction$;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSharedResource;
import eu.xenit.move2alf.core.dto.Resource;
import eu.xenit.move2alf.core.sharedresource.SharedResourceClassInfoService;
import eu.xenit.move2alf.core.sharedresource.SharedResourceService;
import eu.xenit.move2alf.core.sharedresource.alfresco.AbstractAlfrescoSharedResource;
import eu.xenit.move2alf.core.sharedresource.alfresco.AlfrescoHttpSharedResource;
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
 * Created by Stan on 12-Jan-16.
 */
@Controller
@ClassInfo(classId = AlfrescoDestinationTypeController.CLASS_ID,
        category = ResourceTypeClassInfoService.CATEGORY_DESTINATION,
        description = "AlfrescoHttp destination type")
public class AlfrescoHttpDestinationTypeController extends AbstractController implements DestinationTypeController {

    public static final String CLASS_ID = "AlfrescoHttp";

    @Autowired
    protected DestinationService destinationService;

    @Autowired
    protected ActionClassInfoService actionClassInfoService;

    @Autowired
    protected SharedResourceClassInfoService sharedResourceClassInfoService;

    @Autowired
    protected SharedResourceService sharedResourceService;

    public AlfrescoHttpDestinationTypeController() {
        //ioc
    }

    @Override
    public String getName() {
        return "AlfrescoHttp";
    }

    @Override
    public DestinationInfo getDestinationInfo(Resource resource) {
        DestinationInfo info = new DestinationInfo();
        info.setName(resource.getName());
        ConfiguredAction first = resource.getFirstConfiguredAction();
        int sourceSink = Integer.parseInt(first.getParameter(AlfrescoHttpResourceAction$.MODULE$.PARAM_ALFRESCOHTTPSHAREDRESOURCE()));
        ConfiguredSharedResource configuredSharedResource = sharedResourceService.getConfiguredSharedResource(sourceSink);
        if(configuredSharedResource==null)
            return null;
        info.setType(getName());
        info.setUrl(configuredSharedResource.getParameter(AbstractAlfrescoSharedResource.PARAM_URL));
        info.setUserName(configuredSharedResource.getParameter(AbstractAlfrescoSharedResource.PARAM_USER));
        info.setId(resource.getId());
        info.setThreads(first.getNmbOfWorkers());

        return info;
    }

    private DestinationConfig getDestinationConfig(Resource resource) {
        AlfrescoDestinationModel destinationConfig = new AlfrescoDestinationModel();
        destinationConfig.setName(resource.getName());

        ConfiguredAction first = resource.getFirstConfiguredAction();
        int sourceSink = Integer.parseInt(first.getParameter(AlfrescoHttpResourceAction$.MODULE$.PARAM_ALFRESCOHTTPSHAREDRESOURCE()));
        ConfiguredSharedResource configuredSharedResource = sharedResourceService.getConfiguredSharedResource(sourceSink);
        if(configuredSharedResource==null)
            return null;

        destinationConfig.setDestinationURL(configuredSharedResource.getParameter(AbstractAlfrescoSharedResource.PARAM_URL));
        destinationConfig.setAlfUser(configuredSharedResource.getParameter(AbstractAlfrescoSharedResource.PARAM_USER));
        destinationConfig.setAlfPswd(configuredSharedResource.getParameter(AbstractAlfrescoSharedResource.PARAM_PASSWORD));
        destinationConfig.setNbrThreads(first.getNmbOfWorkers());
        return destinationConfig;
    }

    @RequestMapping(value = "/destinations/AlfrescoHttp/create", method = RequestMethod.POST)
    public ModelAndView createDestination(@ModelAttribute("alfrescoDestination") @Valid AlfrescoDestinationModel destination, BindingResult errors){

        if(errors.hasErrors()){
            ModelAndView mav = new ModelAndView("destinationtypes/alfrescoHttp/create");
            mav.addObject("destination", destination);

            mav.addObject("role", getRole());
            mav.addObject("errors", errors.getFieldErrors());
            return mav;
        }
        Resource resource = populateResource(destination, new Resource(), false);

        destinationService.saveDestination(resource);

        return new ModelAndView("redirect:/destinations");
    }

    @RequestMapping(value = "/destinations/AlfrescoHttp/create", method = RequestMethod.GET)
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
            int alfrescoResourceId = Integer.parseInt(action.getParameter(AlfrescoHttpResourceAction$.MODULE$.PARAM_ALFRESCOHTTPSHAREDRESOURCE()));
            alfrescoResource = sharedResourceService.getConfiguredSharedResource(alfrescoResourceId);
            if(alfrescoResource==null)
                return null;
        } else {
            action = new ConfiguredAction();
            alfrescoResource = new ConfiguredSharedResource();
        }
        action.setActionId(destination.getName()+"_action");
        action.setClassId(actionClassInfoService.getClassId(AlfrescoHttpSharedResource.class));
        action.setNmbOfWorkers(destination.getNbrThreads());
        action.setDispatcher(PipelineAssemblerImpl.PINNED_DISPATCHER);



        alfrescoResource.setClassId(sharedResourceClassInfoService.getClassId(AlfrescoHttpSharedResource.class));
        alfrescoResource.setName(destination.getName());
        alfrescoResource.setParameter(AbstractAlfrescoSharedResource.PARAM_URL, destination.getDestinationURL());
        alfrescoResource.setParameter(AbstractAlfrescoSharedResource.PARAM_USER, destination.getAlfUser());
        alfrescoResource.setParameter(AbstractAlfrescoSharedResource.PARAM_PASSWORD, destination.getAlfPswd());

        if(update){
            sharedResourceService.updateConfiguredSharedResource(alfrescoResource);
        } else {
            sharedResourceService.saveConfiguredSharedResource(alfrescoResource);
        }

        action.setParameter(AlfrescoHttpResourceAction$.MODULE$.PARAM_ALFRESCOHTTPSHAREDRESOURCE(), Integer.toString(alfrescoResource.getId()));

        resource.setName(destination.getName());
        resource.setClassId(CLASS_ID);
        resource.setFirstConfiguredAction(action);
        return resource;
    }

    @RequestMapping(value = "/destinations/AlfrescoHttp/{id}/edit", method = RequestMethod.POST)
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

    @RequestMapping(value = "/destinations/AlfrescoHttp/{id}/edit", method = RequestMethod.GET)
    public ModelAndView editDestination(@PathVariable int id){
        ModelAndView mav = getModelAndView();
        Resource resource = destinationService.getDestination(id);
        DestinationConfig destinationConfig = getDestinationConfig(resource);

        mav.addObject("destination", destinationConfig);
        mav.addObject("destinationId", id);
        mav.setViewName("destinationtypes/alfresco/edit");
        return mav;
    }

    @RequestMapping(value = "/destination/AlfrescoHttp/{id}/delete", method = RequestMethod.GET)
    public ModelAndView deleteDestination(@PathVariable int id){
        ModelAndView mav = new ModelAndView();
        final Resource destinationResource = destinationService.getDestination(id);
        destinationService.deleteDestination(destinationResource);

        ConfiguredAction action = destinationResource.getFirstConfiguredAction();
        int sourceSink = Integer.parseInt(action.getParameter(AlfrescoHttpResourceAction$.MODULE$.PARAM_ALFRESCOHTTPSHAREDRESOURCE()));
        final ConfiguredSharedResource configuredSharedResource = sharedResourceService.getConfiguredSharedResource(sourceSink);
        sharedResourceService.deleteConfiguredSharedResource(configuredSharedResource);
        mav.setViewName("redirect:/destinations");
        return mav;
    }


}
