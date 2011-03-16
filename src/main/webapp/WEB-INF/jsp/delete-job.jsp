<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">
<div class="center">
<h2>Delete Job</h2>

Are you sure you want to delete "<c:out value="${job.name}"/>"?
</div>
<br />
<form:form modelAttribute="job" method="post">
<button type="button" class="left" onclick="javascript:location.href ='<spring:url value="/job/dashboard" htmlEscape="true" />';">Cancel</button>
<div class="right"><input type="submit" value="Delete Job"/></div>
</form:form>
	
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
