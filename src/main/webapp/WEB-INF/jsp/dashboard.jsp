<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="/WEB-INF/jsp/header.jsp" %>

<%
	String currentDate="";
	java.util.Date currentDateTime = new java.util.Date();
	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
	currentDate = formatter.format(currentDateTime);
	pageContext.setAttribute("currentDate", currentDate);
%>

<div class="span-24 last main">

<div class="frame-dashboard">
<h2 class="left">Jobs</h2>

<% 
	//All role checks are done in the header.jsp
	if(roleCheck=="jobAdmin" || roleCheck=="systemAdmin"){ 
%>
	<h4 class="right"><a href="<spring:url value="/job/create" htmlEscape="true" />">Create new job</a></h4>
<%} %>
<br class="clear">

<c:if test="${empty jobInfoList}">
<p>No jobs found, use the link above to create a new one</p>
</c:if>

<table class="table-jobAndDestination">
<c:forEach var="jobInfo" items="${jobInfoList}">
		<tr>
		<td class="table-border">

		<h4 class="link left jobname"><c:out value="${jobInfo.jobName}" /></h4>
		
			<%if(roleCheck=="jobAdmin" || roleCheck=="systemAdmin"){ %>
				<div class="link right"><a href="<spring:url value="/job/${jobInfo.jobId}/cycle/run" htmlEscape="true" />" >Run</a>	<a href="<spring:url value="/job/${jobInfo.jobId}/edit" htmlEscape="true" />">Edit</a></div>
			<%}else if(roleCheck=="scheduleAdmin"){ %>
				<div class="link right"><a href="<spring:url value="/job/${jobInfo.jobId}/cycle/run" htmlEscape="true" />" >Run</a>	<a href="<spring:url value="/job/${jobInfo.jobId}/edit/schedule" htmlEscape="true" />">Edit schedule</a></div>
			<%}%>
			
			<br class="clear">
			
		<div class="properties">
		<c:if test='${-1 == jobInfo.cycleId}'>
		<div class="labelcontainer"><span class="left label">Last run:</span> None</div>
		<div class="labelcontainer"><span class="left label">Status:</span> Not running</div>
		</c:if>
		<c:if test='${-1 != jobInfo.cycleId}'>
		
			<div class=hide">
				<fmt:formatDate var="date" value="${jobInfo.cycleStartDateTime}" pattern="yyyy-MM-dd" type="both"/>
			</div>
			
			<c:if test='${date == currentDate}'>
				<div class="labelcontainer"><span class="left label">Last run:</span> today <fmt:formatDate value="${jobInfo.cycleStartDateTime}" pattern="HH:mm" type="both"/></div>
			</c:if>
			<c:if test='${date != currentDate}'>
				<div class="labelcontainer"><span class="left label">Last run:</span> <fmt:formatDate value="${jobInfo.cycleStartDateTime}" pattern="yyyy-MM-dd HH:mm" type="both"/></div>
			</c:if>
			<div class="labelcontainer"><span class="left label">Status:</span> <c:out value="${jobInfo.scheduleState}" /></div>
		</c:if>
		</div>

		<div class="link"><a href="<spring:url value="/job/${jobInfo.jobId}/history" htmlEscape="true"/>" >History</a>
		<c:if test='${-1 != jobInfo.cycleId}'>
		<a href="<spring:url value="/job/${jobInfo.jobId}/report" htmlEscape="true" />">View last report</a>
		</c:if>
		</div>
		
		</td>
		</tr>
		<tr class="spacer"><td></td></tr>
		
	</c:forEach>

</table>

</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
