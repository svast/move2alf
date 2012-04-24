<#include "general.ftl" />
<#include "forms.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="Add User">
<form class="form-horizontal" method="post" name="editUser" action="<@spring.url relativeUrl=("/user/add") />" />
	<#include "userform.ftl" />
</form>
</@bodyMenu>
</@html>