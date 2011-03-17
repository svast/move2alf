<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div class="span-24 last main">

<h2>Destinations</h2>

<div class="frame-dashboard">
<h3 class="left">Destinations</h3>
<h4 class="right"><a href="<spring:url value="/destinations/create" htmlEscape="true" />">Create new Destinations</a></h4>
<br class="clear">

<c:if test="${empty destinations}">
<p>No destinations found, use the link above to create a new one</p>
</c:if>

<table class="table-jobAndDestination">
<c:forEach var="destination" items="${destinations}">
		<tr>
		<td class="table-border">

		<div class="link left"><c:out value="${destination.parameters.name}" /> </div>
		<div class="link right"> <a href="<spring:url value="/destination/${destination.id}/edit" htmlEscape="true" />">edit</a></div>
		<br class="clear">

		<p class="reduce-bottom">
		Type: <c:out value="${typeNames[destination.className]}" />
		<br />
		URL: <c:out value="${destination.parameters.url}" />
		<br />
		Username: <c:out value="${destination.parameters.user}" />
		<br />
		Password: <c:out value="${destination.parameters.password}" />
		<br />
		Threads: <c:out value="${destination.parameters.threads}" />
		
		</p>
		<br />
		</td>
		</tr>
		<tr class="spacer"><td></td></tr>
	</c:forEach>
</table>

</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
