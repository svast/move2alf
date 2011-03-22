package eu.xenit.move2alf.core.enums;

import java.util.EnumSet;

public enum ERole {
	SYSTEM_ADMIN("System admin"),
	JOB_ADMIN("Job admin"),
	SCHEDULE_ADMIN("Schedule admin"),
	CONSUMER("Consumer");
	
	private String displayName;

	private ERole(String displayName) {
	    this.displayName = displayName;
	  }
	
	public String getDisplayName() {
	    return displayName;
	  }
	
	public static ERole getByDisplayName(String displayName){
		ERole returnValue = null;
		for(final ERole element : EnumSet.allOf(ERole.class)){
			if(element.getDisplayName().equals(displayName)){
				returnValue = element;
			}
		}
		return returnValue;
	}
}
