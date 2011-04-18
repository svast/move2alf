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
<c:if test="${doubleMetadata==true}" >
<br />
<h4 class="error center">You may not create more than one metadata parameter with the same name</h4>
<br />
</c:if>
<c:if test="${doubleTransform==true}" >
<br />
<h4 class="error center">You may not create more than one transform parameter with the same name</h4>
<br />
</c:if>
<c:if test="${threadsIsInteger==false}" >
<br />
<h4 class="error center">The number of threads in the destination dialogue must contain numbers only</h4>
<br />
</c:if>
<c:if test="${doubleInputFolder==true}" >
<br />
<h4 class="error center">You may not create an input folder with the same path more than once</h4>
<br />
</c:if>

<div class="frame-job">

<button type="button" class="right" onclick="javascript:location.href ='<spring:url value="/job/${job.id}/delete" htmlEscape="true" />';">Delete</button>

<br />
<form:form modelAttribute="job" method="post" name="editJob" onSubmit="return jobValidation(this);">
<fieldset>
<h4>General</h4>
<table>
<tr>
<td>Name:</td>
<td><form:input path="name" size="30" maxlength="50"/></td>
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
</fieldset>
<br />

<fieldset>
<h4>Import from</h4>

<p id="inputFolderError" class="hide error">input path may not be empty.</p>
<p><form:errors path="inputFolder" cssClass="error"/></p>

<%int rowInputCounter=1; %>
<table id="inputPathTable">
<c:if test="${not empty job.inputFolder}" >
<c:forEach var="input" items="${job.inputFolder}">
<tr>
<td>
<div id="inputPath<%=rowInputCounter%>"><%=rowInputCounter%></div>
</td>
<td>
<c:out value="${input}" />
</td>
<td>
<div class="pointer" id="removeInputPath<%=rowInputCounter%>" onclick="removeRowFromInputPath(<%=rowInputCounter%>)">remove</div>
</td>
</tr>
<%rowInputCounter++; %>
</c:forEach>
</c:if>
</table>

<div id="addInputPathButton" class="link small hide" onclick="addInputPath();"><span class="pointer">Add Input Path</span></div>
<table id="inputPathForm" >
<tr>
<td>Path: <form:input path="inputPath" size="50" maxlength="255" /></td>
<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "inputPath",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {   
                                                                                    
                                        }
                                }));
                        </script>

</tr>
<tr>
<td><button type="button" class="button" onclick="cancelInputPath();">Cancel</button></td>
<td><input name="cancelButton" type="button" class="button" value="Ok" onclick="confirmInputPath();addRowToInputPath(this.form);" /></td>

</tr>
</table>

 <table id="tblInputPath" class="hide">
 <c:if test="${not empty job.inputFolder}" >
<c:forEach var="inputPathName" items="${job.inputFolder}">
<tr>
<td><input name="inputFolder" type="checkbox" value="<c:out value="${inputPathName}" />" checked="true" /></td>
</tr>
</c:forEach>
</c:if>
</table>
<!-- 
<table>
<tr>
<td>Path: <form:input path="inputFolder" size="50" maxlength="255"/></td>
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
<tr>
<td class="cell-padding">Extension: <form:input path="extension" size="25" maxlength="255" /></td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "extension",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {                                         	
                                        }
                                }));
                        </script>
</tr>
</table>

-->

<br />
<h4>Destination</h4>

<%@ include file="/WEB-INF/jsp/destination.jsp"%>
<table>
<tr>
<td>Path: <form:input path="destinationFolder" size="50" maxlength="255"/></td>
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
</fieldset>
<br />

<fieldset>
<h4>Schedule</h4>

<%@ include file="/WEB-INF/jsp/schedule.jsp"%>
</fieldset>
<br />

<%@ include file="/WEB-INF/jsp/job-options.jsp"%>

<button type="button" class="left" onclick="javascript:location.href ='<spring:url value="/job/dashboard" htmlEscape="true" />';">Cancel</button>

<input id="proceed" type="submit" value="Update job" class="right"/>

</form:form>
</div>	
</div>
</body>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
