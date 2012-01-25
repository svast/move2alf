<h1>Users</h1>
<#assign activeMenu="Manage users" />
<#include "header.ftl" />

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

<table id="users" class="zebra-striped">
	<thead>
		<tr>
			<th><a href="<@spring.url relativeUrl="/users/add" />"><img src="<@spring.url relativeUrl="/images/add-icon.png"/>" alt="Add new user" /></a></th>
			<th>Username</th>
			<th>Role</th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<#list userInfoList as user>
		<tr>
			<td style="width:10px" ><a href="<@spring.url relativeUrl="/user/${user.userName}/edit" />"><img src="<@spring.url relativeUrl="/images/edit-icon.png"/>" alt="edit" /></a></td>
			<td>${user.userName}</td>
			<td>${user.role}</td>
			<td><a href="<@spring.url relativeUrl="/user/${user.userName}/delete" />"><img src="<@spring.url relativeUrl="/images/delete-icon.png"/>" alt="delete" /></a></td>
		</tr>
		</#list>
	</tbody>
</table>

<#include "footer.ftl" />