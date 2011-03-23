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
		<td class="text-align:left">
			<c:out value="${role}" />
			</td>
	</tr>
</table>

<button type="button" class="left" onclick="javascript:location.href ='<spring:url value="/user/profile/${user.userName}/edit" htmlEscape="true" />';">Change password</button>



</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
