<fieldset>
<h4>Metadata</h4>
<table>
<%int metadataCounter=0; %>
<c:forEach var="metadataOption" items="${metadataOptions}" >
<tr>
<c:set var="jobMetadata" value="${job.metadata}" scope="session" />
<c:set var="metadataOption" value="${metadataOption.class.name}" scope="session" />
<%
String jobMetadata = (String) session.getAttribute("jobMetadata");
String metadataOption = (String) session.getAttribute("metadataOption");
if(metadataOption!=null){
if ((metadataCounter==0 && (jobMetadata == null || jobMetadata.equals(""))) || metadataOption.equals(jobMetadata)) { 
%>
<td><form:radiobutton path="metadata" value="${metadataOption.class.name}" checked="true" /><c:out value="${metadataOption.name}" /> - <c:out value="${metadataOption.description}" /></td>
<%}else{ %>
<td><form:radiobutton path="metadata" value="${metadataOption.class.name}" /><c:out value="${metadataOption.name}" /> - <c:out value="${metadataOption.description}" /></td>
<%}
if(metadataCounter==0){%>
<td><form:errors path="metadata" cssClass="error"/></td>
<%} %>
</tr>
<%}
metadataCounter++; %>
</c:forEach>
</table>
<input type="radio" class="hide" value="" name="metadata" />

<br />

<table id="paramMetadataTable">

</table>

<div id="addParameterMetadataButton" class="link small" onclick="addParameterMetadata();"><span class="pointer">Add metadata parameter</span></div>
<table id="parameterMetadataForm" class="hide">
<tr>
<td>Name: <form:input path="parameterMetadataName" size="30" maxlength="30"  /></td>
<td>Value: <form:input path="parameterMetadataValue" size="30" maxlength="30"  /></td>
</tr>
<tr>
<td><button type="button" class="button" onclick="cancelParameterMetadata();">Cancel</button></td>
<td><input name="cancelButton" type="button" class="button" value="Ok" onclick="confirmParameterMetadata();addRowToParameterMetadata(this.form);" /></td>

</tr>
</table>

 <table id="tblParamMetadata" class="hide">

</table>

<br />

<h4>Transform</h4>
<table>
<%int transformCounter=0; %>
<c:set var="jobTransform" value="${job.transform}" scope="session" />
<c:set var="transformOption" value="${transformOption.class.name}" scope="session" />
<%
String jobTransform = (String) session.getAttribute("jobTransform");
String transformOption = (String) session.getAttribute("transformOption");

if((transformCounter==0 && (jobTransform == null || jobTransform.equals(""))) || "No transformation".equals(jobTransform)) { 
%>
<tr>
<td><form:radiobutton path="transform" value="No transformation" checked="true"/>No transformation</td>
<td><form:errors path="transform" cssClass="error"/></td>
</tr>
<%} else{%>
<tr>
<td><form:radiobutton path="transform" value="No transformation" />No transformation</td>
<td><form:errors path="transform" cssClass="error"/></td>
</tr>
<%}%>
<c:forEach var="transformOption" items="${transformOptions}" >
<%if(jobTransform != null && !jobTransform.equals("") && transformOption!=null && transformOption.equals(jobTransform)){%>
<tr>
<td><form:radiobutton path="transform" value="${transformOption.class.name}" checked="true"/><c:out value="${transformOption.description}" /></td>
</tr>
<%}else{ %>
<tr>
<td><form:radiobutton path="transform" value="${transformOption.class.name}" /><c:out value="${transformOption.description}" /></td>
</tr>
<%} %>
</c:forEach>
</table>
<input type="radio" class="hide" value="" name="transform" />

<br />

<table id="paramTransformTable">

</table>

<div id="addParameterTransformButton" class="link small" onclick="addParameterTransform();"><span class="pointer">Add transform parameter</span></div>
<table id="parameterTransformForm" class="hide">
<tr>
<td>Name: <form:input path="parameterTransformName" size="30" maxlength="30"  /></td>
<td>Value: <form:input path="parameterTransformValue" size="30" maxlength="30"  /></td>
</tr>
<tr>
<td><button type="button" class="button" onclick="cancelParameterTransform();">Cancel</button></td>
<td><input name="cancelButton" type="button" class="button" value="Ok" onclick="confirmParameterTransform();addRowToParameterTransform(this.form);" /></td>

</tr>
</table>

 <table id="tblParamTransform" class="hide">

</table>

</fieldset>
<br />


<fieldset>
<h4>Options</h4>
<div class="indent">
<p>If document already exists in destination:</p>
<table>
<%int optionsCounter=0; %>
<c:set var="jobOption" value="${job.docExist}" scope="session" />

<tr>
<c:set var="emptyList" value="false" />
<c:if test="${job.docExist=='SkipAndLog' || empty job.docExist}" >
<td><form:radiobutton path="docExist" value="SkipAndLog" checked="true"/>Skip document and log error</td>
<c:set var="emptyList" value="true" />
</c:if>
<c:if test="${job.docExist!='SkipAndLog' && emptyList==false}" >
<td><form:radiobutton path="docExist" value="SkipAndLog" />Skip document and log error</td>
</c:if>
<td><form:errors path="docExist" cssClass="error"/></td>
</tr>
<c:if test="${job.docExist=='Skip'}" >
<tr><td><form:radiobutton path="docExist" value="Skip" checked="true"/>Skip document silently</td></tr>
</c:if>
<c:if test="${job.docExist!='Skip'}" >
<tr><td><form:radiobutton path="docExist" value="Skip" />Skip document silently</td></tr>
</c:if>
<c:if test="${job.docExist=='Overwrite'}" >
<tr><td><form:radiobutton path="docExist" value="Overwrite" checked="true"/>Overwrite document</td></tr>
</c:if>
<c:if test="${job.docExist!='Overwrite'}" >
<tr><td><form:radiobutton path="docExist" value="Overwrite" />Overwrite document</td></tr>
</c:if>
<c:if test="${job.docExist=='Delete'}" >
<tr><td><form:radiobutton path="docExist" value="Delete" checked="true"/>Delete</td></tr>
</c:if>
<c:if test="${job.docExist!='Delete'}" >
<tr><td><form:radiobutton path="docExist" value="Delete" />Delete</td></tr>
</c:if>
<c:if test="${job.docExist=='ListPresence'}" >
<tr><td><form:radiobutton path="docExist" value="ListPresence" checked="true"/>List presence</td></tr>
</c:if>
<c:if test="${job.docExist!='ListPresence'}" >
<tr><td><form:radiobutton path="docExist" value="ListPresence" />List presence</td></tr>
</c:if>
</table>
</div>

</fieldset>

<br />

<fieldset>
<br />
<div><form:checkbox path="moveBeforeProc" value="true" />Move before processing to</div>
<table>
<tr>
<td>Path: <form:input path="beforeProcPath" size="50" maxlength="50" /></td>

					<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "beforeProcPath",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {                                                   
                                        }
                                }));
                        </script>
</tr>
</table>
<br />

<div><form:checkbox path="moveAfterLoad" value="true" />Move loaded files to</div>
<table >
<tr>
<td>Path: <form:input path="afterLoadPath" size="50" maxlength="50" /></td>
					<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "afterLoadPath",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {                                                   
                                        }
                                }));
                        </script>
</tr>
</table>
<br />

<div><form:checkbox path="moveNotLoad" value="true" />Move not loaded files to</div>
<table>
<tr>
<td>Path: <form:input path="notLoadPath" size="50" maxlength="50" /></td>
					<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "notLoadPath",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {                                                   
                                        }
                                }));
                        </script>
</tr>
</table>
</fieldset>
<br />

<fieldset>
<br />
<div><form:checkbox path="sendNotification" value="true" />Send notification e-mails on errors</div>
<div>To: <form:input path="emailAddressError" size="50" maxlength="50"/></div>
<div class="smaller">Separate multiple e-mail addresses with commas</div>
					<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "emailAddressError",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {                                                   
                                        }
                                }));
                        </script>
<br />

<div><form:checkbox path="sendReport" value="true" />Send load reports</div>
<div>To: <form:input path="emailAddressRep" size="50" maxlength="50"/></div>
<div class="smaller">Separate multiple e-mail addresses with commas</div>
					<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "emailAddressRep",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {                                                   
                                        }
                                }));
                        </script>
</fieldset>
<br />