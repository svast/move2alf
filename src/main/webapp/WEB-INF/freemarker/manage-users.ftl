<#include "general.ftl" />
<#assign activeMenu="Manage users" />
<@html>
<@head>
</@head>
<@bodyMenu title="Manage users">

<script src="<@spring.url relativeUrl="/js/jquery.tablesorter.js" />"></script>
<script >
$(function() {
	$("table#users").tablesorter({
		headers:{
			0:{sorter: false},
			3:{sorter: false}
		}
	});
});
</script>

<table id="users" class="table-striped wide">
	<thead>
		<tr>
			<th><a href="<@spring.url relativeUrl="/user/add" />"><img src="<@spring.url relativeUrl="/images/add-icon.png"/>" alt="Add new user" /></a></th>
			<th>Username</th>
			<th>Role</th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<#list userInfoList as user>
		<tr>
			<td><a href="<@spring.url relativeUrl="/user/${user.userName}/edit/password" />"><img src="<@spring.url relativeUrl="/images/edit-icon.png"/>" alt="edit" /></a></td>
			<td>${user.userName}</td>
			<td>${user.role}<a href="<@spring.url relativeUrl="/user/${user.userName}/edit/role" />"><img src="<@spring.url relativeUrl="/images/edit-icon.png"/>" alt="edit" /></a></td>
			<td><a href="<@spring.url relativeUrl="/user/${user.userName}/delete" />"><img src="<@spring.url relativeUrl="/images/delete-icon.png"/>" alt="delete" /></a></td>
		</tr>
		</#list>
	</tbody>
</table>

</@bodyMenu>
</@html>