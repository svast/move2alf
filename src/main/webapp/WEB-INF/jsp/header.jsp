<!doctype html>
<html>
<head>
<meta charset="utf-8">
<link rel="stylesheet"
	href="<spring:url value="/styles/main.css" htmlEscape="true" />"
	type="text/css" />
<title>XeniT Move2Alf</title>
</head>
<body>
<div class="header">
<h1>XeniT Move2Alf</h1>
<p><a href="<spring:url value="/" htmlEscape="true"/>">Home</a>
| Username: <security:authentication property="principal.username" />
<sec:authorize access="hasRole('SYSTEM_ADMIN')">
| <a href="<spring:url value="/users/" htmlEscape="true"/>">Manage users</a>
</sec:authorize>
| <a
	href="<spring:url value="/j_spring_security_logout" htmlEscape="true"/>">Logout</a></p>
</div>

<div class="main">