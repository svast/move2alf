<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<div class="frame-job">
<br />
<br />
<br />
<br />
<h3 class="center">You did not enter your password correctly</h3>

<a href="<spring:url value="/users" htmlEscape="true" />" class="left"><button type="button">Cancel</button></a>
<a href="<spring:url value="/user/${user.userName}/edit/password" htmlEscape="true" />" class="right"><button type="button">Try again</button></a>

</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
