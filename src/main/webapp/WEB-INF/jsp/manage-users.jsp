<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2>Manage Users</h2>

<p>Actions: <a href="<spring:url value="/users/add" htmlEscape="true" />">add user</a></p>
<table>
	<thead>
		<tr>
			<th>Username</th>
			<th>Roles</th>
			<th></th>
		</tr>
	</thead>
	<c:forEach var="user" items="${userInfoList}">
		<tr>
			<td><c:out value="${user.userName}" /></td>
			<td>
				<c:out value="${user.role}" />
				</td>
			<td><a href="<spring:url value="/user/${user.userName}/edit/password" htmlEscape="true" />">edit password</a></td>
			<td><a href="<spring:url value="/user/${user.userName}/edit/role" htmlEscape="true" />">edit role</a></td>
			<td><a href="<spring:url value="/user/${user.userName}/delete" htmlEscape="true" />">delete</a></td>
		</tr>
	</c:forEach>
</table>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
