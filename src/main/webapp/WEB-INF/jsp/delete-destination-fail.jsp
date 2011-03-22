<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2>Delete Failed</h2>

<div class="center">

You cannot delete destination "<c:out value="${destination.parameters.name}" />" because it is currently being used by a job.
<br />
<br />
<button type="button" onclick="javascript:location.href ='<spring:url value="/destinations" htmlEscape="true" />';">Ok</button>
</div>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
