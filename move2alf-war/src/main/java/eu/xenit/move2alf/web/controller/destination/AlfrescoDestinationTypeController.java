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
import eu.xenit.move2alf.core.sourcesink.AlfrescoSourceSink;
import eu.xenit.move2alf.logic.DestinationService;
import eu.xenit.move2alf.logic.PipelineAssemblerImpl;
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
import javax.validation.Validator;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/2/13
 * Time: 2:38 PM
 */
@Controller
@ClassInfo(classId = "Alfresco",
            category = ResourceTypeClassInfoService.CATEGORY_DESTINATION,
            description = "Alfresco destination type")
public class AlfrescoDestinationTypeController implements DestinationTypeController {

    @Autowired
    private Validator validator;

    @Autowired
    private DestinationService destinationService;

    @Autowired
    private ActionClassInfoService actionClassInfoService;

    @Autowired
    private SharedResourceClassInfoService sharedResourceClassInfoService;

    @Autowired
    private SharedResourceService sharedResourceService;

    @Override
    public void processModel(DestinationConfig destination) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getModel(){
        return new AlfrescoDestinationModel();
    }

    @Override
    public String getViewName() {
        return "destinationtypes/alfrescodestination.ftl";
    }

    public String getName(){
        return "Alfresco";
    }

    @Override
    public DestinationInfo getDestinationInfo(Resource resource) {
        DestinationInfo info = new DestinationInfo();
        info.setName(resource.getName());
        ConfiguredAction first = resource.getFirstConfiguredAction();
        int sourceSink = Integer.parseInt(first.getParameter(AlfrescoResourceAction$.MODULE$.PARAM_ALFRESCOSOURCESINK()));
        ConfiguredSharedResource configuredSharedResource = sharedResourceService.getConfiguredSharedResource(sourceSink);
        info.setType(getName());
        info.setUrl(configuredSharedResource.getParameter(AlfrescoSourceSink.PARAM_URL));
        info.setUserName(configuredSharedResource.getParameter(AlfrescoSourceSink.PARAM_USER));
        info.setId(resource.getId());
        info.setThreads(first.getNmbOfWorkers());

        return info;
    }

    @Override
    public DestinationConfig getDestinationConfig(Resource resource) {
        AlfrescoDestinationModel destinationConfig = new AlfrescoDestinationModel();
        destinationConfig.setName(resource.getName());
        destinationConfig.setType(resource.getClassId());

        ConfiguredAction first = resource.getFirstConfiguredAction();
        int sourceSink = Integer.parseInt(first.getParameter(AlfrescoResourceAction$.MODULE$.PARAM_ALFRESCOSOURCESINK()));
        ConfiguredSharedResource configuredSharedResource = sharedResourceService.getConfiguredSharedResource(sourceSink);

        destinationConfig.setDestinationURL(configuredSharedResource.getParameter(AlfrescoSourceSink.PARAM_URL));
        destinationConfig.setAlfUser(configuredSharedResource.getParameter(AlfrescoSourceSink.PARAM_USER));
        destinationConfig.setAlfPswd(configuredSharedResource.getParameter(AlfrescoSourceSink.PARAM_PASSWORD));
        destinationConfig.setNbrThreads(first.getNmbOfWorkers());

        return destinationConfig;
    }

    @RequestMapping(value = "/destination/create/Alfresco", method = RequestMethod.POST)
    public ModelAndView createDestination(@ModelAttribute("alfrescoDestination") @Valid AlfrescoDestinationModel destination, BindingResult errors){
        //TODO: Handle errors

        Resource resource = populateResource(destination, new Resource(), false);

        destinationService.saveDestination(resource);

        return new ModelAndView("redirect:/destinations");
    }

    private Resource populateResource(AlfrescoDestinationModel destination, Resource resource, boolean update) {
        ConfiguredAction action;
        ConfiguredSharedResource alfrescoResource;
        if(update){
            action =  resource.getFirstConfiguredAction();
            int alfrescoResourceId = Integer.parseInt(action.getParameter(AlfrescoResourceAction$.MODULE$.PARAM_ALFRESCOSOURCESINK()));
            alfrescoResource = sharedResourceService.getConfiguredSharedResource(alfrescoResourceId);
        } else {
            action = new ConfiguredAction();
            alfrescoResource = new ConfiguredSharedResource();
        }
        action.setActionId(destination.getName()+"_action");
        action.setClassId(actionClassInfoService.getClassId(AlfrescoResourceAction.class));
        action.setNmbOfWorkers(destination.getNbrThreads());
        action.setDispatcher(PipelineAssemblerImpl.PINNED_DISPATCHER);



        alfrescoResource.setClassId(sharedResourceClassInfoService.getClassId(AlfrescoSourceSink.class));
        alfrescoResource.setName(destination.getName());
        alfrescoResource.setParameter(AlfrescoSourceSink.PARAM_URL, destination.getDestinationURL());
        alfrescoResource.setParameter(AlfrescoSourceSink.PARAM_USER, destination.getAlfUser());
        alfrescoResource.setParameter(AlfrescoSourceSink.PARAM_PASSWORD, destination.getAlfPswd());

        if(update){
            sharedResourceService.updateConfiguredSharedResource(alfrescoResource);
        } else {
            sharedResourceService.saveConfiguredSharedResource(alfrescoResource);
        }

        action.setParameter(AlfrescoResourceAction$.MODULE$.PARAM_ALFRESCOSOURCESINK(), Integer.toString(alfrescoResource.getId()));

        resource.setName(destination.getName());
        resource.setClassId(destination.getType());
        resource.setFirstConfiguredAction(action);
        return resource;
    }

    @RequestMapping(value = "/destination/alfresco/{id}/edit", method = RequestMethod.POST)
    public ModelAndView editDestination(@ModelAttribute("alfrescoDestination") @Valid AlfrescoDestinationModel alfrescoDestinationModel, BindingResult errors, @PathVariable int id){
        Resource resource = populateResource(alfrescoDestinationModel, destinationService.getDestination(id), true);
        destinationService.updateDestination(resource);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/destinations");
        return mav;
    }
}
