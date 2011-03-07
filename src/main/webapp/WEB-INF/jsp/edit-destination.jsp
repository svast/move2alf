<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>


<body onload>
<div class="span-24 last main">

<h2>Edit Destination</h2>

<div class="frame-job">

<a href="<spring:url value="/destination/${destination.id}/delete" htmlEscape="true" />" class="right"><button type="button">Delete</button></a>


<form:form modelAttribute="destinations" method="post" name="editDestination" onSubmit="return checkDestinationFields();">

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
<%int type=0; %>
<c:if test="${destination.className == 'CMIS'}" >
<%type= 1;%>
</c:if> 
<td>Type:</td> 
<td><input type="radio" id="destinationType0" name="destinationType" value="AlfrescoSink" <%if(type==0){ %>CHECKED <%} %> />Alfresco</td>
<td><form:errors path="destinationType" cssClass="error"/></td>
</tr>
<tr>
<td></td>
<td><input type="radio" id="destinationType1" name="destinationType" value="CMIS" <%if(type==1){ %>CHECKED <%} %> />CMIS</td>
</tr>
<tr>
<td>URL:</td>
<td><form:input path="destinationURL" size="50" maxlength="40" value="${destination.parameters.url}" /></td>
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

<a href="<spring:url value="/destinations" htmlEscape="true" />" class="left"><button type="button">Cancel</button></a>
<input id="proceed" type="submit" value="Update Destination" class="right" onclick="addRowToDestination();"/>
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
