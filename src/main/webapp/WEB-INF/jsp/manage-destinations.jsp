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

<table>
<c:forEach var="destination" items="${destinations}">
		<tr>
		<div class="table-border">
		<c:forEach var="destinationParams" items="${destination.configuredSourceSinkParameterSet}">
		<c:if test="${destinationParams.name=='name'}" >
		<div class="link left"><c:out value="${destinationParams.value}" /> </div>
		</c:if>
		</c:forEach>
		<div class="link right"> <a href="<spring:url value="/destination/${destination.id}/edit" htmlEscape="true" />">edit</a></div>
		<br class="clear">

		<p class="reduce-bottom">
		Type: <c:out value="${destination.sourceSinkClassName}" />
		<br />
		URL: 
		<c:forEach var="destinationParams" items="${destination.configuredSourceSinkParameterSet}">
		<c:if test="${destinationParams.name=='url'}" >
		<c:out value="${destinationParams.value}" /> 
		</c:if>
		</c:forEach>
		<br />
		Username: 
		<c:forEach var="destinationParams" items="${destination.configuredSourceSinkParameterSet}">
		<c:if test="${destinationParams.name=='user'}" >
		<c:out value="${destinationParams.value}" />
		</c:if>
		</c:forEach>
		<br />
		Password: 
		<c:forEach var="destinationParams" items="${destination.configuredSourceSinkParameterSet}">
		<c:if test="${destinationParams.name=='password'}" >
		<c:out value="${destinationParams.value}" />
		</c:if>
		</c:forEach>
		<br />
		Threads: 
		<c:forEach var="destinationParams" items="${destination.configuredSourceSinkParameterSet}">
		<c:if test="${destinationParams.name=='threads'}" >
		<c:out value="${destinationParams.value}" /> 
		</c:if>
		</c:forEach>
		
		</p>
		<br />
		</div>
		
		</tr>
		
	</c:forEach>
</table>

</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
