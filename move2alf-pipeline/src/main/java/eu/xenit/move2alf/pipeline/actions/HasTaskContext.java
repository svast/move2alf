package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.actions.context.TaskContext;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 3:50 PM
 */
public interface HasTaskContext extends Action {

    public void setTaskContext(TaskContext taskContext);
}
