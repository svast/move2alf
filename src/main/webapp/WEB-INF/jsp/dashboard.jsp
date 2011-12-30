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

<c:if test="${not licenseIsValid}">
<div class="warning"><p><strong>
    <c:choose>
        <c:when test="${'nolicense' eq licenseValidationFailureCause}">
        You need a license key to run jobs.
        </c:when>
        <c:otherwise>
        Your license key has expired. You will not be able to run new jobs.
        </c:otherwise>
    </c:choose>
</strong>Please contact <a href="mailto:sales@xenit.eu?subject=Move2Alf license&body=Dear XeniT,%0A%0AWe hereby request a production license key for the Move2Alf.%0A%0AOur company information:%0A Company name: <c:out value="${licensee.companyName}"/>%0A Street: <c:out value="${licensee.street}"/>%0A City: <c:out value="${licensee.city}"/>%0A Postal Code: <c:out value="${licensee.postalCode}"/>%0A State: <c:out value="${licensee.state}"/>%0A Country: <c:out value="${licensee.country}"/>%0A%0AContact person: <c:out value="${licensee.contactPerson}"/>%0A E-mail: <c:out value="${licensee.email}"/>%0A Telephone: <c:out value="${licensee.telephone}"/>%0A%0AMigration volume in number of documents:%0A - One time:%0A - Per year:">sales@xenit.eu</a> to obtain a license key.</p>
</div>
</c:if>

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

    <c:if test="${licenseIsValid}">
        <p class="info">
        This Move2Alf is licensed to <c:out value="${licensee.companyName}"/>. 
            <c:if test="${not empty expirationDate}">
            The license expires on <c:out value="${expirationDate}"/>.
            </c:if>
        </p>
    </c:if>
</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
