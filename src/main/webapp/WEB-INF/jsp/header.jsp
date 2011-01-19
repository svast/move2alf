<!doctype html>
<html>
<head>
<script type="text/javascript">

function popup(mylink, windowname)
{
if (! window.focus)return true;
var href;
if (typeof(mylink) == 'string')
   href=mylink;
else
   href=mylink.href;
window.open(href, windowname, 'width=850,height=400,scrollbars=yes');
return false;
}

function showInput(){
	document.getElementById('destination').style.display='inline';
}
</script>
<meta charset="utf-8">
<link rel="stylesheet" href="/styles/blueprint/screen.css"
	type="text/css" media="screen, projection">
<link rel="stylesheet" href="/styles/blueprint/print.css"
	type="text/css" media="print">
<!--[if lt IE 8]>
  <link rel="stylesheet" href="/styles/blueprint/ie.css" type="text/css" media="screen, projection">
<![endif]-->
<link rel="stylesheet"
	href="<spring:url value="/styles/main.css" htmlEscape="true" />"
	type="text/css" />
<title>XeniT Move2Alf</title>
</head>
<body>
<div class="container">
<div class="span-24 last header">
<h1>XeniT Move2Alf</h1>
<sec:authorize access="hasRole('CONSUMER')">
	<p><a href="<spring:url value="/" htmlEscape="true"/>">Home</a> |
	Username: <sec:authentication property="principal.username" /> <sec:authorize
		access="hasRole('SYSTEM_ADMIN')">
| <a href="<spring:url value="/users/" htmlEscape="true"/>">Manage
		users</a>
	</sec:authorize> | <a
		href="<spring:url value="/j_spring_security_logout" htmlEscape="true"/>">Logout</a></p>
</sec:authorize></div>