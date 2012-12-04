package eu.xenit.move2alf.core.cyclestate;

import java.io.Serializable;
import java.util.Map;

public interface CycleStateManager {
	Map<String, Serializable> getState(int cycleId);

	void initializeState(int cycleId);

	void destroyState(int cycleId);
}
