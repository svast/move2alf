<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div class="span-24 last main">

<h2>Dashboard</h2>

<%int roleCheck= 0;%>
<c:forEach var="role" items="${roles}">
		<c:if test='${role.role=="JOB_ADMIN" || role.role=="SYSTEM_ADMIN"}'>
		<%roleCheck=2; %>
		</c:if>
		<%if(roleCheck==0){ %>
		<c:if test='${role.role=="SCHEDULE_ADMIN"}'>
		<%roleCheck=1; %>
		</c:if>
		<%}%>
</c:forEach>

<div class="frame-dashboard">
<h3 class="left">Jobs</h3>
<%if(roleCheck==2){ %>
<h4 class="right"><a href="<spring:url value="/job/create" htmlEscape="true" />">Create new job</a></h4>
<%} %>
<br class="clear">

<c:if test="${empty jobs}">
<p>No jobs found, use the link above to create a new one</p>
</c:if>

<table>
<c:forEach var="job" items="${jobs}">
		<tr>
		<div class="table-border">
		<div class="link left"><c:out value="${job.name}" /></div>
		
			<%if(roleCheck==2){ %>
				<div class="link right"><a href="">run poller</a>	<a href="<spring:url value="/job/${job.id}/edit" htmlEscape="true" />">edit</a></div>
				<br class="clear">
			<%}else if(roleCheck==1){ %>
				<div class="link right"><a href="">run poller</a>	<a href="<spring:url value="/job/${job.id}/edit/schedule" htmlEscape="true" />">edit schedule</a></div>
				<br class="clear">
			<%}%>

		<p class="reduce-bottom">
		Last run: 
		<br />
		Status:
		</p>
		<br/>
		<div class="link"><a href="">View detailed report</a>		<a href="">History</a></div>
		</div>
		</tr>
		
	</c:forEach>
</table>

</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
