<#include "general.ftl" />
<#assign activeMenu="Manage destinations" />
<@html>
<@head>
</@head>
<@bodyMenu title="Destinations" >

<script src="<@spring.url relativeUrl="/js/jquery.tablesorter.js" />"></script>
<script >
$(function() {
	$("table#destinations").tablesorter({
		headers:{
			0:{sorter: false},
			6:{sorter: false}
		}
	});
});

function deleteDestination(id){
	if(confirm("Are you sure you want to delete this destination?")){
		window.location.href = "<@spring.url relativeUrl="/destination/" />"+id+"/delete";
	}
}
</script>

<table id="destinations" class="table-striped wide tablesorter">
	<col class="edit-column" />
	<col />
	<col />
	<col />
	<col />
	<col />
	<thead>
		<tr>
			<th></th>
			<th>Name</th>
			<th>Type</th>
			<th>URL</th>
			<th>Username</th>
			<th>Threads</th>
		</tr>
	</thead>
	<tbody>
		<#list destinations as destination>
		<tr>
			<td><a href="<@spring.url relativeUrl="/destinations/${destination.type}/${destination.id}/edit" />"><img src="<@spring.url relativeUrl="/images/edit-icon.png"/>" alt="edit" /></a></td>
			<td>${destination.name}</td>
			<td>${destination.type}</td>
			<td>${destination.url}</td>
			<td>${destination.userName}</td>
			<td>${destination.threads}</td>
		</tr>
		</#list>
	</tbody>
</table>
<b>Create new:<b>
    <#list destinationOptions?keys as type>
        <a class="btn btn-success" href="<@spring.url relativeUrl="/destinations/${type}/create" />">${destinationOptions[type].name}</a>
    </#list>

</@bodyMenu>
</@html>