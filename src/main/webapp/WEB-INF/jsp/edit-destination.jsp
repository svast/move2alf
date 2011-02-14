<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>


<body onload>
<div class="span-24 last main">

<h2>Edit Destination</h2>

<div class="frame-job">

<a href="<spring:url value="/destination/${destination.id}/delete" htmlEscape="true" />" class="right"><button type="button">Delete</button></a>


<form:form modelAttribute="job" method="post" name="editDestination">

<h3>
<table>
<tr>
<td>Name:</td>
<c:forEach var="destinationParams" items="${destination.configuredSourceSinkParameterSet}">
<c:if test="${destinationParams.name=='name'}" >
<td><c:out value="${destinationParams.value}" /></td>
</c:if>
</c:forEach>
</tr>
</table>
</h3>
<br />

<%int type=0; %>
<c:if test="${destination.sourceSinkClassName == 'CMIS'}" >
<%type= 1;%>
</c:if> 

<table id="tblDestination" class="hide">
</table>

<table id="destinationForm" class="indent">
<tr>
<td>Name:</td> 
<c:forEach var="destinationParams" items="${destination.configuredSourceSinkParameterSet}">
<c:if test="${destinationParams.name=='name'}" >
<td><form:input path="destinationName" size="15" maxlength="30" value="${destinationParams.value}" /> </td>
</c:if>
</c:forEach>
</tr>
<tr>
<td>Type:</td> 
<td><input type="radio" id="destinationType0" name="destinationType" value="AlfrescoSink" <%if(type==0){ %>CHECKED <%} %> />Alfresco</td>
</tr>
<tr>
<td></td>
<td><input type="radio" id="destinationType1" name="destinationType" value="CMIS" <%if(type==1){ %>CHECKED <%} %> />CMIS</td>
</tr>
<tr>
<td>URL:</td>
<c:forEach var="destinationParams" items="${destination.configuredSourceSinkParameterSet}">
<c:if test="${destinationParams.name=='url'}" >
<td><form:input path="destinationURL" size="50" maxlength="40" value="${destinationParams.value}" /></td>
</c:if>
</c:forEach>
</tr>
<tr>
<td>Username:</td>
<c:forEach var="destinationParams" items="${destination.configuredSourceSinkParameterSet}">
<c:if test="${destinationParams.name=='user'}" >
<td><form:input path="alfUser" size="30" maxlength="30" value="${destinationParams.value}" /></td>
</c:if>
</c:forEach>
</tr>
<tr>
<td>Password:</td>
<c:forEach var="destinationParams" items="${destination.configuredSourceSinkParameterSet}">
<c:if test="${destinationParams.name=='password'}" >
<td><form:input path="alfPswd" size="30" maxlength="30" value="${destinationParams.value}" /></td>
</c:if>
</c:forEach>
</tr>
<tr>
<td>Number of threads:</td>
<c:forEach var="destinationParams" items="${destination.configuredSourceSinkParameterSet}">
<c:if test="${destinationParams.name=='threads'}" >
<td><form:input path="nbrThreads" size="10" value="${destinationParams.value}" maxlength="2" onKeyPress="return numbersonly(this, event)"/></td>
</c:if>
</c:forEach>
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
