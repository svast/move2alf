package eu.xenit.move2alf.web.dto;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

public class JobConfig {
	private int id;

	@NotEmpty
	private String name;

	@NotEmpty
	private String description;

	@NotEmpty
	private List<String> inputFolder;

	@NotEmpty
	private String destinationFolder;

	private String runFrequency;

	private String singleDate;

	private String singleTime;

	private String hourTime;

	private String dayTime;

	private String weekDay;

	private String weekTime;

	private String cronJob;

	private List<String> cron = new ArrayList<String>();

	@NotEmpty
	private String metadata;

	@NotEmpty
	private String transform;

	@NotEmpty
	private String docExist;

	private Boolean moveBeforeProc = false;

	private String moveBeforeProcText;

	private Boolean moveAfterLoad = false;

	private String moveAfterLoadText;

	private Boolean moveNotLoad = false;

	private String moveNotLoadText;

	private Boolean sendNotification = false;

	private String sendNotificationText;

	private Boolean sendReport = false;

	private String sendReportText;

	private String destinationName;

	private String destinationType;

	private String destinationURL;

	private String alfUser;

	private String alfPswd;

	private int nbrThreads = 5;
	
	private String extension = "*";
	
	private String parameterMetadataName = "";
	
	private String parameterMetadataValue = "";
	
	private String parameterTransformName = "";
	
	private String parameterTransformValue = "";
	
	private List<String> paramMetadata;
	
	private List<String> paramTransform;
	
	private String command;
	
	private String commandAfter;

	private int dest;

	private List<String> sourceSink;
	
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

	public void setRunFrequency(String runFrequency) {
		this.runFrequency = runFrequency;
	}

	public String getRunFrequency() {
		return runFrequency;
	}

	public void setSingleDate(String singleDate) {
		this.singleDate = singleDate;
	}

	public String getSingleDate() {
		return singleDate;
	}

	public void setSingleTime(String singleTime) {
		this.singleTime = singleTime;
	}

	public String getSingleTime() {
		return singleTime;
	}

	public void setHourTime(String hourTime) {
		this.hourTime = hourTime;
	}

	public String getHourTime() {
		return hourTime;
	}

	public void setDayTime(String dayTime) {
		this.dayTime = dayTime;
	}

	public String getDayTime() {
		return dayTime;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public void setWeekTime(String weekTime) {
		this.weekTime = weekTime;
	}

	public String getWeekTime() {
		return weekTime;
	}

	public void setCronJob(String cronJob) {
		this.cronJob = cronJob;
	}

	public String getCronJob() {
		return cronJob;
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

	public void setSourceSink(List<String> sourceSink) {
		this.sourceSink = sourceSink;
	}

	public List<String> getSourceSink() {
		return sourceSink;
	}
	
	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getExtension() {
		return extension;
	}
	
	public void setParameterMetadataName(String parameterMetadataName) {
		this.parameterMetadataName = parameterMetadataName;
	}

	public String getParameterMetadataName() {
		return parameterMetadataName;
	}
	
	public void setParameterMetadataValue(String parameterMetadataValue) {
		this.parameterMetadataValue = parameterMetadataValue;
	}

	public String getParameterMetadataValue() {
		return parameterMetadataValue;
	}
	
	public void setParameterTransformName(String parameterTransformName) {
		this.parameterTransformName = parameterTransformName;
	}

	public String getParameterTransformName() {
		return parameterTransformName;
	}
	
	public void setParameterTransformValue(String parameterTransformValue) {
		this.parameterTransformValue = parameterTransformValue;
	}

	public String getParameterTransformValue() {
		return parameterTransformValue;
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

}
