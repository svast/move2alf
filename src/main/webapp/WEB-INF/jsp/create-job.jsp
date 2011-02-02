<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>


<body>
<div class="span-24 last main">

<h2>Create new job</h2>

<div class="frame-job">
<form:form modelAttribute="job" method="post" name="createJob">
<h4>General</h4>
<table class="indent">
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

<div class="indent">

<p id="noDestinations">No existing destinations found.</p>

<table id="tblDestination" class="indent">
</table>

<div id="addDestinationButton" class="link small indent" onclick="addDestination();"><span class="pointer">Create new destination</span></div>

<table id="destinationForm" class="hide indent">
<tr>
<td id="destName">Name:</td> 
<td><form:input path="destinationName" size="15" maxlength="30" /></td>
</tr>
<tr>
<td id="destType">Type:</td> 
<td><form:radiobutton path="destinationType" value="Alfresco" />Alfresco</td>
</tr>
<tr>
<td></td>
<td><form:radiobutton path="destinationType" value="CMIS" />CMIS</td>
</tr>
<tr>
<td id="destURL">URL:</td>
<td><form:input path="destinationURL" size="50" maxlength="40" /></td>
</tr>

<tr>
<td id="destUser" >Username:</td>
<td><form:input path="alfUser" size="30" maxlength="30" /></td>
</tr>

<tr>
<td id="destPswd">Password:</td>
<td><form:input path="alfPswd" size="30" maxlength="30" /></td>
</tr>

<tr>
<td id="threadNbr">Number of threads:</td>
<td><form:input path="nbrThreads" size="10" value="5" maxlength="2" onKeyPress="return numbersonly(this, event)"/></td>
</tr>

<tr>
<td onclick="cancelDestination();"><span class="pointer">Cancel</span></td>
<td onclick="confirmDestination();addRowToDestination();"><span class="pointer">Ok</span></td>
</tr>
</table>

<p>Path: <form:input path="InputFolder" size="50" maxlength="50" /></p>

</div>


<br />

<h4>Schedule</h4>

<table id="tblSample" class="indent">
</table>

<%@ include file="/WEB-INF/jsp/schedule.jsp"%>

<table id="tblCron" class="hide">
</table>

<br />

<h4>Metadata</h4>
<table class="indent">
<tr><td><form:radiobutton path="metadata" value="No metadata" />No metadata</td></tr>
<tr>
<td><form:radiobutton path="metadata" value="Read metadata from CSV file" />Read metadata from CSV file</td>
<td class="link small"><a href="" target="_blank">Configure</a></td>
</tr>
<tr><td><form:radiobutton path="metadata" value="Use dedicated parser" />Use dedicated parser</td></tr>
</table>
<br />

<h4>Transform</h4>
<table class="indent">
<tr><td><form:radiobutton path="transform" value="No transformation" />No transformation</td></tr>
<tr>
<td><form:radiobutton path="transform" value="Convert to PDF" />Convert to PDF</td>
<td class="link small"><a href="" target="_blank">Configure</a></td>
</tr>
<tr><td><form:radiobutton path="transform" value="Adept" />Adept</td></tr>
</table>
<br />

<h4>Options</h4>
<div class="indent">
<p>If document already exists in destination:</p>
<div><form:radiobutton path="docExist" value="SkipLog" />Skip document and log error</div>
<div><form:radiobutton path="docExist" value="Skip" />Skip document silently</div>
<div><form:radiobutton path="docExist" value="Overwrite" />Overwrite document</div>
<div><form:radiobutton path="docExist" value="Delete" />Delete</div>
<div><form:radiobutton path="docExist" value="Presence" />List presence</div>
</div>

<br />

<div class="indent"><form:checkbox path="moveBeforeProc" value="Move before processing to" />Move before processing to</div>
<table>
<tr>
<td class="double-indent">Path: <form:input path="beforeProcPath" size="50" maxlength="50" /></td>
<td></td>
</tr>
</table>
<br />

<div class="indent"><form:checkbox path="moveAfterLoad" value="Move loaded files to" />Move loaded files to</div>
<table >
<tr>
<td class="double-indent">Path: <form:input path="afterLoadPath" size="50" maxlength="50" /></td>
<td></td>
</tr>
</table>
<br />

<div class="indent"><form:checkbox path="moveNotLoad" value="Move not loaded files to" />Move not loaded files to</div>
<table>
<tr>
<td class="double-indent">Path: <form:input path="notLoadPath" size="50" maxlength="50" /></td>
<td></td>
</tr>
</table>
<br />

<div class="indent"><form:checkbox path="sendNotification" value="Send notification e-mails on errors" />Send notification e-mails on errors</div>
<div class="double-indent">To: <form:input path="emailAddressError" size="50" maxlength="50"/></div>
<div class="smaller double-indent">Separate multiple e-mail addresses with commas</div>
<br />

<div class="indent"><form:checkbox path="sendReport" value="Send load reports" />Send load reports</div>
<div class="double-indent">To: <form:input path="emailAddressRep" size="50" maxlength="50"/></div>
<div class="smaller double-indent">Separate multiple e-mail addresses with commas</div>
<br />

<a href="<spring:url value="/job/dashboard" htmlEscape="true" />" class="left"><button type="button">Cancel</button></a>
<input type="submit" value="Create new job" class="right"/>
</form:form>
</div>	
</div>
</body>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>