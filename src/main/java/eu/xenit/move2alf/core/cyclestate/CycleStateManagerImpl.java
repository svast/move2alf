package eu.xenit.move2alf.core.cyclestate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class CycleStateManagerImpl implements CycleStateManager {

	private final Map<Integer, Map<String, Serializable>> state;

	public CycleStateManagerImpl() {
		this.state = new HashMap<Integer, Map<String, Serializable>>();
	}

	@Override
	public Map<String, Serializable> getState(final int cycleId) {
		return state.get(cycleId);
	}

	@Override
	public void initializeState(final int cycleId) {
		state.put(cycleId, new HashMap<String, Serializable>());
	}

	@Override
	public void destroyState(final int cycleId) {
		state.remove(cycleId);
	}

}
