/**
 * 
 */
package eu.xenit.move2alf.web.dto;

public class User {
	private String userName;
	
	private String password;
	
	private String role;
	
	private String oldPassword;
	
	private String newPassword;
	
	private String newPasswordRetype;
	
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
	
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
	public String getOldPassword() {
		return oldPassword;
	}
	
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	public String getNewPassword() {
		return newPassword;
	}
	
	public void setNewPasswordRetype(String newPasswordRetype) {
		this.newPasswordRetype = newPasswordRetype;
	}
	
	public String getNewPasswordRetype() {
		return newPasswordRetype;
	}
}