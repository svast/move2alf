package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.pipeline.actions.EOCBlockingAction;
import eu.xenit.move2alf.pipeline.actions.StartAware;
import eu.xenit.move2alf.pipeline.actions.context.EOCBlockingContext;
import eu.xenit.move2alf.repository.RepositoryAccessSession;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/11/13
 * Time: 10:28 AM
 */
public class RepositoryAction extends Move2AlfReceivingAction implements StartAware, EOCBlockingAction {

    RepositoryAccessSession session;
    public void setDestination(RepositoryAccessSession session){
        this.session = session;
    }

    private EOCBlockingContext eocBlockingContext;
    @Override
    public void setEOCBlockingContext(EOCBlockingContext eocBlockingContext) {
        this.eocBlockingContext = eocBlockingContext;
    }

    @Override
    protected void executeImpl(Object message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onStart() {
        eocBlockingContext.blockEOC();
    }
}
