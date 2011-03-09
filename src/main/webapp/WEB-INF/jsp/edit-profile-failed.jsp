<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<div class="frame-job">
<br />
<br />
<br />
<br />
<h3 class="center">The old password you entered did not coincide <br />with the actual value for the old password</h3>

<button type="button" class="left" onclick="javascript:location.href ='<spring:url value="/user/profile" htmlEscape="true" />';">Cancel</button>
<button type="button" class="right" onclick="javascript:location.href ='<spring:url value="/user/profile/${user.userName}/edit" htmlEscape="true" />';">Try again</button>

</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
