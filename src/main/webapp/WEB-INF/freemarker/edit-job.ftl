<#include "general.ftl" />
<#include "forms.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="Edit Job">
<#list errors! as error>
    <div class="alert alert-error">
    	${error.defaultMessage}
    </div>
</#list>
<form class="form-horizontal" method="post" name="editJob" action="<@spring.url relativeUrl=("/job/"+job.id+"/edit") />" />
	<@spring.formHiddenInput 'job.id'/>
	<#include "jobform.ftl" />	
</form>

</@bodyMenu>
</@html>