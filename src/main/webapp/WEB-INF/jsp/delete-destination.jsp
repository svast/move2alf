<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main center">

<h2>Delete Destination</h2>

Are you sure you want to delete destination "<c:out value="${destination.parameters.name}" />"?

<br />

<form:form modelAttribute="job" method="post">
<button type="button" class="left" onclick="javascript:location.href ='<spring:url value="/destinations" htmlEscape="true" />';">Cancel</button>
<div class="right"><input type="submit" value="Delete Destination"/></div>
</form:form>
	
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
