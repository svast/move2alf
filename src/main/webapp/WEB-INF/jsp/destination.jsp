<%@ page import="eu.xenit.move2alf.web.dto.JobConfig" %>
<%@ page import="eu.xenit.move2alf.core.dto.ConfiguredSourceSink" %>
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
<%
// TODO
JobConfig job = (JobConfig) request.getAttribute("job");
ConfiguredSourceSink destination = (ConfiguredSourceSink) pageContext.getAttribute("destination");
if ((job.getDest() == null || (job.getDest().equals("") && count==1)) || destination.getIdAsString().equals(job.getDest())) { %>
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
<input type="radio" id="dest<%=count %>" name="dest" value="<c:out value="${destinationInfo.destinationValue}" />"  />
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
<input type="hidden" id="dest<%=count %>" name="dest" value="<c:out value='${destinationInfo.destinationValue}' />"  />
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

<div id="addDestinationButton" class="link small indent" onclick="addDestination();"><span class="pointer">Create new destination</span></div>

<table id="destinationForm" class="hide indent">
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
<td><form:radiobutton path="destinationType" value="eu.xenit.move2alf.core.sourcesink.AlfrescoSourceSink" checked="true" />Alfresco</td>
</tr>
<tr>
<td></td>
<td><form:radiobutton path="destinationType" value="eu.xenit.move2alf.core.sourcesink.CmisSourceSink" />CMIS</td>
</tr>
<tr>
<td>URL:</td>

<td><form:input path="destinationURL" size="50" maxlength="40" /></td>
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
<td id="nbrThreadsError" class="hide error">number of threads may not be empty.</td>
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