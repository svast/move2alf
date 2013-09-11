<#include "../../general.ftl" />
<#include "../../forms.ftl" />

<@html>
    <@head>
    </@head>
    <@bodyMenu title="Create Castor Destination">
        <#include "../../jobform-errors.ftl" />
    <form class="form-horizontal" method="post" name="createDestination" action="<@spring.url relativeUrl=("/destinations/Castor/create") />" />
        <#include "form.ftl" />
    </form>
    </@bodyMenu>
</@html>