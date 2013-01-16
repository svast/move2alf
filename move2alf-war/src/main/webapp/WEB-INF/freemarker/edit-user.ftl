<#include "general.ftl" />
<#include "forms.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="Change password">
<#include "jobform-errors.ftl" />
<script>
	function checkPassword(){
		if($("#newPassword").val()!=$("#newPasswordRetype").val()){
			alert("The passwords you entered did not match!");
			return false;
		}
	}
</script>
<form class="form-horizontal" method="post" name="editPassword" onsubmit="return checkPassword()" action="<@spring.url relativeUrl=("/user/"+user.userName+"/edit/password") />" />
	<@labeledSingleLinePasswordInput label="Enter your own password" name="oldPassword" binding="editPassword.oldPassword" />
	<@labeledSingleLinePasswordInput label="New password" name="newPassword" binding="editPassword.newPassword" />
	<@labeledSingleLinePasswordInput label="Retype the new password" name="newPasswordRetype" binding="editPassword.newPasswordRetype" />

	<input class="btn btn-success" type="submit" value="Update password" />
	<a class="btn btn-inverse" href="<@spring.url relativeUrl="/users" />">Cancel</a>
</form>
</@bodyMenu>
</@html>