<#include "../../general.ftl" />
<#include "../../forms.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="Create Alfresco Destination">
<#include "../../jobform-errors.ftl" />
<form class="form-horizontal" method="post" name="createDestination" action="<@spring.url relativeUrl=("/destinations/Alfresco/create") />" />
	<#include "form.ftl" />
</form>
</@bodyMenu>
</@html>