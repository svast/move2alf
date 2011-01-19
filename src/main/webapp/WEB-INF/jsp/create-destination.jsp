<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/dialog-header.jsp"%>

<body>
<div class="span-24 last main">

<h2>Create new job</h2>

<form:form modelAttribute="job" method="post">

<p>Create new destination</p>
<table>
<tr>
<td>Name:</td>
<td><form:input path="destinationName" size="30" maxlength="30" /></td>
</tr>

<tr>
<td>Type:</td>
<td><form:radiobutton path="destinationType" value="Alfresco" />Alfresco</td>
</tr>
<tr>
<td></td>
<td><form:radiobutton path="destinationType" value="CMIS" />CMIS</td>
</tr>

<tr>
<td>URL:</td>
<td><form:input path="destinationURL" size="50" maxlength="40" /></td>
</tr>

<tr>
<td>Username:</td>
<td><form:input path="alfUser" size="30" maxlength="30" /></td>
</tr>

<tr>
<td>Password:</td>
<td><form:input path="alfPswd" size="30" maxlength="30" /></td>
</tr>

<tr>
<td>Number of threads:</td>
<td><form:input path="nbrThreads" size="10" value="5" maxlength="2" onKeyPress="return numbersonly(this, event)"/></td>
</tr>

</table>
<br />

<div class="left"><a href="JavaScript:window.close()">Cancel</a></div>
<div class="right"><button type="button">Validate</button>	<input type="submit" value="Ok"/></div>
<br class="clear">
</form:form>
	
</div>
</body>

