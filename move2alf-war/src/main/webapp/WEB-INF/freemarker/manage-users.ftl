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

function deleteUser(user){
	if(confirm("Are you sure you want to delete user "+user)){
		window.location.href = "<@spring.url relativeUrl="/user/" />"+user+"/delete";
	}
}
</script>

<table id="users" class="table-striped wide tablesorter">
	<col class="edit-column" />
	<col />
	<col />
	<col class="delete-column" />
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
			<td><img class="clickable" onclick="deleteUser('${user.userName}')" src="<@spring.url relativeUrl="/images/delete-icon.png"/>" alt="delete" /></td>
		</tr>
		</#list>
	</tbody>
</table>

</@bodyMenu>
</@html>