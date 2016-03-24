package eu.xenit.move2alf.web.controller;

import eu.xenit.move2alf.logic.StatsService;
import eu.xenit.move2alf.web.controller.destination.model.StatsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by willem on 3/21/16.
 */
@Controller
public class StatsController {

    @Autowired
    private StatsService statsService;

    @RequestMapping("/api/v1/stats")
    @ResponseBody
    public StatsModel getStats() {
        StatsModel stats = new StatsModel();
        stats.setTotal(this.statsService.getTotals());
        stats.setJobs(this.statsService.getTotalsPerJob());
        return stats;
    }

}