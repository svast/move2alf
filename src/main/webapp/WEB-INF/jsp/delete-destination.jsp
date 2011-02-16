<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2>Delete Destination</h2>

<c:forEach var="destinationParams" items="${destination.configuredSourceSinkParameterSet}">
<c:if test="${destinationParams.name=='name'}" >
Are you sure you want to delete destination "<c:out value="${destinationParams.value}" />"?
</c:if>
</c:forEach>

<form:form modelAttribute="job" method="post">
<p><input type="submit" value="Delete Destination"/></p>
</form:form>
	
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>