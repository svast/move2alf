<div class="indent">

<%int count=0; %>

<table id="tblDestination" class="indent">
<c:if test="${empty destinations}" >
<p id="noDestinations">No existing destinations found.</p>
</c:if>

<c:forEach var="destination" items="${destinations}">
<c:forEach var="destinationParams" items="${destination.configuredSourceSinkParameterSet}">
<c:if test="${destinationParams.name=='name'}" >
<tr>
<td>
<div>
<input type="radio" id="dest<%=count %>" name="dest" value="destExists<c:out value="${destination.id}" />"  onclick="fillDestFields(<%=count %>)" />
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
</table>

<div id="addDestinationButton" class="link small indent" onclick="addDestination();"><span class="pointer">Create new destination</span></div>

<table id="destinationForm" class="hide indent">
<tr>
<td>Name:</td> 
<td><form:input path="destinationName" size="15" maxlength="30" /></td>
</tr>
<tr>
<td>Type:</td> 
<td><form:radiobutton path="destinationType" value="AlfrescoSink" />Alfresco</td>
</tr>
<tr>
<td></td>
<td><form:radiobutton path="destinationType" value="CMIS" />CMIS</td>
</tr>
<tr>
<td>URL:</td>
<td><form:input path="destinationURL" size="50" maxlength="40" /></td>
</tr>
<tr>
<td>Username:</td>
<td><form:input path="alfUser" size="30" maxlength="30" /></td>
</tr>
<tr>
<td>Password:</td>
<td><form:input path="alfPswd" size="30" maxlength="30" /></td>
</tr>
<tr>
<td>Number of threads:</td>
<td><form:input path="nbrThreads" size="10" value="5" maxlength="2" onKeyPress="return numbersonly(this, event)"/></td>
</tr>

<tr>
<td onclick="cancelDestination();"><span class="pointer">Cancel</span></td>
<td onclick="confirmDestination();addRowToDestination();"><span class="pointer">Ok</span></td>
</tr>
</table>

<table id="tblDestForm" class="hide">
</table>

</div>