<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>


<body onload="noDestNeeded();">
<div class="span-24 last main">

<h2>Create new destinations</h2>

<c:if test="${destinationExists==true}" >
<br />
<h4 class="error center">A destination with this name already exists</h4>
<br />
</c:if>
<c:if test="${doubleNewDestination==true}" >
<br />
<h4 class="error center">You may not create a destination with the same name more than once</h4>
<br />
</c:if>
<c:if test="${threadsIsInteger==false}" >
<br />
<h4 class="error center">The number of threads in the destination dialogue must contain numbers only</h4>
<br />
</c:if>

<div class="frame-job">
<form:form modelAttribute="destination" method="post" name="createDestinations" onSubmit="return createDestinationsValidation(this);">

<h4>Destination</h4>

<%@ include file="/WEB-INF/jsp/destination.jsp"%>

<br />

<button type="button" class="center" onclick="javascript:location.href ='<spring:url value="/destinations" htmlEscape="true" />';">Cancel</button>

<input id="proceed" type="submit" value="Create new destinations" class="right"/>

</form:form>
</div>	
</div>
</body>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
