<#include "general.ftl" />
<#include "forms.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="Edit Job">

<#include "jobform-errors.ftl" />
<form class="form-horizontal" method="post" name="editJob" action="<@spring.url relativeUrl=("/job/"+job.id+"/edit") />" />
	<@spring.formHiddenInput 'job.id'/>

	<#include "jobform.ftl" />	
</form>

</@bodyMenu>
</@html>