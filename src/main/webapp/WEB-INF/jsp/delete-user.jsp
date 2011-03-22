<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2>Delete User</h2>
<div class="center frame-job">

Are you sure you want to delete "<c:out value="${user.userName}"/>"?

<br />
<form:form modelAttribute="user" method="post">
<br />
<br />
<button type="button" class="left" onclick="javascript:location.href ='<spring:url value="/users" htmlEscape="true" />';">Cancel</button>
<div class="right"><input type="submit" value="Delete User"/></div>
</form:form>
<br />
</div>	
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
