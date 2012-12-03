package eu.xenit.move2alf.web.dto;

import org.hibernate.validator.constraints.NotEmpty;

public class EditPassword {

	@NotEmpty
	private String oldPassword;

	@NotEmpty
	private String newPassword;

	@NotEmpty
	private String newPasswordRetype;

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
