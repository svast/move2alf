package eu.xenit.move2alf.core.enums;

public enum EScheduleState {
	RUNNING("Running"),
	NOT_RUNNING("Not running");
	
	private String displayName;

	private EScheduleState(String displayName) {
	    this.displayName = displayName;
	  }
	
	public String getDisplayName() {
	    return displayName;
	  }


}
