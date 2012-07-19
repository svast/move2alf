package eu.xenit.move2alf.core.enums;

public enum ECycleState {
	RUNNING("Running"),
	NOT_RUNNING("Not running");
	
	private String displayName;

	private ECycleState(String displayName) {
	    this.displayName = displayName;
	  }
	
	public String getDisplayName() {
	    return displayName;
	  }


}
