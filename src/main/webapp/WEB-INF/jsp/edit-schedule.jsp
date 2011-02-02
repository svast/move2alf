<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>


<body>
<div class="span-24 last main">

<h2>Edit schedule</h2>

<div class="frame-job">
<form:form modelAttribute="job" method="post" name="editSchedule">

<h3>
<table>
<tr>
<td>Name:</td>
<td><c:out value="${job.name}" /></td>
</tr>
</table>
</h3>
<br />
<%int coun
ter = 1; %>
<table id="tblSample" class="indent">
<c:forEach var="schedule" items="${schedules}">
<tr>
<td>
<div id="rowNumber<%=counter%>"><%=counter%></div>
</td>
<td>Cron job</td>
<td>
<div><c:out value="${schedule.quartzScheduling}" /></div>
</td>
<td>
<div class="pointer" id="remove<%=counter%>" onclick="removeRowFromSchedule(<%=counter%>)">remove</div>
</td>
</tr>
<%counter++; %>
</c:forEach>
</table>

<%@ include file="/WEB-INF/jsp/schedule.jsp"%>

<table id="tblCron" class="hide">
<c:forEach var="schedule" items="${schedules}">
<tr>
<td><input name="cron" type="checkbox" value="<c:out value="${schedule.quartzScheduling}" />" checked /></td>
</tr>
</c:forEach>
</table>

<br />

<a href="<spring:url value="/job/dashboard" htmlEscape="true" />" class="left"><button type="button">Cancel</button></a>
<input type="submit" value="Update schedule" class="right"/>
</form:form>
</div>	
</div>

</body>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
