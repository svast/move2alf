package eu.xenit.move2alf.core.enums;

import java.util.EnumSet;

public enum ERole{
	ROLE_SYSTEM_ADMIN("System admin"),
	ROLE_JOB_ADMIN("Job admin"),
	ROLE_SCHEDULE_ADMIN("Schedule admin"),
	ROLE_CONSUMER("Consumer");
	
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
