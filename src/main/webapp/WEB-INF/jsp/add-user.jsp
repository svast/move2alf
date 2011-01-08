<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2>Add User</h2>

<form:form modelAttribute="user" method="post">
<p>Username: <form:input path="userName" size="30" maxlength="30" /></p>
<p>Password: <form:password path="password" size="30" maxlength="30" /></p>
<p>Role: <form:input path="role" size="30" maxlength="30" /></p>
<p><input type="submit" value="Add User"/></p>
</form:form>
	
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
