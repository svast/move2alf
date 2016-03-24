package eu.xenit.move2alf.web.controller.destination.model;

import java.util.List;

/**
 * Created by willem on 3/22/16.
 */
public class StatsModel {
    private List total;
    private List jobs;

    public List getJobs() {
        return jobs;
    }

    public void setJobs(List jobs) {
        this.jobs = jobs;
    }

    public List getTotal() {
        return total;
    }

    public void setTotal(List total) {
        this.total = total;
    }
}
