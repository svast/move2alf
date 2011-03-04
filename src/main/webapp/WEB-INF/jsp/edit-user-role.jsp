<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2>Change <c:out value="${user.userName}" />'s role</h2>

<div class="frame-job">

<form:form modelAttribute="userClass" method="post" name="changeRole" >
<table class="indent">
<tr>
<td>Please enter your password:</td>
<td><form:password path="oldPassword" size="15" maxlength="15" /></td>
<td><form:errors path="oldPassword" cssClass="error"/></td>
</tr>
</table>
<br />
<br />
<table class="indent">
<tr>
<td>Role: </td>
<td><form:select path="role">
    		<form:options items="${roleList}" />
		</form:select></td>
</tr>
</table>

<a href="<spring:url value="/users" htmlEscape="true" />" class="left"><button type="button">Cancel</button></a>
<input type="submit" value="Update role" class="right" />
</form:form>
</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
