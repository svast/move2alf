
<!doctype html>
<html>
<head>

	 	<script type="text/javascript" src="<c:url value="/resources/dojo/dojo.js" />"></script> 
         <script type="text/javascript" src="<c:url value="/resources/spring/Spring.js" />"></script> 
         <script type="text/javascript" src="<c:url value="/resources/spring/Spring-Dojo.js" />"></script> 
          <link type="text/css" rel="stylesheet" href="<c:url value="/resources/dijit/themes/tundra/tundra.css" />" /> 

		<script type="text/javascript" src="<c:url value="/js/validation.js" />"> </script>
		<script type="text/javascript" src="<c:url value="/js/scheduleHandler.js" />"> </script>
		<script type="text/javascript" src="<c:url value="/js/destinationHandler.js" />"> </script>
		<script type="text/javascript" src="<c:url value="/js/parameterHandler.js" />"> </script>
		<script type="text/javascript" src="<c:url value="/js/inputPathHandler.js" />"> </script>
		<script type="text/javascript">
		
		function putFocus(formInst, elementInst) {
		    if (document.forms.length > 0) {
		        try{
		      		document.forms[formInst].elements[elementInst].focus();
		        }catch(e){
		        }
		    }
		}
		
		</script>

<meta charset="utf-8">
<!-- <link rel="stylesheet" href="/styles/blueprint/screen.css"
	type="text/css" media="screen, projection">
<link rel="stylesheet" href="/styles/blueprint/print.css"
	type="text/css" media="print"> -->
<!--[if lt IE 8]>
  <link rel="stylesheet" href="/styles/blueprint/ie.css" type="text/css" media="screen, projection">
<![endif]-->
<link rel="stylesheet"
	href="<spring:url value="/styles/main.css" htmlEscape="true" />"
	type="text/css" />
<title>XeniT Move2Alf</title>
</head>
<body onLoad="putFocus(0,0);" class="tundra">
<div class="container">
<div class="span-24 last header">
<img src="<spring:url value="/images/move2alf-logo.png" htmlEscape="true" />" alt="Move2Alf" width=""/>

<%//This determines the role of the user
String roleCheck= "";%>
<c:forEach var="role" items="${roles}">
		<c:if test='${role.role=="SYSTEM_ADMIN"}'>
		<%roleCheck="systemAdmin"; %>
		</c:if>
		<%if(roleCheck=="consumer" ||roleCheck=="scheduleAdmin" || roleCheck==""){ %>
		<c:if test='${role.role=="JOB_ADMIN"}'>
		<%roleCheck="jobAdmin"; %>
		</c:if>
		<%}if(roleCheck=="consumer" || roleCheck==""){ %>
		<c:if test='${role.role=="SCHEDULE_ADMIN"}'>
		<%roleCheck="scheduleAdmin"; %>
		</c:if>
		<%}if(roleCheck==""){ %>
		<c:if test='${role.role=="CONSUMER"}'>
		<%roleCheck="consumer"; %>
		</c:if>
		<%}%>
</c:forEach>

	<p>
	<div>

	<div class="left">
<%if(roleCheck=="systemAdmin" || roleCheck=="jobAdmin" || roleCheck=="scheduleAdmin" || roleCheck=="consumer"){ %>
<a href="<spring:url value="/" htmlEscape="true"/>">Home</a> 
<%if(roleCheck=="systemAdmin" || roleCheck=="jobAdmin"){ %>
| <a href="<spring:url value="/destinations/" htmlEscape="true"/>">Manage destinations</a>
<%}if(roleCheck=="systemAdmin"){ %>
| <a href="<spring:url value="/users/" htmlEscape="true"/>">Manage users</a>
<%} %>
| 	<a href="<spring:url value="/user/profile/" htmlEscape="true"/>">My profile</a>

</div>

<div class="right">
Username: <sec:authentication property="principal.username" /> 

| <a href="<spring:url value="/j_spring_security_logout" htmlEscape="true"/>">Logout</a>
<%} %>
</div>
<div class="clear"></div>

</div></p>

</div>