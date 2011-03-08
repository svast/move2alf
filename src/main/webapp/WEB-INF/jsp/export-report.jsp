<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ page contentType="application/x-csv" %><% response.setHeader("Content-Disposition", "attachment; filename=\"Report.csv\"");%>  
Name;<c:out value="${job.name}"/>
Description;<c:out value="${job.description}"/>
Start time;<c:out value="${cycle.startDateTime}"/>
End time;<c:out value="${cycle.endDateTime}"/>
Duration;<c:out value="${duration}"/>
Status;<c:out value="${cycle.schedule.state.displayName}" />
Nr. of documents;<c:out value=""/>
Docs / s;<c:out value=""/>

List of imported documents

Name;Processed date and time;Status
<c:forEach var="document" items="${documents}" >
<c:out value="${document.name}" />;<c:out value="${document.processedDateTime}"/>;<c:out value="${document.status}"/>
 </c:forEach> 