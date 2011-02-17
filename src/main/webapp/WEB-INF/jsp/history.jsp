<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="/WEB-INF/jsp/header.jsp" %>



<div class="span-24 last main">

<h2>Historical reports</h2>
<br />

<div class="frame-job">
<h3><c:out value="${job.name}" /></h3>

<c:if test='${empty cycles}'>
This job has not yet run.
</c:if>

<c:if test='${not empty cycles}'>
<table class="table-border-thin">
<tr class="table-border-thin">
<th class="table-border-thin">Start time</th>
<th class="table-border-thin">Status</th>
<th class="table-border-thin">Nr. of documents</th>
<th class="table-border-thin"></th>
</tr>

<c:forEach var="cycle" items="${cycles}">
<tr>
<td class="table-border-thin"><fmt:formatDate value="${cycle.startDateTime}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></td>
<td class="table-border-thin"><c:out value="${cycle.schedule.state.displayName}"/></td>
<td class="table-border-thin"><c:out value="" /></td>
<td class="table-border-thin"><a href="<spring:url value="/job/${job.id}/${cycle.id}/report" htmlEscape="true" />">view details</a></td>
</tr>

</c:forEach>
</table>
</c:if>


</div>

</div>


<%@ include file="/WEB-INF/jsp/footer.jsp" %>
