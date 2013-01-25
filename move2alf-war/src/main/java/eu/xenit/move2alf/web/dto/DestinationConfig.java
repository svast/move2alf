package eu.xenit.move2alf.web.dto;

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

	private int nbrThreads = 1;

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

}
