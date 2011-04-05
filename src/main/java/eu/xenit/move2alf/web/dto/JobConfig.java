package eu.xenit.move2alf.web.dto;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

public class JobConfig {
	private int id;

	@NotEmpty
	private String name;

	@NotEmpty
	private String description;

	@NotEmpty
	private String inputFolder;

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

	@NotEmpty
	private List<String> cron;

	@NotEmpty
	private String metadata;

	@NotEmpty
	private String transform;

	@NotEmpty
	private String docExist;

	private String moveBeforeProc = "false";

	private String beforeProcPath;

	private String moveAfterLoad = "false";

	private String afterLoadPath;

	private String moveNotLoad = "false";

	private String notLoadPath;

	private String sendNotification = "false";

	private String emailAddressError;

	private String sendReport = "false";

	private String emailAddressRep;

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

	@NotEmpty
	private String dest;

	private List<String> sourceSink;

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
		this.cron = cron;
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
		this.moveBeforeProc = moveBeforeProc;
	}

	public String getMoveBeforeProc() {
		return moveBeforeProc;
	}

	public void setBeforeProcPath(String beforeProcPath) {
		this.beforeProcPath = beforeProcPath;
	}

	public String getBeforeProcPath() {
		return beforeProcPath;
	}

	public void setMoveAfterLoad(String moveAfterLoad) {
		this.moveAfterLoad = moveAfterLoad;
	}

	public String getMoveAfterLoad() {
		return moveAfterLoad;
	}

	public void setAfterLoadPath(String afterLoadPath) {
		this.afterLoadPath = afterLoadPath;
	}

	public String getAfterLoadPath() {
		return afterLoadPath;
	}

	public void setMoveNotLoad(String moveNotLoad) {
		this.moveNotLoad = moveNotLoad;
	}

	public String getMoveNotLoad() {
		return moveNotLoad;
	}

	public void setNotLoadPath(String notLoadPath) {
		this.notLoadPath = notLoadPath;
	}

	public String getNotLoadPath() {
		return notLoadPath;
	}

	public void setSendNotification(String sendNotification) {
		this.sendNotification = sendNotification;
	}

	public String getSendNotification() {
		return sendNotification;
	}

	public void setEmailAddressError(String emailAddressError) {
		this.emailAddressError = emailAddressError;
	}

	public String getEmailAddressError() {
		return emailAddressError;
	}

	public void setSendReport(String sendReport) {
		this.sendReport = sendReport;
	}

	public String getSendReport() {
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

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getDest() {
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


}
