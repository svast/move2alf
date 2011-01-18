<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/dialog-header.jsp"%>

<body>
<div class="span-24 last main">

<h2>Add schedule</h2>

<form:form modelAttribute="job" method="post">

<table>
<tr>
<td><form:radiobutton path="runFrequency" value="Single run at" />Single run at</td>
<td>date: <form:input path="runDate" size="15" maxlength="15"/></td>
<td>time: <form:input path="runTime" size="15" maxlength="15"/></td>
</tr>
<tr><td><form:radiobutton path="runFrequency" value="Hourly" />Hourly</td></tr>
<tr>
<td><form:radiobutton path="runFrequency" value="Daily" />Daily</td>
<td>time: <form:input path="runTime" size="15" maxlength="15"/></td>
</tr>
<tr><td><form:radiobutton path="runFrequency" value="Weekly" />Weekly</td></tr>
<tr><td><form:radiobutton path="runFrequency" value="Advanced" />Advanced</td></tr>
</table>
<br />
<div style="float:left"><a href="JavaScript:window.close()">Cancel</a></div>
<div style="float:right"><input type="submit" value="Ok"/></div>
<br style="clear:both">
</form:form>
	
</div>
</body>

