/**
 * 
 */
package eu.xenit.move2alf.web.dto;

public class User {
	private String userName;
	
	private String password;
	
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