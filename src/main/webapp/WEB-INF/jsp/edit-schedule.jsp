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
<td><c:out value="${jobConfig.name}" /></td>
</tr>
</table>
</h3>
<br />


<%@ include file="/WEB-INF/jsp/schedule.jsp"%>

<br />

<a href="<spring:url value="/job/dashboard" htmlEscape="true" />" class="left"><button type="button">Cancel</button></a>
<input id="proceed" type="submit" value="Update schedule" class="right"/>
						<script type="text/javascript">
                            Spring.addDecoration(new Spring.ValidateAllDecoration({
                                    elementId: "proceed",
                                    event: "onclick" }));
                        </script>
</form:form>
</div>	
</div>

</body>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
