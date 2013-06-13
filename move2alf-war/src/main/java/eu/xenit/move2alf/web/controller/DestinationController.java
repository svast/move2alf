package eu.xenit.move2alf.web.controller;

import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.ConfiguredObject;
import eu.xenit.move2alf.core.dto.ConfiguredSharedResource;
import eu.xenit.move2alf.core.enums.EDestinationParameter;
import eu.xenit.move2alf.core.sourcesink.SourceSink;
import eu.xenit.move2alf.core.sourcesink.SourceSinkFactory;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.web.dto.DestinationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
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

    @Autowired
    private JobService jobService;
    private JobService getJobService(){
        return jobService;
    }

    @Autowired
    private SourceSinkFactory sourceSinkFactory;
    private SourceSinkFactory getSourceSinkFactory(){
        return sourceSinkFactory;
    }

    @RequestMapping("/destinations")
    public ModelAndView destinations() {
        ModelAndView mav = new ModelAndView();
        List<ConfiguredSharedResource> destinations = getJobService()
                .getAllDestinationConfiguredSourceSinks();

        Map<String, String> sourceSinkNames = new HashMap<String, String>();

        for (ConfiguredSharedResource destination : destinations) {
            SourceSink sourceSink = getSourceSinkFactory().getObject(
                    destination.getClassId());
            sourceSinkNames.put(destination.getClassId(), sourceSink
                    .getName());
        }

        mav.addObject("destinations", destinations);
        mav.addObject("typeNames", sourceSinkNames);
        mav.addObject("role", getRole());
        mav.setViewName("manage-destinations");
        return mav;
    }

    @RequestMapping(value = "/destination/create", method = RequestMethod.GET)
    public ModelAndView createDestinationsForm() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("destination", new DestinationConfig());
        mav.addObject("destinationOptions", getJobService()
                .getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
        mav.addObject("role", getRole());
        mav.addObject("showDestinations", "false");
        mav.setViewName("create-destination");
        return mav;
    }

    @RequestMapping(value = "/destination/create", method = RequestMethod.POST)
    public ModelAndView createDestination(
            @ModelAttribute("destination") @Valid DestinationConfig destination,
            BindingResult errors) {

        if(errors.hasErrors()){
            ModelAndView mav = new ModelAndView("create-destination");
            mav.addObject("destination", destination);
            mav.addObject("destinationOptions", getJobService()
                    .getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
            mav.addObject("role", getRole());
            mav.addObject("errors", errors.getFieldErrors());
            return mav;
        }
        ModelAndView mav = new ModelAndView();

        HashMap<EDestinationParameter, Object> destinationParams = new HashMap<EDestinationParameter, Object>();
        destinationParams.put(EDestinationParameter.NAME, destination.getDestinationName());
        destinationParams.put(EDestinationParameter.URL, destination.getDestinationURL());
        destinationParams.put(EDestinationParameter.USER, destination.getAlfUser());
        destinationParams.put(EDestinationParameter.PASSWORD, destination.getAlfPswd());
        destinationParams.put(EDestinationParameter.THREADS, Integer.toString(destination.getNbrThreads()));
        getJobService().createDestination(destination.getDestinationType(), destinationParams);

        mav.setViewName("redirect:/destinations");
        return mav;
    }

    @RequestMapping(value = "/destination/{id}/edit", method = RequestMethod.GET)
    public ModelAndView editDestinationForm(@PathVariable int id) {
        ModelAndView mav = makeEditDestinationModelAndView(id);
        return mav;
    }

    private ModelAndView makeEditDestinationModelAndView(int id) {
        ModelAndView mav = new ModelAndView();
        ConfiguredObject destination = getJobService().getConfiguredSourceSink(id);
        DestinationConfig destinationConfig = new DestinationConfig();

        destinationConfig.setDestinationName(destination.getParameter("name"));
        destinationConfig.setDestinationType(destination.getClassId());
        destinationConfig.setDestinationURL(destination.getParameter("url"));
        destinationConfig.setAlfUser(destination.getParameter("user"));
        destinationConfig.setAlfPswd(destination.getParameter("password"));
        destinationConfig.setNbrThreads(Integer.parseInt(destination.getParameter("threads")));

        mav.addObject("destination", destinationConfig);
        mav.addObject("destinationId", id);
        mav.addObject("destinationOptions", getJobService()
                .getSourceSinksByCategory(ConfigurableObject.CAT_DESTINATION));
        mav.addObject("role", getRole());
        mav.setViewName("edit-destination");
        return mav;
    }

    @RequestMapping(value = "/destination/{id}/edit", method = RequestMethod.POST)
    public ModelAndView editDestination(
            @PathVariable int id,
            @ModelAttribute("destination") @Valid DestinationConfig destination,
            BindingResult errors) {

        boolean destinationExists = false;
        if (!getJobService().getConfiguredSourceSink(id).getParameter("name")
                .equals(destination.getDestinationName())) {
            destinationExists = getJobService().checkDestinationExists(
                    destination.getDestinationName());
        }

        if (errors.hasErrors() || destinationExists == true) {
            System.out.println("THE ERRORS: " + errors.toString());

            ModelAndView mav = makeEditDestinationModelAndView(id);

            mav.addObject("destinationExists", destinationExists);
            mav.addObject("errors", errors.getFieldErrors());
            return mav;
        }

        ModelAndView mav = new ModelAndView();

        HashMap<EDestinationParameter, Object> destinationParams = new HashMap<EDestinationParameter, Object>();

        destinationParams.put(EDestinationParameter.NAME, destination
                .getDestinationName());
        destinationParams.put(EDestinationParameter.URL, destination
                .getDestinationURL());
        destinationParams.put(EDestinationParameter.USER, destination
                .getAlfUser());
        destinationParams.put(EDestinationParameter.PASSWORD, destination
                .getAlfPswd());
        destinationParams.put(EDestinationParameter.THREADS, destination
                .getNbrThreads());
        getJobService().editDestination(id, destination.getDestinationType(),
                destinationParams);

        mav.setViewName("redirect:/destinations");
        return mav;
    }

    @RequestMapping(value = "/destination/{id}/delete", method = RequestMethod.GET)
    public ModelAndView deleteDestination(@PathVariable int id) {
        ModelAndView mav = new ModelAndView();
        getJobService().deleteDestination(id);
        mav.setViewName("redirect:/destinations");
        return mav;
    }
}
