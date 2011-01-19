package eu.xenit.move2alf.web.dto;


public class JobConfig {
	private String name;

	private String description;

	private String inputFolder;
	
	private String destinationFolder;
	
	private String runFrequency;
	
	private String runDate;
	
	private String runTime;
	
	private String metadata;
	
	private String transform;
	
	private String docExist;
	
	private boolean moveBeforeProc = false;
	
	private String beforeProcPath;
	
	private boolean moveAfterLoad = false;
	
	private String afterLoadPath;
	
	private boolean moveNotLoad = false;
	
	private String notLoadPath;
	
	private boolean sendNotification = false;
	
	private String emailAddressError;
	
	private boolean sendReport = false;
	
	private String emailAddressRep;
	
	private String destinationName;
	
	private String destinationType;
	
	private String destinationURL;
	
	private String alfUser;
	
	private String alfPswd;
	
	private int nbrThreads = 5;
	
	public JobConfig() {

	}
	
	public JobConfig(String name, String description) {
		this.name = name;
		this.description = description;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setInputFolder(String inputFolder) {
		this.inputFolder = inputFolder;
	}

	public String getInputFolder() {
		return inputFolder;
	}
	
	public void setDestinationFolder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}

	public String getDestinationFolder() {
		return destinationFolder;
	}
	
	public void setRunFrequency(String runFrequency) {
		this.runFrequency = runFrequency;
	}

	public String getRunFrequency() {
		return runFrequency;
	}
	
	public void setRunDate(String runDate) {
		this.runDate = runDate;
	}

	public String getRunDate() {
		return runDate;
	}
	
	public void setRunTime(String runTime) {
		this.runTime = runTime;
	}

	public String getRunTime() {
		return runTime;
	}
	
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getMetadata() {
		return metadata;
	}
	
	public void setTransform(String transform) {
		this.transform = transform;
	}

	public String getTransform() {
		return transform;
	}
	
	public void setDocExist(String docExist) {
		this.docExist = docExist;
	}

	public String getDocExist() {
		return docExist;
	}
	
	public void setMoveBeforeProc(boolean moveBeforeProc) {
		this.moveBeforeProc = moveBeforeProc;
	}

	public boolean getMoveBeforeProc() {
		return moveBeforeProc;
	}
	
	public void setBeforeProcPath(String beforeProcPath) {
		this.beforeProcPath = beforeProcPath;
	}

	public String getBeforeProcPath() {
		return beforeProcPath;
	}
	
	public void setMoveAfterLoad(boolean moveAfterLoad) {
		this.moveAfterLoad = moveAfterLoad;
	}
	
	public boolean getMoveAfterLoad() {
		return moveAfterLoad;
	}
	
	public void setAfterLoadPath(String afterLoadPath) {
		this.afterLoadPath = afterLoadPath;
	}

	public String getAfterLoadPath() {
		return afterLoadPath;
	}
	
	public void setMoveNotLoad(boolean moveNotLoad) {
		this.moveNotLoad = moveNotLoad;
	}

	public boolean getMoveNotLoad() {
		return moveNotLoad;
	}
	
	public void setNotLoadPath(String notLoadPath) {
		this.notLoadPath = notLoadPath;
	}

	public String getNotLoadPath() {
		return notLoadPath;
	}
	
	public void setSendNotification(boolean sendNotification) {
		this.sendNotification = sendNotification;
	}

	public boolean getSendNotification() {
		return sendNotification;
	}
	
	public void setEmailAddressError(String emailAddressError) {
		this.emailAddressError = emailAddressError;
	}

	public String getEmailAddressError() {
		return emailAddressError;
	}
	
	public void setSendReport(boolean sendReport) {
		this.sendReport = sendReport;
	}

	public boolean getSendReport() {
		return sendReport;
	}
	
	public void setEmailAddressRep(String emailAddressRep) {
		this.emailAddressRep = emailAddressRep;
	}

	public String getEmailAddressRep() {
		return emailAddressRep;
	}
	
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public String getDestinationName() {
		return destinationName;
	}
	
	public void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}

	public String getDestinationType() {
		return destinationType;
	}
	
	public void setDestinationURL(String destinationURL) {
		this.destinationURL = destinationURL;
	}

	public String getDestinationURL() {
		return destinationURL;
	}
	
	public void setAlfUser(String alfUser) {
		this.alfUser = alfUser;
	}

	public String getAlfUser() {
		return alfUser;
	}
	
	public void setAlfPswd(String alfPswd) {
		this.alfPswd = alfPswd;
	}

	public String getAlfPswd() {
		return alfPswd;
	}
	
	public void setNbrThreads(int nbrThreads) {
		this.nbrThreads = nbrThreads;
	}

	public int getNbrThreads() {
		return nbrThreads;
	}

}

