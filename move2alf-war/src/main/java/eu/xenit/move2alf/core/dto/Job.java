package eu.xenit.move2alf.core.dto;

import eu.xenit.move2alf.common.IdObject;

import java.util.Date;
import java.util.Set;

public class Job extends IdObject {

	private String name;

	private String description;

	private UserPswd creator;

	private Date creationDateTime;

	private Date lastModifyDateTime;
	
	private ConfiguredAction firstConfiguredAction;
	
	private Set<Schedule> schedules;
	
	private Set<Cycle> cycles;

    private boolean sendReport;

    public boolean isSendReport() {
        return sendReport;
    }

    public void setSendReport(boolean sendReport) {
        this.sendReport = sendReport;
    }

    public String getSendReportTo() {
        return sendReportTo;
    }

    public void setSendReportTo(String sendReportTo) {
        this.sendReportTo = sendReportTo;
    }

    public boolean isSendErrorReport() {
        return sendErrorReport;
    }

    public void setSendErrorReport(boolean sendErrorReport) {
        this.sendErrorReport = sendErrorReport;
    }

    public String getSendErrorReportTo() {
        return sendErrorReportTo;
    }

    public void setSendErrorReportTo(String sendErrorReportTo) {
        this.sendErrorReportTo = sendErrorReportTo;
    }

    private String sendReportTo;

    private boolean sendErrorReport;

    private String sendErrorReportTo;

	public Job() {

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

	public void setCreator(UserPswd creator) {
		this.creator = creator;
	}

	public UserPswd getCreator() {
		return creator;
	}

	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public Date getCreationDateTime() {
		return creationDateTime;
	}

	public void setLastModifyDateTime(Date lastModifyDateTime) {
		this.lastModifyDateTime = lastModifyDateTime;
	}

	public Date getLastModifyDateTime() {
		return lastModifyDateTime;
	}

	public void setFirstConfiguredAction(ConfiguredAction firstConfiguredAction) {
		this.firstConfiguredAction = firstConfiguredAction;
	}

	public ConfiguredAction getFirstConfiguredAction() {
		return firstConfiguredAction;
	}

	public void setSchedules(Set<Schedule> schedules) {
		this.schedules = schedules;
	}

	public Set<Schedule> getSchedules() {
		return schedules;
	}
	
	public void setCycles(Set<Cycle> cycles) {
		this.cycles = cycles;
	}
	
	public Set<Cycle> getCycles() {
		return cycles;
	}
	
}
