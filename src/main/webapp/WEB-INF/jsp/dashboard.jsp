<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div class="span-24 last main">

<h2>Dashboard</h2>

<div style="padding-left:25%; padding-right:25%">
<h3 style="float:left;">Jobs</h3>
<h4 style="float:right;"><a href="<spring:url value="/job/create" htmlEscape="true" />">Create new job</a></h4>
<br style="clear:both">

<p>No jobs found, use the link above to create a new one</p>

<table>
<tr>
<div style="border-style:solid;border-width:3px;padding:3px;">
<div style="float:left;font-weight:bold;">Name</div>
<div style="float:right;font-weight:bold;"><a href="">run poller</a>	<a href="<spring:url value="/job/${job.id}/edit" htmlEscape="true" />">edit</a></div>
<br style="clear:both">
<p style="margin-bottom:-5px">
Last run: 
<br />
Status:
</p>
<br/>
<div style="font-weight:bold;"><a href="">View detailed report</a>		<a href="">History</a></div>
</div>
</tr>
</table>

</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
