<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2>Delete User</h2>

Are you sure you want to delete "<c:out value="${user.userName}"/>"?

<form:form modelAttribute="user" method="post">
<p><input type="submit" value="Delete User"/></p>
</form:form>
	
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
