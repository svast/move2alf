package eu.xenit.move2alf.logic;

import java.util.List;

/**
 * Created by willem on 3/21/16.
 *
 * Service that returns statistics (numerical facts) about processed documents
 *
 */
public interface StatsService {
    List getTotals();
    List getTotalsPerJob();
}
