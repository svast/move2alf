<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<div class="frame-job center">
<br />
<br />
<br />
<br />
<h3>Cycle started for job "<c:out value="${job.name}" />"</h3>

<button type="button" class="center" onclick="javascript:location.href ='<spring:url value="/job/dashboard" htmlEscape="true" />';">Ok</button>

</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>