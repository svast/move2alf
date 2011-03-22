<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<body>
<div class="span-24 last main">

<h2>Edit job</h2>

<c:if test="${jobExists==true}" >
<br />
<h4 class="error center">A job with this name already exists</h4>
<br />
</c:if>
<c:if test="${destinationExists==true}" >
<br />
<h4 class="error center">A destination with this name already exists</h4>
<br />
</c:if>
<c:if test="${doubleNewDestination==true}" >
<br />
<h4 class="error center">You may not create a destination with the same name more than once</h4>
<br />
</c:if>
<c:if test="${threadsIsInteger==false}" >
<br />
<h4 class="error center">The number of threads in the destination dialogue must contain numbers only</h4>
<br />
</c:if>

<div class="frame-job">

<button type="button" class="right" onclick="javascript:location.href ='<spring:url value="/job/${job.id}/delete" htmlEscape="true" />';">Delete</button>


<form:form modelAttribute="job" method="post" name="editJob" onSubmit="return jobValidation(this);">
<h4>General</h4>
<table>
<tr>
<td>Name:</td>
<td><form:input path="name" size="30" maxlength="30" /></td>
<td id="nameError" class="hide error">name may not be empty.</td>
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
<td id="descriptionError" class="hide error">description may not be empty.</td>
<td><form:errors path="description" cssClass="error"/></td>
</tr>
</table>

<br />

<h4>Import from</h4>
<table>
<tr>
<td>Path: <form:input path="inputFolder" size="50" maxlength="50" /></td>
<td id="inputFolderError" class="hide error">input path may not be empty.</td>
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
<table>
<tr>
<td>Path: <form:input path="destinationFolder" size="50" maxlength="50" /></td>
<td id="destinationFolderError" class="hide error">destination path may not be empty.</td>
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

<%@ include file="/WEB-INF/jsp/job-options.jsp"%>

<button type="button" class="left" onclick="javascript:location.href ='<spring:url value="/job/dashboard" htmlEscape="true" />';">Cancel</button>

<input id="proceed" type="submit" value="Update job" class="right"/>

</form:form>
</div>	
</div>
</body>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
