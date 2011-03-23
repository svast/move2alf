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

<h2>Dashboard</h2>

<div class="frame-dashboard">
<h3 class="left">Jobs</h3>

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

		<div class="link left"><c:out value="${jobInfo.jobName}" /></div>
		
			<%if(roleCheck=="jobAdmin" || roleCheck=="systemAdmin"){ %>
				<div class="link right"><a href="<spring:url value="/job/${jobInfo.jobId}/cycle/run" htmlEscape="true" />" >run poller</a>	<a href="<spring:url value="/job/${jobInfo.jobId}/edit" htmlEscape="true" />">edit</a></div>
			<%}else if(roleCheck=="scheduleAdmin"){ %>
				<div class="link right"><a href="<spring:url value="/job/${jobInfo.jobId}/cycle/run" htmlEscape="true" />" >run poller</a>	<a href="<spring:url value="/job/${jobInfo.jobId}/edit/schedule" htmlEscape="true" />">edit schedule</a></div>
			<%}%>
			
			<br class="clear">
			
		<p class="reduce-bottom">
		
		<c:if test='${-1 == jobInfo.cycleId}'>
		Last run: None
		<br />
		Status: Not running
		</c:if>
		<c:if test='${-1 != jobInfo.cycleId}'>
		
			<div class=hide">
				<fmt:formatDate var="date" value="${jobInfo.cycleStartDateTime}" pattern="yyyy-MM-dd" type="both"/>
			</div>
			
			<c:if test='${date == currentDate}'>
				Last run: today <fmt:formatDate value="${jobInfo.cycleStartDateTime}" pattern="HH:mm" type="both"/>
			</c:if>
			<c:if test='${date != currentDate}'>
				Last run: <fmt:formatDate value="${jobInfo.cycleStartDateTime}" pattern="yyyy-MM-dd HH:mm" type="both"/>
			</c:if>
			<br />
			Status: <c:out value="${jobInfo.scheduleState}" />
		<br />
		</c:if>
		</p>
		<br />

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
