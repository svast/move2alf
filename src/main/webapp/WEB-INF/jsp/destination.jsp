<div class="indent">

<div id="destError" class="hide error">you must create a destination.</div>
<div><form:errors path="sourceSink" cssClass="error"/></div>
<div><form:errors path="dest" cssClass="error"/></div>

<%int count=1; %>

<c:if test="${empty destinations && empty destinationInfoList && empty showDestinations}" >
<br />
<div id="noDestinations">No existing destinations found.<br /></div>
</c:if>

<table id="tblDestination" class="indent">

<c:forEach var="destination" items="${destinations}">
<c:if test="${not empty destination.parameters}" >

<tr>
<td>
<div>

<c:set var="jobDest" value="${job.dest}" scope="session" />
<c:set var="destId" value="${destination.idAsString}" scope="session" />
<%
String jobDest = (String) session.getAttribute("jobDest");
String destId = (String) session.getAttribute("destId");
if ((count==1 && (jobDest == null || jobDest.equals(""))) || destId.equals(jobDest)) { 
%>
<input type="radio" id="dest<%=count %>" name="dest" value="destExists<c:out value="${destination.id}" />"  checked="true"/>
<%}else{ %>
<input type="radio" id="dest<%=count %>" name="dest" value="destExists<c:out value="${destination.id}"/>"  />
<%} %>
<c:out value="${destination.parameters.name}" /> - <c:out value="${destination.parameters.url}" />
<%count++; %>

</div>
</td>
</tr>

</c:if>
</c:forEach>

<c:if test="${not empty destinationInfoList}" >
<c:if test="${notNull}" >
<c:forEach var="destinationInfo" items="${destinationInfoList}" >
<tr>
<td>
<div>
<%if(count==1){ %>
<input type="radio" id="dest<%=count %>" name="dest" value="<c:out value="${destinationInfo.destinationValue}" />"  checked="true"/>
<%}else{%>
<input type="radio" id="dest<%=count %>" name="dest" value="<c:out value="${destinationInfo.destinationValue}" />" />
<%} %>
<c:out value="${destinationInfo.destinationName}" /> - <c:out value="${destinationInfo.destinationUrl}" />
</div>
</td>
</tr>
<%count++; %>
</c:forEach>
</c:if>
</c:if>
<c:if test="${not empty createDestinationInfoList}" >
<c:if test="${notNull}" >
<c:forEach var="destinationInfo" items="${createDestinationInfoList}" >
<tr>
<td>
<div>
<%if(count==1){ %>
<input type="hidden" id="dest<%=count %>" name="dest" value="<c:out value='${destinationInfo.destinationValue}' />" checked="true"/>
<%}else{%>
<input type="hidden" id="dest<%=count %>" name="dest" value="<c:out value='${destinationInfo.destinationValue}' />" />
<%} %>
<c:out value="${destinationInfo.destinationName}" /> - <c:out value="${destinationInfo.destinationUrl}" />

</div>
</td>
</tr>
<%count++; %>
</c:forEach>
</c:if>
<c:if test="${notNull==false}" >
<div class="error">Fields may not be empty</div>
</c:if>
</c:if>
</table>

<div id="addDestinationButton" class="link small" onclick="addDestination();"><span class="pointer">Create new destination</span></div>

<table id="destinationForm" class="hide">
<tr>
<td>Name:</td> 
<td><form:input path="destinationName" size="15" maxlength="30" /></td>
<td id="destinationNameError" class="hide error">destination name may not be empty.</td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "destinationName",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {
                                               	invalidMessage : "name cannot be empty",
                                               	promptMessage : "name cannot be empty"      	
                                        }
                                }));
                        </script>
</tr>
<tr>
<td>Type:</td> 

<%count=0; %>
<c:forEach var="destinationOption" items="${destinationOptions}" >
<%if(count==0){%>
<td><form:radiobutton path="destinationType" value="${destinationOption.class.name}" checked="true" /><c:out value="${destinationOption.name}" /></td>
</tr>
<%}else{%>
<tr>
<td><form:radiobutton path="destinationType" value="${destinationOption.class.name}" /><c:out value="${destinationOption.name}" /></td>
</tr>
<%
}
count ++;
%>
</c:forEach>
<%if (count==1){ %>
<input type="radio" class="hide" value="" name="destinationType" />
<%} %>

<tr>
<td>URL:</td>

<td><form:input path="destinationURL" size="50" maxlength="50" /></td>
<td id="destinationURLError" class="hide error">destination url may not be empty.</td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "destinationURL",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {
                                               	invalidMessage : "url cannot be empty",
                                               	promptMessage : "url cannot be empty" 	
                                        }                               		
                                }));
                        </script>
</tr>
<tr>
<td>Username:</td>
<td><form:input path="alfUser" size="30" maxlength="30" /></td>
<td id="alfUserError" class="hide error">user may not be empty.</td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "alfUser",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {
                                               	invalidMessage : "user cannot be empty",
                                               	promptMessage : "user cannot be empty"      	
                                        }
                                }));
                        </script>
</tr>
<tr>
<td>Password:</td>
<td><form:input path="alfPswd" size="30" maxlength="30" /></td>
<td id="alfPswdError" class="hide error">password may not be empty.</td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "alfPswd",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {
                                               	invalidMessage : "password cannot be empty",
                                               	promptMessage : "password cannot be empty"      	
                                        }
                                }));
                        </script>
</tr>
<tr>
<td>Number of threads:</td>
<td><form:input path="nbrThreads" size="10" value="5" maxlength="2"/></td>
<td id="nbrThreadsError" class="hide error">number of threads may not be empty and may only contain numbers.</td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "nbrThreads",
                                        widgetType : "dijit.form.NumberTextBox",
                                        widgetAttrs : {
                                               	invalidMessage : "number of threads must contain a number",
                                               	promptMessage : "number of threads cannot be empty"     
                                                   	 	
                                        }
                                }));
                        </script>
</tr>

<tr>
<td><button type="button" class="button" onclick="cancelDestination();">Cancel</button></td>
<td id="confirm"><input name="cancelButton" class="button" type="button" value="Ok" onclick="destinationValidation(this.form);" /></td>


</tr>
</table>

<table id="tblDestForm" class="hide">
<c:if test="${not empty destinationInfoList}" >
<c:if test="${notNull}" >
<c:forEach var="destinationInfo" items="${destinationInfoList}" >
<tr>
<td><input type="checkbox" name="sourceSink" value=<c:out value="${destinationInfo.destinationValue}" /> checked="true"/></td>
</tr>
</c:forEach>
</c:if>
</c:if>
<c:if test="${not empty createDestinationInfoList}" >
<c:if test="${notNull}" >
<c:forEach var="destinationInfo" items="${createDestinationInfoList}" >
<tr>
<td><input type="checkbox" name="sourceSink" value=<c:out value="${destinationInfo.destinationValue}" /> checked="true"/></td>
</tr>
</c:forEach>
</c:if>
</c:if>
</table>

</div>

<!-- Opens up the create destination dialog if there are no existing destinations-->
<c:if test="${empty destinations && empty destinationInfoList}" >
	<script type="text/javascript">
			addDestination();
	</script>
</c:if>