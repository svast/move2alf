
<h4>Metadata</h4>
<table class="indent">
<c:forEach var="metadataOption" items="${metadataOptions}" >
<tr>
<%int metadataCounter=0; %>
<c:set var="jobMetadata" value="${job.metadata}" scope="session" />
<c:set var="metadataOption" value="${metadataOption.description}" scope="session" />
<%
String jobMetadata = (String) session.getAttribute("jobMetadata");
String metadataOption = (String) session.getAttribute("metadataOption");
if(metadataOption!=null){
if ((metadataCounter==0 && (jobMetadata == null || jobMetadata.equals(""))) || metadataOption.equals(jobMetadata)) { 
%>
<td><form:radiobutton path="metadata" value="${metadataOption.name}" checked="true" /><c:out value="${metadataOption.name}" /> - <c:out value="${metadataOption.description}" /></td>
<%}else{ %>
<td><form:radiobutton path="metadata" value="${metadataOption.name}" /><c:out value="${metadataOption.name}" /> - <c:out value="${metadataOption.description}" /></td>
<%} 
if(metadataCounter==0){%>
<td><form:errors path="metadata" cssClass="error"/></td>
<%} %>
</tr>
<%}
metadataCounter++; %>
</c:forEach>
</table>
<input type="hidden" value="" name="metadata" />
<br />

<h4>Transform</h4>
<table class="indent">
<%int transformCounter=0; %>
<c:set var="jobTransform" value="${job.transform}" scope="session" />
<c:set var="transformOption" value="${transformOption.description}" scope="session" />
<%
String jobTransform = (String) session.getAttribute("jobTransform");
String transformOption = (String) session.getAttribute("transformOption");

if(transformCounter==0 && (jobTransform == null || jobTransform.equals(""))) { 
%>
<tr>
<td><form:radiobutton path="transform" value="No transformation" checked="true"/>No transformation</td>
<td><form:errors path="transform" cssClass="error"/></td>
</tr>
<%}%>
<c:forEach var="transformOption" items="${transformOptions}" >
<%if(jobTransform != null && !jobTransform.equals("") && transformOption!=null && transformOption.equals(jobTransform)){%>
<tr>
<td><form:radiobutton path="transform" value="${transformOption.name}" checked="true"/><c:out value="${transformOption.description}" /></td>
</tr>
<%}else{ %>
<tr>
<td><form:radiobutton path="transform" value="${transformOption.name}" /><c:out value="${transformOption.description}" /></td>
</tr>
<%} %>
</c:forEach>
</table>
<input type="hidden" value="" name="transform" />
<br />

<h4>Options</h4>
<div class="indent">
<p>If document already exists in destination:</p>
<table class="indent">
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
<c:if test="${job.docExist=='Presence'}" >
<tr><td><form:radiobutton path="docExist" value="ListPresence" checked="true"/>List presence</td></tr>
</c:if>
<c:if test="${job.docExist!='Presence'}" >
<tr><td><form:radiobutton path="docExist" value="ListPresence" />List presence</td></tr>
</c:if>
</table>
</div>

<br />

<div class="indent"><form:checkbox path="moveBeforeProc" value="true" />Move before processing to</div>
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
</tr>
</table>
<br />

<div class="indent"><form:checkbox path="moveAfterLoad" value="true" />Move loaded files to</div>
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
</tr>
</table>
<br />

<div class="indent"><form:checkbox path="moveNotLoad" value="true" />Move not loaded files to</div>
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
</tr>
</table>
<br />

<div class="indent"><form:checkbox path="sendNotification" value="true" />Send notification e-mails on errors</div>
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

<div class="indent"><form:checkbox path="sendReport" value="true" />Send load reports</div>
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