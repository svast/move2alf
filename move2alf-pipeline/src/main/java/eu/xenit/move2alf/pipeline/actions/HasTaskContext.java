package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.actions.context.TaskContext;

/**
 * Sending a message from one job's action to another job's action is called a task
 * Receiving this message is done using the AcceptsReply interface
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 3:50 PM
 */
public interface HasTaskContext extends Action {

    void setTaskContext(TaskContext taskContext);
}
