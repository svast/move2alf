package eu.xenit.move2alf.core.dto;

import java.net.URL;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.enums.ECommunicationType;

public class Destination extends IdObject {
	private String name;
	
	// TODO: Hibernate mapping for URL type?
	private URL url;
	
	private String user;
	
	private String password;
	
	private ECommunicationType communicationType;
	
	public Destination() {
		
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public URL getUrl() {
		return url;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setCommunicationType(ECommunicationType communicationType) {
		this.communicationType = communicationType;
	}

	public ECommunicationType getCommunicationType() {
		return communicationType;
	}
}
