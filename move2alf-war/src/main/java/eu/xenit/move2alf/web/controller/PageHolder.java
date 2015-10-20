package eu.xenit.move2alf.web.controller;

import org.springframework.beans.support.PagedListHolder;

/**
 * User: Thijs Lemmens
 * Date: 20/10/15
 * Time: 10:15
 */
public class PageHolder extends PagedListHolder {

    private final int nmbOfResults;

    PageHolder(int nmbOfResults, int page, int pageSize, int linkedPages){
        this.nmbOfResults = nmbOfResults;
        this.setPage(page);
        this.setPageSize(pageSize);
        this.setMaxLinkedPages(linkedPages);
    }

    @Override
    public int getNrOfElements(){
        return nmbOfResults;
    }
}
