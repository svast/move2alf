<#include "general.ftl" />
<#include "forms.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="Create Destination">
<#include "jobform-errors.ftl" />
<form class="form-horizontal" method="post" name="createDestination" action="<@spring.url relativeUrl=("/destination/create") />" />
	<#include "destinationform.ftl" />

</form>
</@bodyMenu>
</@html>