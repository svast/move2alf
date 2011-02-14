<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>


<body onload="noDestNeeded();">
<div class="span-24 last main">

<h2>Create new destinations</h2>

<div class="frame-job">
<form:form modelAttribute="job" method="post" name="createDestinations">

<h4>Destination</h4>

<%@ include file="/WEB-INF/jsp/destination.jsp"%>

<br />

<a href="<spring:url value="/destinations" htmlEscape="true" />" class="left"><button type="button">Cancel</button></a>
<input type="submit" value="Create new destinations" class="right"/>
</form:form>
</div>	
</div>
</body>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
