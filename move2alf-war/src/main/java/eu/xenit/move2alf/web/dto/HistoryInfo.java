package eu.xenit.move2alf.web.dto;

import java.util.Date;

public class HistoryInfo {
	private int cycleId = -1;
	private Date cycleStartDateTime;
	private String scheduleState;
	private int nbrOfDocuments;

	public HistoryInfo(int cycleId, Date cycleStartDateTime, String scheduleState, int nbrOfDocuments) {
		this.cycleId=cycleId;
		this.cycleStartDateTime = cycleStartDateTime;
		this.scheduleState = scheduleState;
		this.nbrOfDocuments = nbrOfDocuments;
	}

	public HistoryInfo() {
		// TODO Auto-generated constructor stub
	}

	public void setCycleId(int cycleId) {
		this.cycleId = cycleId;
	}

	public int getCycleId() {
		return cycleId;
	}

	public void setCycleStartDateTime(Date cycleStartDateTime) {
		this.cycleStartDateTime = cycleStartDateTime;
	}

	public Date getCycleStartDateTime() {
		return cycleStartDateTime;
	}

	public void setScheduleState(String scheduleState) {
		this.scheduleState = scheduleState;
	}

	public String getScheduleState() {
		return scheduleState;
	}
	
	public void setNbrOfDocuments(int nbrOfDocuments) {
		this.nbrOfDocuments = nbrOfDocuments;
	}

	public int getNbrOfDocuments() {
		return nbrOfDocuments;
	}
}
