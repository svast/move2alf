package eu.xenit.move2alf.pipeline.actions;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 10:56 AM
 */
public class JobConfig {

    private final ActionConfig firstAction;
    private final boolean autoStop;

    public JobConfig(ActionConfig firstAction, boolean autoStop){
        this.firstAction = firstAction;
        this.autoStop = autoStop;
    }

    public ActionConfig getFirstAction() {
        return firstAction;
    }

    public boolean isAutoStop() {
        return autoStop;
    }

    public JobConfig(ActionConfig firstAction){
        this(firstAction, true);
    }
}
