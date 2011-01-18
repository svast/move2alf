<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2>Delete Job</h2>

Are you sure you want to delete "<c:out value="${job.name}"/>"?

<form:form modelAttribute="job" method="post">
<p><input type="submit" value="Delete Job"/></p>
</form:form>
	
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
