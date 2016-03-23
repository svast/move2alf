package eu.xenit.move2alf.web.controller;

import eu.xenit.move2alf.logic.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by willem on 3/21/16.
 */
@Controller
public class StatsController {

    @Autowired
    private StatsService statsService;

    @RequestMapping("/api/v1/stats")
    @ResponseBody
    public Map<String,Long> getTotals() {
        Map<String,Long> stats = new HashMap<String, Long>();
        stats.put("OK", this.statsService.getTotalsSucceeded());
        return stats;
    }

}