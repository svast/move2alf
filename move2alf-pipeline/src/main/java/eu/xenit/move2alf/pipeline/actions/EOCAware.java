package eu.xenit.move2alf.pipeline.actions;

/**
 * End of Cycle aware.
 * An EOC message is sent when an actor knows it will not send any more message.
 * When an EOC reaches the end of the pipeline the job cycle is finished.
 *
 * User: thijs
 * Date: 5/8/13
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EOCAware extends Action{


    /**
     * Runtime tracks all possible senders for an action.
     * When all senders are dead, beforeSendEOC() is called, afterwards the dead message for this action is invoked.
     */
    void beforeSendEOC();
}
