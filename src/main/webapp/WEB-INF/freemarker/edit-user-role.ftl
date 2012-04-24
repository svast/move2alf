<#include "general.ftl" />
<#include "forms.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="Change role">
<#include "jobform-errors.ftl" />
<form class="form-horizontal" method="post" name="edit" action="<@spring.url relativeUrl=("/user/"+user.userName+"/edit/role") />" />
	<@labeledSingleLinePasswordInput label="Enter your own password" name="oldPassword" binding="editRole.oldPassword" />
	<@labeledSelectList label="Role" name="role" options=roleList; item>
		${editRole.role}
		${item}
		<option value="${item}" <#if (editRole.role==item)>selected="selected"</#if>> ${item}</option>
	</@labeledSelectList>
	<input type="submit" value="Update role" />

</form>
</@bodyMenu>
</@html>