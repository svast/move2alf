<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2>Edit Destination</h2>

<c:if test="${destinationExists==true}" >
<br />
<h3 class="error center">A destination with this name already exists</h3>
<br />
</c:if>
<c:if test="${threadsIsInteger==false}" >
<br />
<h3 class="error center">The number of threads in the destination dialogue must contain numbers only</h3>
<br />
</c:if>

<div class="frame-job">

<button type="button" class="right" onclick="javascript:location.href ='<spring:url value="/destination/${destination.id}/delete" htmlEscape="true" />';">Delete</button>


<form:form modelAttribute="destinations" method="post" name="editDestination" onSubmit="return destinationValidation(this);">

<h3>
<table>
<tr>
<td>Name:</td>
<td><c:out value="${destination.parameters.name}" /></td>
</tr>
</table>
</h3>
<br />

<table id="tblDestination" class="hide">
</table>

<table id="destinationForm" class="indent">
<tr>
<td>Name:</td> 

<td><form:input path="destinationName" size="15" maxlength="30" value="${destination.parameters.name}" /> </td>
<td id="destinationNameError" class="hide error">destination name may not be empty.</td>
<td><form:errors path="destinationName" cssClass="error"/></td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "destinationName",
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
<td>Type:</td> 
<c:set var="firstIteration" value="${true}" />
<c:forEach var="destinationOption" items="${destinationOptions}" >
	<c:set var="valueAlreadySet" value="${false}" />
	<c:if test="${destinationOption.name == destination.className && firstIteration==true}" >
		<td><form:radiobutton path="destinationType" value="${destinationOption.name}" checked="true"/><c:out value="${destinationOption.name}" /></td>
		</tr>
		<c:set var="firstIteration" value="${false}" />
		<c:set var="valueAlreadySet" value="${true}" />
	</c:if>
	<c:if test="${destinationOption.name != destination.className && firstIteration==true && valueAlreadySet==false}" >
		<td><form:radiobutton path="destinationType" value="${destinationOption.name}" /><c:out value="${destinationOption.name}" /></td>
		</tr>
		<c:set var="firstIteration" value="${false}" />
		<c:set var="valueAlreadySet" value="${true}" />
	</c:if>
	<c:if test="${destinationOption.name == destination.className && firstIteration==false && valueAlreadySet==false}" >
		<td></td>
		<td><form:radiobutton path="destinationType" value="${destinationOption.name}" checked="true"/><c:out value="${destinationOption.name}" /></td>
		</tr>
		<c:set var="valueAlreadySet" value="${true}" />
	</c:if>
	<c:if test="${destinationOption.name != destination.className && firstIteration==false && valueAlreadySet==false}" >
		<tr>
		<td></td>
		<td><form:radiobutton path="destinationType" value="${destinationOption.name}" /><c:out value="${destinationOption.name}" /></td>
		</tr>
	</c:if>
</c:forEach>
<input type="hidden" value="" name="destinationType" />

<tr>
<td>URL:</td>
<td><form:input path="destinationURL" size="50" maxlength="40" value="${destination.parameters.url}" /></td>
<td id="destinationURLError" class="hide error">destination url may not be empty.</td>
<td><form:errors path="destinationURL" cssClass="error"/></td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "destinationURL",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {
                                                required : true,
                                               	invalidMessage : "url cannot be empty",
                                               	promptMessage : "url cannot be empty"
                                                   	
                                        }
                                }));
                        </script>
</tr>
<tr>
<td>Username:</td>
<td><form:input path="alfUser" size="30" maxlength="30" value="${destination.parameters.user}" /></td>
<td id="alfUserError" class="hide error">user may not be empty.</td>
<td><form:errors path="alfUser" cssClass="error"/></td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "alfUser",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {
                                                required : true,
                                               	invalidMessage : "user cannot be empty",
                                               	promptMessage : "user cannot be empty"
                                                   	
                                        }
                                }));
                        </script>
</tr>
<tr>
<td>Password:</td>
<td><form:input path="alfPswd" size="30" maxlength="30" value="${destination.parameters.password}" /></td>
<td id="alfPswdError" class="hide error">password may not be empty.</td>
<td><form:errors path="alfPswd" cssClass="error"/></td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "alfPswd",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {
                                                required : true,
                                               	invalidMessage : "password cannot be empty",
                                               	promptMessage : "password cannot be empty"
                                                   	
                                        }
                                }));
                        </script>
</tr>
<tr>
<td>Number of threads:</td>
<td><form:input path="nbrThreads" size="10" value="${destination.parameters.threads}" maxlength="2" onKeyPress="return numbersonly(this, event)"/></td>
<td id="nbrThreadsError" class="hide error">number of threads may not be empty.</td>
<td><form:errors path="nbrThreads" cssClass="error"/></td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "nbrThreads",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {
                                                required : true,
                                               	invalidMessage : "threads cannot be empty",
                                               	promptMessage : "threads cannot be empty"
                                                   	
                                        }
                                }));
                        </script>
</tr>

</table>

<table id="tblDestForm" class="hide">
</table>

<br />

<button type="button" class="left" onclick="javascript:location.href ='<spring:url value="/destinations" htmlEscape="true" />';">Cancel</button>
<input id="proceed" type="submit" value="Update Destination" class="right" />

</form:form>
</div>	
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
