<#include "general.ftl" />
<#include "forms.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="Create Job">
<#include "jobform-errors.ftl" />
<form class="form-horizontal" method="post" name="createJob" action="<@spring.url relativeUrl=("/job/create") />" />
	<#include "jobform.ftl" />	
</form>

</@bodyMenu>
</@html>