package eu.xenit.move2alf.core.dto;

import java.util.Set;

import eu.xenit.move2alf.common.IdObject;

public class Report extends IdObject {
	private Job job;
	
	private String name;
	
	private String emailList;
	
	private String className;
	
	private Set<ReportActionProperty> reportActionProperties;
	
	public Report() {
		
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Job getJob() {
		return job;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setEmailList(String emailList) {
		this.emailList = emailList;
	}

	public String getEmailList() {
		return emailList;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public void setReportActionProperties(Set<ReportActionProperty> reportActionProperties) {
		this.reportActionProperties = reportActionProperties;
	}

	public Set<ReportActionProperty> getReportActionProperties() {
		return reportActionProperties;
	}
	
}
