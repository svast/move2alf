<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<body>
<div class="span-24 last main">

<h2>Edit job</h2>

<div class="frame-job">

<a href="<spring:url value="/job/${job.id}/delete" htmlEscape="true" />" class="right"><button type="button">Delete</button></a>


<form:form modelAttribute="job" method="post">
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
<table class="indent">
<tr>
<td>Path: <form:input path="InputFolder" size="50" maxlength="50" /></td>
<td></td>
</tr>
</table>

<br />

<h4>Destination</h4>
<div class="indent">
<p>No existing destinations found.</p>
<table>
<tr>
<td class="link small" onclick="showInput();"><a href="<spring:url value="/job/create/destination" htmlEscape="true" />" onclick="return popup(this, 'notes')">Create new destination</a></td>
<td></td>
</tr>
<tr>
<td>Path: <form:input path="destinationFolder" size="50" maxlength="50" /></td>
</tr>
</table>
</div>

<br />

<h4>Schedule</h4>

<table id="tblSample">
</table>

<div id="addScheduleButton" class="link small indent" onclick="addSchedule();"><span class="pointer">Add Schedule</span></div>
<div id="scheduleForm" class="hide">
<table class="indent">
<tr>
<td><form:radiobutton path="runFrequency" value="Single run at" onclick="scheduleBox(0)"/>Single run at</td>
<td id="sDate" class="hide">date: <form:input path="singleDate" size="8" maxlength="10" value="1/1/2011"/></td>
<td id="sTime" class="hide">time: <form:input path="singleTime" size="5" maxlength="5" value="00:00" /></td>
</tr>
<tr>
<td><form:radiobutton path="runFrequency" value="Hourly" onclick="scheduleBox(1)"/>Hourly</td>
<td id="hourly" class="hide">minutes: <form:input path="hourTime" size="2" maxlength="2" value="00"/></td>
</tr>
<tr>
<td><form:radiobutton path="runFrequency" value="Daily" onclick="scheduleBox(2)"/>Daily</td>
<td id="daily" class="hide">time: <form:input path="dayTime" size="5" maxlength="5" value="00:00"/></td>
</tr>
<tr>
<td><form:radiobutton path="runFrequency" value="Weekly" onclick="scheduleBox(3)"/>Weekly</td>
<td id="weeklyDay" class="hide">day: <form:select path="weekDay">
			<form:option value="Monday" label="Monday"/>
			<form:option value="Tuesday" label="Tuesday"/>
			<form:option value="Wednesday" label="Wednesday"/>
			<form:option value="Thursday" label="Thursday"/>
			<form:option value="Friday" label="Friday"/>
			<form:option value="Saturday" label="Saturday"/>
			<form:option value="Sunday" label="Sunday"/>
			</form:select>
</td>
<td id="weeklyTime" class="hide">time: <form:input path="weekTime" size="5" maxlength="15" value="00:00"/></td>
</tr>
<tr>
<td><form:radiobutton path="runFrequency" value="Advanced" onclick="scheduleBox(4)"/>Advanced</td>
<td id="advanced" class="hide">cronjob: <form:input path="cronJob" size="15" maxlength="15" value="0 0 * * * ?"/></td>
</tr>
<tr>
<td onclick="cancelSchedule();"><span class="pointer">Cancel</span></td>
<td onclick="confirmSchedule();addRowToSchedule();"><span class="pointer">Ok</span></td>
</tr>
</table>
</div>

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
<input type="submit" value="Update job" class="right"/>
</form:form>
</div>	
</div>
</body>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
