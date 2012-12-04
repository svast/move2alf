package eu.xenit.move2alf.web.dto;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

public class ScheduleConfig {

	private String runFrequency;

	private String singleDate;

	private String singleTime;

	private String hourTime;

	private String dayTime;

	private String weekDay;

	private String weekTime;

	private String cronJob;

	private List<String> cron;

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

}
