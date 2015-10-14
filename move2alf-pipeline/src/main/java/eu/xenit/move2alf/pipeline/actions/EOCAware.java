package eu.xenit.move2alf.pipeline.actions;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/8/13
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EOCAware extends Action{

    void beforeSendEOC();
}
