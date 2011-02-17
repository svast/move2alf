<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2><c:out value="${user.userName}" />'s Profile</h2>

<table>

	<tr>
	<td>User name:</td><td><c:out value="${user.userName}" /></td>
	</tr>
	<tr>
		<td>Role:</td>
		<td class="text-align:left"><c:forEach var="role" items="${user.userRoleSet}">
			<c:out value="${role.role}" />
			<br />
		</c:forEach></td>
	</tr>
</table>
<p><a href="<spring:url value="/user/profile/${user.userName}/edit" htmlEscape="true" />" class="left"><button type="button">Change password</button></a></p>



</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
