/**
 * 
 */
package eu.xenit.move2alf.web.dto;

import org.hibernate.validator.constraints.NotEmpty;

public class User {

	@NotEmpty
	private String userName;

	@NotEmpty
	private String password;

	@NotEmpty
	private String role;

	public User() {

	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

}