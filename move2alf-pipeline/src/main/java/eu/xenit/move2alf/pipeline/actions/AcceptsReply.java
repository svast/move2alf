package eu.xenit.move2alf.pipeline.actions;

/**
 * Accepts messages from Actions outside of the ActionConfig graph
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 3:46 PM
 */
public interface AcceptsReply extends Action{

    void acceptReply(String key, Object reply);
}
