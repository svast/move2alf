package eu.xenit.move2alf.core.cyclestate;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CycleStateManagerImpl implements CycleStateManager {

	private static final Logger logger = LoggerFactory
			.getLogger(CycleStateManagerImpl.class);

	private final Map<Integer, Map<String, Serializable>> statePerCycle;

	public CycleStateManagerImpl() {
		logger.debug("Creating CycleStateManager");
		this.statePerCycle = new ConcurrentHashMap<Integer, Map<String, Serializable>>();
	}

	@Override
	public Map<String, Serializable> getState(final int cycleId) {
		logger.debug("Getting state for cycle " + cycleId);
		return statePerCycle.get(cycleId);
	}

	@Override
	public void initializeState(final int cycleId) {
		logger.debug("Initializing state for cycle " + cycleId);
		statePerCycle.put(cycleId, new ConcurrentHashMap<String, Serializable>());
	}

	@Override
	public void destroyState(final int cycleId) {
		logger.debug("Destroying state for cycle " + cycleId);
		statePerCycle.remove(cycleId);
	}

}
