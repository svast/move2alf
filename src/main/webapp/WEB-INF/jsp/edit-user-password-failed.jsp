<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<div class="frame-job">
<br />
<br />
<br />
<br />
<h3 class="center">You did not enter your password correctly</h3>

<button type="button" class="left" onclick="javascript:location.href ='<spring:url value="/users" htmlEscape="true" />';">Cancel</button>
<button type="button" class="right" onclick="javascript:location.href ='<spring:url value="/user/${user.userName}/edit/password" htmlEscape="true" />';">Try again</button>

</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
