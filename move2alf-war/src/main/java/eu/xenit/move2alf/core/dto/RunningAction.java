package eu.xenit.move2alf.core.dto;

import eu.xenit.move2alf.common.IdObject;

public class RunningAction extends IdObject {
	private Cycle cycle;
	
	private ConfiguredAction configuredAction;

	public RunningAction() {
		
	}

	public void setCycle(Cycle cycle) {
		this.cycle = cycle;
	}

	public Cycle getCycle() {
		return cycle;
	}

	public void setConfiguredAction(ConfiguredAction configuredAction) {
		this.configuredAction = configuredAction;
	}

	public ConfiguredAction getConfiguredAction() {
		return configuredAction;
	}
}
