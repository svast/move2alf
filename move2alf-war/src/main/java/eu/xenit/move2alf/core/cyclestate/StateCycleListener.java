package eu.xenit.move2alf.core.cyclestate;

import eu.xenit.move2alf.core.cyclelistener.CycleListener;

public class StateCycleListener extends CycleListener {

    private final CycleStateManager stateManager;

    public StateCycleListener(CycleStateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void cycleStart(int cycleId) {
        this.stateManager.initializeState(cycleId);
    }

    @Override
    public void cycleEnd(int cycleId) {
        this.stateManager.destroyState(cycleId);
    }
}
