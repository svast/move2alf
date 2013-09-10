<#include "general.ftl" />
<#include "forms.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="Edit Destination">

<script>
    function deleteDestination(id){
        if(confirm("Are you sure you want to delete this destination?")){
            window.location.href = "<@spring.url relativeUrl="/destination/" />${destination.type}/"+id+"/delete";
        }
    }
</script>

<#include "jobform-errors.ftl" />
<form class="form-horizontal" method="post" name="editDestination" action="<@spring.url relativeUrl=("/destination/"+destination.type+"/"+destinationId+"/edit") />" />
	<#include "destinationform.ftl" />

<a class="btn btn-danger" onclick="deleteDestination('${destinationId}')" />Delete</a>

</form>
</@bodyMenu>
</@html>