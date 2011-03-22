package eu.xenit.move2alf.core.enums;

public enum EProcessedDocumentStatus {
	OK("Ok"),
	FAILED("Failed");
	
	private String displayName;

	private EProcessedDocumentStatus(String displayName) {
	    this.displayName = displayName;
	  }
	
	public String getDisplayName() {
	    return displayName;
	  }
}
