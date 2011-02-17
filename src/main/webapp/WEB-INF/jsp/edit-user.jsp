<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2>Change <c:out value="${user.userName}" />'s password</h2>

<div class="frame-job">

<form:form modelAttribute="userClass" method="post" name="changePassword" onsubmit='return comparePasswords();' >
<table class="indent">
<tr>
<td>Please enter your password:</td>
<td><form:password path="oldPassword" size="15" maxlength="15" /></td>
</tr>
</table>
<br />
<br />
<table class="indent">
<tr>
<td>new user's password:</td>
<td><form:password path="newPassword" size="15" maxlength="15" /></td>
</tr>
<tr>
<td>Please retype the new user's password:</td>
<td><form:password path="newPasswordRetype" size="15" maxlength="15" /></td>
</tr>
</table>

<a href="<spring:url value="/users" htmlEscape="true" />" class="left"><button type="button">Cancel</button></a>
<input type="submit" value="Update password" class="right" />
</form:form>
</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
