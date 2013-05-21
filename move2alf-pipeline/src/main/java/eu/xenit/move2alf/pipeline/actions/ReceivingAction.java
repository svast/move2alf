package eu.xenit.move2alf.pipeline.actions;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/25/13
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ReceivingAction<T> extends Action{

    public void execute(T message);

}
