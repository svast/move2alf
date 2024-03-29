package eu.xenit.move2alf.web.dto;

import eu.xenit.move2alf.core.action.*;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.sharedresource.alfresco.DeleteOption;
import eu.xenit.move2alf.core.sharedresource.alfresco.InputSource;
import eu.xenit.move2alf.core.sharedresource.alfresco.WriteOption;
import eu.xenit.move2alf.core.simpleaction.MoveAction;
import eu.xenit.move2alf.core.simpleaction.SACMISInput;
import eu.xenit.move2alf.core.simpleaction.SAFilter;
import eu.xenit.move2alf.core.simpleaction.SASource;
import eu.xenit.move2alf.logic.Mode;
import eu.xenit.move2alf.validation.CSVsize;
import eu.xenit.move2alf.validation.ParamList;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

public class JobModel {
    static private final int CONST_50 = 50;
    static private final int CONST_255 = 255;
    static private final int CONST_5000 = 5000;

    private final int jobNameMaxLength = CONST_50;
    private final int jobDescriptionMaxLength = CONST_255;
    private final int cmisQueryMaxLength = CONST_5000;
    private final int pathMaxLength = CONST_255;
    private final int extensionMaxLength = CONST_255;
    private final int commandMaxLength = CONST_255;
    private final int paramNameMaxLength = CONST_50;
    private final int paramValueMaxLength = CONST_255;
    private final int emailMaxLength = CONST_255;


    private int id;

    @NotEmpty(message = "Please enter a name for the job.")
    @Size(min = 0, max = jobNameMaxLength, message = "Max length of name is " + jobNameMaxLength)
    private String name;

    @Size(min = 0, max = jobDescriptionMaxLength, message = "Max length of description is " + jobDescriptionMaxLength)
    @NotEmpty(message = "Please enter a description for the job.")
    private String description;

    @NotNull(message = "inputSoure is null")
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

    @Size(min = 0, max = cmisQueryMaxLength, message = "Max length of cmis query is " + cmisQueryMaxLength)
    private String cmisQuery;

    @CSVsize(max = pathMaxLength, message = "Max length of the concatenated input paths is " + pathMaxLength)
    private List<String> inputFolders;

    @Size(min = 0, max = pathMaxLength, message = "Max length of destination path is " + pathMaxLength)
    @NotEmpty(message = "Please enter a destination path.")
    private String destinationFolder = "/move2alf";

    private Boolean skipContentUpload = false;

    private Boolean cmisRecursive = false;

    private List<String> cron = new ArrayList<String>();


    @NotEmpty
    private String metadata;

    @NotEmpty
    private String transform;

    @NotNull(message = "mode is null")
    private Mode mode;

    @NotNull(message = "writeOption is null")
    private WriteOption writeOption;

    @NotNull(message = "deleteOption is null")
    private DeleteOption deleteOption;

    private boolean listIgnorePath;

    private Boolean moveBeforeProc = false;

    @Size(min = 0, max = pathMaxLength, message = "Max length of 'Move before processing to path' is " + pathMaxLength)
    private String moveBeforeProcText;

    private Boolean moveAfterLoad = false;

    @Size(min = 0, max = pathMaxLength, message = "Max length of 'Move loaded files to path' is " + pathMaxLength)
    private String moveAfterLoadText;

    private Boolean moveNotLoad = false;

    @Size(min = 0, max = pathMaxLength, message = "Max length of 'Move not loaded files to path' is " + pathMaxLength)
    private String moveNotLoadText;

    private Boolean sendNotification = false;

    @Size(min = 0, max = emailMaxLength, message = "Max length of 'Send notification e-mails on errors to' is " + emailMaxLength)
    private String sendNotificationText;

    private Boolean sendReport = false;

    @Size(min = 0, max = emailMaxLength, message = "Max length of 'Send load reporting e-mails to' is " + emailMaxLength)
    private String sendReportText;

    private String destinationName;

    private String destinationType;

    private String destinationURL;

    private String alfUser;

    private String alfPswd;

    private int nbrThreads = 5;

    // Integer can be null :-)
    @Digits(integer = 10000, fraction = 0, message = "batchSize parameter is incorrectly.")
    private Integer batchSize;

    @Size(min = 0, max = extensionMaxLength, message = "Max length of extension is " + extensionMaxLength)
    private String extension = "*";

    @ParamList(maxKey = paramNameMaxLength, maxValue = paramValueMaxLength, message = "Meta-data parameters: max length of name is " + paramNameMaxLength + ", max length of value is " + paramValueMaxLength)
    private List<String> paramMetadata;

    @ParamList(maxKey = paramNameMaxLength, maxValue = paramValueMaxLength, message = "Tranformation parameters: max length of name is " + paramNameMaxLength + ", max length of value is " + paramValueMaxLength)
    private List<String> paramTransform;

    @Size(min = 0, max = commandMaxLength, message = "Max length of 'Command before' is " + commandMaxLength)
    private String command;

    @Size(min = 0, max = commandMaxLength, message = "Max length of 'Command after' is " + commandMaxLength)
    private String commandAfter;

    @Min(value = 1, message = "You have to specify a destination. If none is available, go to the destinations tab and create one first.")
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

    public boolean getCmisRecursive() {
        return cmisRecursive;
    }

    public void setCmisRecursive(final String cmisRecursive) {
        this.cmisRecursive = Boolean.valueOf(cmisRecursive);
    }

    public void setCmisRecursive(final boolean cmisRecursive) {
        this.cmisRecursive = cmisRecursive;
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
        if (cron != null)
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

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(Mode mode) {
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

    public void setSkipContentUpload(String skipContentUpload) {
        this.skipContentUpload = Boolean.valueOf(skipContentUpload);
    }

    public void setSkipContentUpload(boolean skipContentUpload) {
        this.skipContentUpload = skipContentUpload;
    }

    public boolean getSkipContentUpload() {
        return skipContentUpload;
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

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
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

    public int getCmisQueryMaxLength() {
        return cmisQueryMaxLength;
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

    public void setDeleteOption(DeleteOption deleteOption) {
        this.deleteOption = deleteOption;
    }

    public DeleteOption getDeleteOption() {
        return this.deleteOption;
    }

    public void setListIgnorePath(boolean listIgnorePath) {
        this.listIgnorePath = listIgnorePath;
    }

    public boolean getListIgnorePath() {
        return this.listIgnorePath;
    }


    public static JobModel CreateDefault(String name) {
        final JobModel jobModel = new JobModel();
        jobModel.setName(name);
        jobModel.setDescription("Description of " + name);

        InputSource inputSource = InputSource.FILESYSTEM;
        List<String> inputFolder = new ArrayList();
        inputFolder.add("DUMMYFOLDER");

        jobModel.setSkipContentUpload(false);
        jobModel.setInputSource(inputSource);
        jobModel.setInputFolder(inputFolder);
        jobModel.setCmisURL("");
        jobModel.setCmisUsername("");
        jobModel.setCmisPassword("");
        jobModel.setCmisQuery("");
        jobModel.setCmisRecursive(false);
        jobModel.setDestinationFolder("");
        jobModel.setDest(0);
        jobModel.setContentStoreId(-1);
        jobModel.setMode(Mode.WRITE);
        jobModel.setWriteOption(WriteOption.SKIPANDIGNORE);
        jobModel.setDeleteOption(DeleteOption.SKIPANDIGNORE);
        jobModel.setListIgnorePath(false);
        jobModel.setMetadata("EmptyMetadataAction");
        jobModel.setTransform("");
        jobModel.setMoveBeforeProc(false);
        jobModel.setMoveBeforeProcText("");
        jobModel.setMoveAfterLoad(false);
        jobModel.setMoveAfterLoadText("");
        jobModel.setMoveNotLoad(false);
        jobModel.setMoveNotLoadText("");
        jobModel.setSendNotification(false);//job.isSendErrorReport());
        jobModel.setSendNotificationText("");//job.getSendErrorReportTo());
        jobModel.setSendReport(false);//job.isSendReport());
        jobModel.setSendReportText("");//job.getSendReportTo());
        jobModel.setExtension("*");
        jobModel.setCommand("");
        jobModel.setCommandAfter("");
        jobModel.setCron(new ArrayList<String>());
        //jobModel.setCron(getJobService().getCronjobsForJob(id));

        jobModel.setBatchSize(50);

        jobModel.setParamMetadata(new ArrayList<String>());

        jobModel.setParamTransform(new ArrayList<String>());

        return jobModel;
    }


}
