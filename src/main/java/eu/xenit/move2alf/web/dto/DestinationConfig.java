package eu.xenit.move2alf.web.dto;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

public class DestinationConfig {
	
	@NotEmpty
	private String destinationName;
	
	@NotEmpty
	private String destinationType;
	
	@NotEmpty
	private String destinationURL;
	
	@NotEmpty
	private String alfUser;
	
	@NotEmpty
	private String alfPswd;
	
	@NotNull
	private int nbrThreads = 5;
	
	
	private String dest;
	
	@NotEmpty
	private List<String> sourceSink;
	
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
	
	public void setSourceSink(List<String> sourceSink) {
		this.sourceSink = sourceSink;
	}

	public List<String> getSourceSink() {
		return sourceSink;
	}
	
	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getDest() {
		return dest;
	}
}
