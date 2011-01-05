<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2>Manage users</h2>

<p>List of users, edit user, add user, delete user...</p>
<p>Actions: <a href="dsfsdf">Add new user</a></p>
<table>
	<thead>
		<tr>
			<th>Username</th>
			<th>Roles</th>
			<th>Actions</th>
		</tr>
	</thead>
	<c:forEach var="user" items="${users}">
		<tr>
			<td><c:out value="${user.userName}" /></td>
			<td><c:forEach var="role" items="${user.userRoleSet}">
				<c:out value="${role.role}" />
				<br />
			</c:forEach></td>
			<td><a href="asdf">edit</a> | <a href="sdf">delete</a></td>
		</tr>
	</c:forEach>
</table>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
