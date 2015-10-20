package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.web.dto.HistoryInfo;

import java.util.List;

/**
 * User: Thijs Lemmens
 * Date: 14/10/15
 * Time: 16:47
 */
public class HistoryPage {

    private int totalNumberOfResults;
    private List<HistoryInfo> historyInfos;

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    private int numberOfPages;

    public HistoryPage(int totalNumberOfResults, int numberOfPages, List<HistoryInfo> historyInfos) {
        this.totalNumberOfResults = totalNumberOfResults;
        this.historyInfos = historyInfos;
        this.numberOfPages = numberOfPages;
    }

    public int getTotalNumberOfResults() {
        return totalNumberOfResults;
    }

    public List<HistoryInfo> getHistoryInfos() {
        return historyInfos;
    }
}
