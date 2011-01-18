<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<head>
<script type="text/javascript">

function showInput(){
	document.getElementById('destination').style.display='inline';
}

function popup(mylink, windowname)
{
if (! window.focus)return true;
var href;
if (typeof(mylink) == 'string')
   href=mylink;
else
   href=mylink.href;
window.open(href, windowname, 'width=800,height=400,scrollbars=yes');
return false;
}
</script>
</head>
<body>
<div class="span-24 last main">

<h2>Create new job</h2>

<div style="padding-left:15%; padding-right:15%">
<form:form modelAttribute="job" method="post">
<h4>General</h4>
<table>
<tr>
<td>Name:</td>
<td><form:input path="name" size="30" maxlength="30" /></td>
</tr>
<tr>
<td>Description:</td>
<td><form:textarea path="description" cols="50" rows="4"/></td>
</tr>
</table>
<br />

<h4>Import from</h4>
<table>
<tr>
<td>Path: <form:input path="InputFolder" size="50" maxlength="50" /></td>
<td></td>
</tr>
</table>

<br />

<h4>Destination</h4>
<p>No existing destinations found.</p>
<table>
<tr>
<td style="font-weight:bold; font-size:90%;" onclick="showInput();"><a href="<spring:url value="/job/create/destination" htmlEscape="true" />" onclick="return popup(this, 'notes')">Create new destination</a></td>
<td></td>
</tr>
<tr>
<td id="destination" style="display:none">Path: <form:input path="destinationFolder" size="50" maxlength="50" /></td>
</tr>
</table>

<br />

<h4>Schedule</h4>
<p style="font-weight:bold;font-size:90%;"><a href="<spring:url value="/job/add/schedule" htmlEscape="true" />" onclick="return popup(this, 'notes')">Add schedule</a></p>

<br />

<h4>Metadata</h4>
<table>
<tr><td><form:radiobutton path="metadata" value="No metadata" />No metadata</td></tr>
<tr>
<td><form:radiobutton path="metadata" value="Read metadata from CSV file" />Read metadata from CSV file</td>
<td style="font-weight:bold;font-size:90%;"><a href="" target="_blank">Configure</a></td>
</tr>
<tr><td><form:radiobutton path="metadata" value="Use dedicated parser" />Use dedicated parser</td></tr>
</table>
<br />

<h3>Transform</h3>
<table>
<tr><td><form:radiobutton path="transform" value="No transformation" />No transformation</td></tr>
<tr>
<td><form:radiobutton path="transform" value="Convert to PDF" />Convert to PDF</td>
<td style="font-weight:bold;font-size:90%;"><a href="" target="_blank">Configure</a></td>
</tr>
<tr><td><form:radiobutton path="transform" value="Adept" />Adept</td></tr>
</table>
<br />

<h3>Options</h3>
<p>If document already exists in destination:</p>
<div><form:radiobutton path="docExist" value="SkipLog" />Skip document and log error</div>
<div><form:radiobutton path="docExist" value="Skip" />Skip document silently</div>
<div><form:radiobutton path="docExist" value="Overwrite" />Overwrite document</div>
<div><form:radiobutton path="docExist" value="Delete" />Delete</div>
<div><form:radiobutton path="docExist" value="Presence" />List presence</div>

<br />

<div><form:checkbox path="moveBeforeProc" value="Move before processing to" />Move before processing to</div>
<table>
<tr>
<td>Path: <form:input path="beforeProcPath" size="50" maxlength="50" /></td>
<td></td>
</tr>
</table>
<br />

<div><form:checkbox path="moveAfterLoad" value="Move loaded files to" />Move loaded files to</div>
<table>
<tr>
<td>Path: <form:input path="afterLoadPath" size="50" maxlength="50" /></td>
<td></td>
</tr>
</table>
<br />

<div><form:checkbox path="moveNotLoad" value="Move not loaded files to" />Move not loaded files to</div>
<table>
<tr>
<td>Path: <form:input path="notLoadPath" size="50" maxlength="50" /></td>
<td></td>
</tr>
</table>
<br />

<div><form:checkbox path="sendNotification" value="Send notification e-mails on errors" />Send notification e-mails on errors</div>
<div>To: <form:input path="emailAddressError" size="50" maxlength="50"/></div>
<div style="font-size:75%">Separate multiple e-mail addresses with commas</div>
<br />

<div><form:checkbox path="sendReport" value="Send load reports" />Send load reports</div>
<div>To: <form:input path="emailAddressRep" size="50" maxlength="50"/></div>
<div style="font-size:75%">Separate multiple e-mail addresses with commas</div>
<br />

<a href="<spring:url value="/job/dashboard" htmlEscape="true" />" style="float:left"><button type="button">Cancel</button></a>
<input type="submit" value="Create new job" style="float:right"/>
</form:form>
</div>	
</div>
</body>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
