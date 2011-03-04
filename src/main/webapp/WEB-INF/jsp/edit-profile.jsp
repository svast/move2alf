<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2>Change password</h2>

<div class="frame-job">

<form:form modelAttribute="userClass" method="post" name="changePassword" onsubmit='return comparePasswords();' >
<table class="indent">
<tr>
<td>old password:</td>
<td><form:password path="oldPassword" size="15" maxlength="15" /></td>
<td><form:errors path="oldPassword" cssClass="error"/></td>
</tr>
<tr>
<td>new password:</td>
<td><form:password path="newPassword" size="15" maxlength="15" /></td>
<td><form:errors path="newPassword" cssClass="error"/></td>
</tr>
<tr>
<td>Please retype the new password:</td>
<td><form:password path="newPasswordRetype" size="15" maxlength="15" /></td>
</tr>
</table>

<a href="<spring:url value="/user/profile" htmlEscape="true" />" class="left"><button type="button">Cancel</button></a>
<input type="submit" value="Update password" class="right" />
</form:form>
</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
