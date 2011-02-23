<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="/WEB-INF/jsp/header.jsp" %>
<!-- 
<body onload='setDuration("<c:out value="${cycle.startDateTime.time}"/>","<c:out value="${cycle.endDateTime.time}"/>");'>
-->
<div class="span-24 last main">

<h2>Report</h2>
<br />

<div class="frame-job">
<h3><c:out value="${job.name}" /> - <fmt:formatDate value="${cycle.startDateTime}" pattern="yyyy-MM-dd HH:mm" type="both"/></h3>

<table>
<tr>
<th class="alignRight">Description:</th><td style="width: 200px;"><c:out value="${job.description}" /></td>
</tr>
<tr> <td><br /></td></tr>
<tr>
<th class="alignRight">Start time:</th><td><fmt:formatDate value="${cycle.startDateTime}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></td>
</tr>
<tr><td><br /> </td></tr>
<tr>
<th class="alignRight">End time:</th><td><fmt:formatDate value="${cycle.endDateTime}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></td>
</tr>
<tr><td><br /> </td></tr>
<tr>
<th class="alignRight">Duration:</th><td id="duration"><c:out value="${duration}" /></td>
</tr>
<tr><td><br /> </td></tr>
<tr>
<th class="alignRight">Status:</th><td><c:out value="${cycle.schedule.state.displayName}" /></td>
<th>Nr. of documents:</th><td><c:out value="" /></td>
</tr>
<tr><td><br /> </td></tr>
<tr>
<th class="alignRight">Docs / s:</th><td><c:out value="" /></td>
</tr>
</table>

<br />

<div class="indent"><b>List of imported documents </b></div>

<button type="button" class="right">export</button>


</div>

</div>
<!-- 
</body>
-->

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
