<div class="indent">

<div><form:errors path="sourceSink" cssClass="error"/></div>

<%int count=1; %>

<table id="tblDestination" class="indent">
<c:if test="${empty destinations}" >
<c:if test="${empty destinationInfoList}" >
<p id="noDestinations">No existing destinations found.</p>
</c:if>
</c:if>
<tr><td><form:errors path="dest" cssClass="error"/></td></tr>

<c:forEach var="destination" items="${destinations}">
<c:forEach var="destinationParams" items="${destination.configuredSourceSinkParameterSet}">
<c:if test="${destinationParams.name=='name'}" >
<tr>
<td>
<div>
<% if (count==1){ %>
<input type="radio" id="dest<%=count %>" name="dest" value="destExists<c:out value="${destination.id}" />"  checked="true"/>
<%}else{ %>
<input type="radio" id="dest<%=count %>" name="dest" value="destExists<c:out value="${destination.id}"/>"  />
<%} %>
<c:out value="${destinationParams.value}" /> - 
<%count++; %>
</c:if>
</c:forEach>
<c:forEach var="destinationParams" items="${destination.configuredSourceSinkParameterSet}">
<c:if test="${destinationParams.name=='url'}" >
<c:out value="${destinationParams.value}" />
</div>
</td>
</tr>
</c:if>
</c:forEach>
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
<input type="hidden" id="dest<%=count %>" name="dest" value="<c:out value="${destinationInfo.destinationValue}" />"  />
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
<td><form:radiobutton path="destinationType" value="AlfrescoSink" checked="true" />Alfresco</td>
</tr>
<tr>
<td></td>
<td><form:radiobutton path="destinationType" value="CMIS" />CMIS</td>
</tr>
<tr>
<td>URL:</td>

<td><form:input path="destinationURL" size="50" maxlength="40" /></td>
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
<td onclick="cancelDestination();"><span class="pointer">Cancel</span></td>
<td id="confirm" onclick="checkDestinationFields();"><span class="pointer">Ok</span></td>

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