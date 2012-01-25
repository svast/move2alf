<h1>Destinations</h1>

<#assign activeMenu="Manage destinations" />
<#include "header.ftl" />

<script src="<@spring.url relativeUrl="/js/jquery.tablesorter.js" />"></script>
<script >
$(function() {
	$("table#destinations").tablesorter({
		headers:{
			0:{sorter: false}
		}
	});
});
</script>

<table id="destinations" class="zebra-striped">
	<thead>
		<tr>
			<th><a href="<@spring.url relativeUrl="/destinations/create" />"><img src="<@spring.url relativeUrl="/images/add-icon.png"/>" alt="Create new destination" /></a></th>
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
			<td style="width:10px" ><a href="<@spring.url relativeUrl="/destination/${destination.id}/edit" />"><img src="<@spring.url relativeUrl="/images/edit-icon.png"/>" alt="edit" /></a></td>
			<td>${destination.parameters.name}</td>
			<td>${typeNames[destination.className]}</td>
			<td>${destination.parameters.url}</td>
			<td>${destination.parameters.user}</td>
			<td>${destination.parameters.threads}</td>
		</tr>
		</#list>
	</tbody>
</table>

<#include "footer.ftl" />