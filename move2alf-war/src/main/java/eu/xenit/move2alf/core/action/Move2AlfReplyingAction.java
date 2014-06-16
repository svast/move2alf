package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.pipeline.actions.AbstractStateAction;
import eu.xenit.move2alf.pipeline.actions.HasTaskContext;
import eu.xenit.move2alf.pipeline.actions.ReceivingAction;
import eu.xenit.move2alf.pipeline.actions.context.TaskContext;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 5:00 PM
 */
public abstract class Move2AlfReplyingAction<T> extends AbstractStateAction implements ReceivingAction<T>, HasTaskContext{

    protected TaskContext taskContext;

    @Override
    public void setTaskContext(TaskContext taskContext) {
        this.setTaskContext(taskContext);
    }

    protected void reply(Object message){
        taskContext.reply(message);
    }

    @Override
    public void execute(T message) {
        try {
            executeImpl(message);
        } catch (Exception e){
            reply(e);
        }
    }

    protected abstract void executeImpl(T message);
}
