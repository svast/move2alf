<#include "general.ftl" />
<#include "forms.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="Create Destination">
<form class="form-horizontal" method="post" name="createDestination" action="<@spring.url relativeUrl=("/destination/create") />" />
	<#include "destinationform.ftl" />

</form>
</@bodyMenu>
</@html>