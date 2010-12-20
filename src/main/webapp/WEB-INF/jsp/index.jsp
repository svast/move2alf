<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix='security'
	uri='http://www.springframework.org/security/tags'%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>XeniT Move2Alf</title>
</head>
<body>
<h1>XeniT Move2Alf</h1>
<p>Username: <security:authentication property="principal.username" /></p>
<p><a href="<spring:url value="/j_spring_security_logout" htmlEscape="true"/>">Logout</a></p>
</body>
</html>