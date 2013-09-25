package eu.xenit.move2alf.web.dto;

import eu.xenit.move2alf.core.sharedresource.alfresco.DeleteOption;
import eu.xenit.move2alf.core.sharedresource.alfresco.InputSource;
import eu.xenit.move2alf.core.sharedresource.alfresco.WriteOption;
import eu.xenit.move2alf.logic.Mode;
import eu.xenit.move2alf.validation.CSVsize;
import eu.xenit.move2alf.validation.ParamList;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class JobModel {
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

    @NotNull(message="inputSoure is null")
    private InputSource inputSource;

    private int contentStoreId = -1;

    public int getContentStoreId() {
        return contentStoreId;
    }

    public void setContentStoreId(int contentStoreId) {
        this.contentStoreId = contentStoreId;
    }

    private String cmisURL;
    private String cmisUsername;
    private String cmisPassword;
    private String cmisQuery;

	@CSVsize(max=pathMaxLength, message="Max length of the concatenated input paths is " + pathMaxLength)
	private List<String> inputFolders;

	@Size(min=0, max=pathMaxLength, message="Max length of destination path is " + pathMaxLength)
	@NotEmpty(message="Please enter a destination path.")
	private String destinationFolder="/move2alf";

	private List<String> cron = new ArrayList<String>();


	@NotEmpty
	private String metadata;

	@NotEmpty
	private String transform;
	
	@NotNull(message="mode is null")
	private Mode mode;

	@NotNull(message="writeOption is null")
	private WriteOption writeOption;	
	
	@NotNull(message="deleteOption is null")
	private DeleteOption deleteOption;
	
	private boolean listIgnorePath;

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

    @Min(value=1, message="You have to specify a destination. If none is available, go to the destinations tab and create one first.")
	private int dest;

	public JobModel() {

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

    public InputSource getInputSource() {
        return inputSource;
    }

    public void setInputSource(final InputSource inputSource) {
        this.inputSource = inputSource;
    }

    public String getCmisURL() {
        return cmisURL;
    }

    public void setCmisURL(final String cmisURL) {
        this.cmisURL = cmisURL;
    }

    public String getCmisUsername() {
        return cmisUsername;
    }

    public String getCmisPassword() {
        return cmisPassword;
    }

    public void setCmisUsername(final String cmisUsername) {
        this.cmisUsername = cmisUsername;
    }

    public void setCmisPassword(final String cmisPassword) {
        this.cmisPassword = cmisPassword;
    }

    public String getCmisQuery() {
        return cmisQuery;
    }

    public void setCmisQuery(final String cmisQuery) {
        this.cmisQuery = cmisQuery;
    }

    public void setInputFolder(List<String> inputFolders) {
		this.inputFolders = inputFolders;
	}

	public List<String> getInputFolder() {
		return inputFolders;
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
	
	public Mode getMode(){
		return this.mode;
	}
	
	public void setMode(Mode mode){
		this.mode = mode;
	}

	public void setWriteOption(WriteOption writeOption) {
		this.writeOption = writeOption;
	}

	public WriteOption getWriteOption() {
		return writeOption;
	}

	public void setMoveBeforeProc(String moveBeforeProc) {
		this.moveBeforeProc = Boolean.valueOf(moveBeforeProc);
	}
	
	public void setMoveBeforeProc(boolean moveBeforeProc) {
		this.moveBeforeProc = moveBeforeProc;
	}

	public boolean getMoveBeforeProc() {
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
	
	public void setMoveAfterLoad(boolean moveAfterLoad) {
		this.moveAfterLoad = moveAfterLoad;
	}

	public boolean getMoveAfterLoad() {
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
	
	public void setMoveNotLoad(boolean moveNotLoad) {
		this.moveNotLoad = moveNotLoad;
	}

	public boolean getMoveNotLoad() {
		return moveNotLoad;
	}

	public void setMoveNotLoadText(String notLoadPath) {
		this.moveNotLoadText = notLoadPath;
	}

	public String getMoveNotLoadText() {
		return moveNotLoadText;
	}
	
	public void setSendNotification(boolean sendNotification) {
		this.sendNotification = sendNotification;
	}

	public void setSendNotification(String sendNotification) {
		this.sendNotification = Boolean.valueOf(sendNotification);
	}

	public boolean getSendNotification() {
		return sendNotification;
	}

	public void setSendNotificationText(String emailAddressError) {
		this.sendNotificationText = emailAddressError;
	}

	public String getSendNotificationText() {
		return sendNotificationText;
	}
	
	public void setSendReport(boolean sendReport) {
		this.sendReport = sendReport;
	}

	public void setSendReport(String sendReport) {
		this.sendReport = Boolean.valueOf(sendReport);
	}

	public boolean getSendReport() {
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

	public void setDeleteOption(DeleteOption deleteOption){
		this.deleteOption = deleteOption;
	}
	public DeleteOption getDeleteOption() {
		return this.deleteOption;
	}

	public void setListIgnorePath(boolean listIgnorePath){
		this.listIgnorePath = listIgnorePath;
	}
	
	public boolean getListIgnorePath() {
		return this.listIgnorePath;
	}

}
