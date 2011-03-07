<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>


<body onload>
<div class="span-24 last main">

<h2>Edit Destination</h2>

<div class="frame-job">

<a href="<spring:url value="/destination/${destination.id}/delete" htmlEscape="true" />" class="right"><button type="button">Delete</button></a>


<form:form modelAttribute="destinations" method="post" name="editDestination">

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
</tr>
<tr>
<td>Username:</td>
<td><form:input path="alfUser" size="30" maxlength="30" value="${destination.parameters.user}" /></td>
<td><form:errors path="alfUser" cssClass="error"/></td>
</tr>
<tr>
<td>Password:</td>
<td><form:input path="alfPswd" size="30" maxlength="30" value="${destination.parameters.password}" /></td>
<td><form:errors path="alfPswd" cssClass="error"/></td>
</tr>
<tr>
<td>Number of threads:</td>
<td><form:input path="nbrThreads" size="10" value="${destination.parameters.threads}" maxlength="2" onKeyPress="return numbersonly(this, event)"/></td>
<td><form:errors path="nbrThreads" cssClass="error"/></td>
</tr>

</table>

<table id="tblDestForm" class="hide">
</table>

<br />

<a href="<spring:url value="/destinations" htmlEscape="true" />" class="left"><button type="button">Cancel</button></a>
<input type="submit" value="Update Destination" class="right" onclick="addRowToDestination();"/>
</form:form>
</div>	
</div>

</body>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
