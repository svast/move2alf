<#include "general.ftl" />
<#include "forms.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="Edit Destination">
<#include "jobform-errors.ftl" />
<form class="form-horizontal" method="post" name="editDestination" action="<@spring.url relativeUrl=("/destination/alfresco/"+destinationId+"/edit") />" />
	<#include "destinationform.ftl" />

</form>
</@bodyMenu>
</@html>