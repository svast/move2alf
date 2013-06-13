package eu.xenit.move2alf.pipeline.actions;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 3:46 PM
 */
public interface AcceptsReply<T> extends Action{

    public void acceptReply(String key, T message);
}
