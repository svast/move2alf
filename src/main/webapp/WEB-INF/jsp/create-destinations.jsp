<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>


<body onload="noDestNeeded();">
<div class="span-24 last main">

<h2>Create new destinations</h2>

<div class="frame-job">
<form:form modelAttribute="destination" method="post" name="createDestinations" onSubmit="return formValidator(this, 'destination');">

<h4>Destination</h4>

<%@ include file="/WEB-INF/jsp/destination.jsp"%>

<br />

<a href="<spring:url value="/destinations" htmlEscape="true" />" class="left"><button type="button">Cancel</button></a>
<input id="proceed" type="submit" value="Create new destinations" class="right"/>
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
