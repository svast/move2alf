<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<body>
<div class="span-24 last main">

<h2>Edit job</h2>

<div class="frame-job">

<button type="button" class="right" onclick="javascript:location.href ='<spring:url value="/job/${job.id}/delete" htmlEscape="true" />';">Delete</button>


<form:form modelAttribute="job" method="post" name="editJob" onSubmit="return formValidator(this);">
<h4>General</h4>
<table class="indent">
<tr>
<td>Name:</td>
<td><form:input path="name" size="30" maxlength="30" /></td>
<td><form:errors path="name" cssClass="error"/></td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "name",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {
                                                required : true,
                                               	invalidMessage : "name cannot be empty",
                                               	promptMessage : "name cannot be empty"
                                                   	
                                        }
                                }));
                        </script>
</tr>
<tr>
<td>Description:</td>
<td><form:textarea path="description" cols="50" rows="4"/></td>
<td><form:errors path="description" cssClass="error"/></td>
</tr>
</table>

<br />

<h4>Import from</h4>
<table class="indent">
<tr>
<td>Path: <form:input path="inputFolder" size="50" maxlength="50" /></td>
<td><form:errors path="inputFolder" cssClass="error"/></td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "inputFolder",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {
                                                required : true,
                                               	invalidMessage : "Path cannot be empty",
                                               	promptMessage : "Path cannot be empty"
                                                   	
                                        }
                                }));
                        </script>
</tr>
</table>

<br />
<h4>Destination</h4>

<%@ include file="/WEB-INF/jsp/destination.jsp"%>
<table class="indent">
<tr>
<td class="indent">Path: <form:input path="destinationFolder" size="50" maxlength="50" /></td>
<td><form:errors path="destinationFolder" cssClass="error"/></td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "destinationFolder",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {
                                                required : true,
                                               	invalidMessage : "Path cannot be empty",
                                               	promptMessage : "Path cannot be empty"
                                                   	
                                        }
                                }));
                        </script>
</tr>
</table>
<br />

<h4>Schedule</h4>

<%@ include file="/WEB-INF/jsp/schedule.jsp"%>

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
					<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "beforeProcPath",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {                                                   
                                        }
                                }));
                        </script>
<td></td>
</tr>
</table>
<br />

<div class="indent"><form:checkbox path="moveAfterLoad" value="Move loaded files to" />Move loaded files to</div>
<table >
<tr>
<td class="double-indent">Path: <form:input path="afterLoadPath" size="50" maxlength="50" /></td>
					<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "afterLoadPath",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {                                                   
                                        }
                                }));
                        </script>
<td></td>
</tr>
</table>
<br />

<div class="indent"><form:checkbox path="moveNotLoad" value="Move not loaded files to" />Move not loaded files to</div>
<table>
<tr>
<td class="double-indent">Path: <form:input path="notLoadPath" size="50" maxlength="50" /></td>
					<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "notLoadPath",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {                                                   
                                        }
                                }));
                        </script>
<td></td>
</tr>
</table>
<br />

<div class="indent"><form:checkbox path="sendNotification" value="Send notification e-mails on errors" />Send notification e-mails on errors</div>
<div class="double-indent">To: <form:input path="emailAddressError" size="50" maxlength="50"/></div>
<div class="smaller double-indent">Separate multiple e-mail addresses with commas</div>
					<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "emailAddressError",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {                                                   
                                        }
                                }));
                        </script>
<br />

<div class="indent"><form:checkbox path="sendReport" value="Send load reports" />Send load reports</div>
<div class="double-indent">To: <form:input path="emailAddressRep" size="50" maxlength="50"/></div>
<div class="smaller double-indent">Separate multiple e-mail addresses with commas</div>
					<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "emailAddressRep",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {                                                   
                                        }
                                }));
                        </script>
<br />

<button type="button" class="left" onclick="javascript:location.href ='<spring:url value="/job/dashboard" htmlEscape="true" />';">Cancel</button>

<input id="proceed" type="submit" value="Update job" class="right"/>
						<script type="text/javascript">
                            Spring.addDecoration(new Spring.ValidateAllDecoration({
                                    elementId: "proceed",
                                    event: "onclick" }));
                        </script>
</form:form>
</div>	
</div>
</body>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
