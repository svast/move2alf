<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<div class="frame-job">
<br />
<br />
<br />
<br />
<h3 class="center">The old password you entered did not coincide <br />with the actual value for the old password</h3>

<a href="<spring:url value="/user/profile" htmlEscape="true" />" class="left"><button type="button">Cancel</button></a>
<a href="<spring:url value="/user/profile/${user.userName}/edit" htmlEscape="true" />" class="right"><button type="button">Try again</button></a>

</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
