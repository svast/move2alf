package eu.xenit.move2alf.web.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import eu.xenit.move2alf.validation.CSVsize;
import eu.xenit.move2alf.validation.ParamList;

public class JobConfig {
	static private final int CONST_50 = 50;
	static private final int CONST_255 = 255;

	private final int jobNameMaxLength = CONST_50;
	private final int jobDescriptionMaxLength = CONST_255;
	private final int pathMaxLength = CONST_255;
	private final int extensionMaxLength = CONST_255;
	private final int commandMaxLength = CONST_255;
	private final int paramNameMaxLength = CONST_50;
	private final int paramValueMaxLength = CONST_255;
	private final int emailMaxLength = CONST_255;

	
	private int id;

	@NotEmpty(message="Please enter a name for the job.")
	@Size(min=0, max=jobNameMaxLength, message="Max length of name is " + jobNameMaxLength)
	private String name;

	@Size(min=0, max=jobDescriptionMaxLength, message="Max length of description is " + jobDescriptionMaxLength)
	@NotEmpty(message="Please enter a description for the job.")
	private String description;

	@NotEmpty(message="There should be at least one inputfolder!")
	@CSVsize(max=pathMaxLength, message="Max length of the concatenated input paths is " + pathMaxLength)
	private List<String> inputFolder;

	@Size(min=0, max=pathMaxLength, message="Max length of destination path is " + pathMaxLength)
	@NotEmpty(message="Please enter a destination path.")
	private String destinationFolder;

	private List<String> cron = new ArrayList<String>();

	@NotEmpty
	private String metadata;

	@NotEmpty
	private String transform;

	@NotEmpty
	private String docExist;

	private Boolean moveBeforeProc = false;

	@Size(min=0, max=pathMaxLength, message="Max length of 'Move before processing to path' is " + pathMaxLength)
	private String moveBeforeProcText;

	private Boolean moveAfterLoad = false;

	@Size(min=0, max=pathMaxLength, message="Max length of 'Move loaded files to path' is " + pathMaxLength)
	private String moveAfterLoadText;

	private Boolean moveNotLoad = false;

	@Size(min=0, max=pathMaxLength, message="Max length of 'Move not loaded files to path' is " + pathMaxLength)
	private String moveNotLoadText;

	private Boolean sendNotification = false;

	@Size(min=0, max=emailMaxLength, message="Max length of 'Send notification e-mails on errors to' is " + emailMaxLength)
	private String sendNotificationText;

	private Boolean sendReport = false;

	@Size(min=0, max=emailMaxLength, message="Max length of 'Send load reporting e-mails to' is " + emailMaxLength)
	private String sendReportText;

	private String destinationName;

	private String destinationType;

	private String destinationURL;

	private String alfUser;

	private String alfPswd;

	private int nbrThreads = 5;

	@Size(min=0, max=extensionMaxLength, message="Max length of extension is " + extensionMaxLength)
	private String extension = "*";
		
	@ParamList(maxKey=paramNameMaxLength, maxValue=paramValueMaxLength, message="Meta-data parameters: max length of name is " + paramNameMaxLength + ", max length of value is " + paramValueMaxLength)
	private List<String> paramMetadata;

	@ParamList(maxKey=paramNameMaxLength, maxValue=paramValueMaxLength, message="Tranformation parameters: max length of name is " + paramNameMaxLength + ", max length of value is " + paramValueMaxLength)
	private List<String> paramTransform;

	@Size(min=0, max=commandMaxLength, message="Max length of 'Command before' is " + commandMaxLength)
	private String command;

	@Size(min=0, max=commandMaxLength, message="Max length of 'Command after' is " + commandMaxLength)
	private String commandAfter;

	private int dest;
	
	private String inputPath="";

	public JobConfig() {

	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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

	public void setInputFolder(List<String> inputFolder) {
		this.inputFolder = inputFolder;
	}

	public List<String> getInputFolder() {
		return inputFolder;
	}
	
	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setDestinationFolder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}

	public String getDestinationFolder() {
		return destinationFolder;
	}

	public void setCron(List<String> cron) {
		if(cron != null)
			this.cron = cron;
		else
			this.cron = new ArrayList<String>();
	}

	public List<String> getCron() {
		return cron;
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

	public void setMoveBeforeProc(String moveBeforeProc) {
		this.moveBeforeProc = Boolean.valueOf(moveBeforeProc);
	}
	
	public void setMoveBeforeProc(Boolean moveBeforeProc) {
		this.moveBeforeProc = moveBeforeProc;
	}

	public Boolean getMoveBeforeProc() {
		return moveBeforeProc;
	}

	public void setMoveBeforeProcText(String beforeProcPath) {
		this.moveBeforeProcText = beforeProcPath;
	}

	public String getMoveBeforeProcText() {
		return moveBeforeProcText;
	}

	public void setMoveAfterLoad(String moveAfterLoad) {
		this.moveAfterLoad = Boolean.valueOf(moveAfterLoad);
	}
	
	public void setMoveAfterLoad(Boolean moveAfterLoad) {
		this.moveAfterLoad = moveAfterLoad;
	}

	public Boolean getMoveAfterLoad() {
		return moveAfterLoad;
	}

	public void setMoveAfterLoadText(String afterLoadPath) {
		this.moveAfterLoadText = afterLoadPath;
	}

	public String getMoveAfterLoadText() {
		return moveAfterLoadText;
	}

	public void setMoveNotLoad(String moveNotLoad) {
		this.moveNotLoad = Boolean.valueOf(moveNotLoad);
	}
	
	public void setMoveNotLoad(Boolean moveNotLoad) {
		this.moveNotLoad = moveNotLoad;
	}

	public Boolean getMoveNotLoad() {
		return moveNotLoad;
	}

	public void setMoveNotLoadText(String notLoadPath) {
		this.moveNotLoadText = notLoadPath;
	}

	public String getMoveNotLoadText() {
		return moveNotLoadText;
	}
	
	public void setSendNotification(Boolean sendNotification) {
		this.sendNotification = sendNotification;
	}

	public void setSendNotification(String sendNotification) {
		this.sendNotification = Boolean.valueOf(sendNotification);
	}

	public Boolean getSendNotification() {
		return sendNotification;
	}

	public void setSendNotificationText(String emailAddressError) {
		this.sendNotificationText = emailAddressError;
	}

	public String getSendNotificationText() {
		return sendNotificationText;
	}
	
	public void setSendReport(Boolean sendReport) {
		this.sendReport = sendReport;
	}

	public void setSendReport(String sendReport) {
		this.sendReport = Boolean.valueOf(sendReport);
	}

	public Boolean getSendReport() {
		return sendReport;
	}

	public void setSendReportText(String emailAddressRep) {
		this.sendReportText = emailAddressRep;
	}

	/*
	 * Comma separated list of emailadresses to send the reports to.
	 */
	public String getSendReportText() {
		return sendReportText;
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

	public void setDest(int dest) {
		this.dest = dest;
	}

	public int getDest() {
		return dest;
	}
	
	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getExtension() {
		return extension;
	}
	
	public void setParamMetadata(List<String> paramMetadata) {
		this.paramMetadata = paramMetadata;
	}

	public List<String> getParamMetadata() {
		return paramMetadata;
	}
	
	public void setParamTransform(List<String> paramTransform) {
		this.paramTransform = paramTransform;
	}

	public List<String> getParamTransform() {
		return paramTransform;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}
	
	public void setCommandAfter(String commandAfter) {
		this.commandAfter = commandAfter;
	}

	public String getCommandAfter() {
		return commandAfter;
	}

	public int getJobNameMaxLength() {
		return jobNameMaxLength;
	}

	public int getJobDescriptionMaxLength() {
		return jobDescriptionMaxLength;
	}

	public int getPathMaxLength() {
		return pathMaxLength;
	}

	public int getExtensionMaxLength() {
		return extensionMaxLength;
	}

	public int getCommandMaxLength() {
		return commandMaxLength;
	}

	public int getParamNameMaxLength() {
		return paramNameMaxLength;
	}

	public int getParamValueMaxLength() {
		return paramValueMaxLength;
	}

	public int getEmailMaxLength() {
		return emailMaxLength;
	}

}
