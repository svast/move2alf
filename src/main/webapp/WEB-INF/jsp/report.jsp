<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="/WEB-INF/jsp/header.jsp" %>
<%@ taglib prefix="tg" tagdir="/WEB-INF/tags" %>


<jsp:useBean id="pagedListHolder" scope="request" 
   type="org.springframework.beans.support.PagedListHolder"/>
<%-- // create link for pages, "~" will be replaced 
   later on with the proper page number --%>
<c:url value="/job/${job.id}/${cycle.id}/report" var="pagedLink">
<c:param name="action" value="list"/>
<c:param name="p" value="~"/>
</c:url>


<div class="span-24 last main">

<h2>Report</h2>
<br />

<div class="frame-job">
<h3><c:out value="${job.name}" /> - <fmt:formatDate value="${cycle.startDateTime}" pattern="yyyy-MM-dd HH:mm" type="both"/></h3>

<table>
<tr>
<th class="alignRight">Description:</th><td style="width: 200px;"><c:out value="${job.description}" /></td>
</tr>
<tr> <td><br /></td></tr>
<tr>
<th class="alignRight">Start time:</th><td><fmt:formatDate value="${cycle.startDateTime}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></td>
</tr>
<tr><td><br /> </td></tr>
<tr>
<th class="alignRight">End time:</th><td><fmt:formatDate value="${cycle.endDateTime}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></td>
</tr>
<tr><td><br /> </td></tr>
<tr>
<th class="alignRight">Duration:</th><td id="duration"><c:out value="${duration}" /></td>
</tr>
<tr><td><br /> </td></tr>
<tr>
<th class="alignRight">Status:</th><td><c:out value="${cycle.schedule.state.displayName}" /></td>
<th>Nr. of documents:</th><td><c:out value="${documentListSize}" /></td>
</tr>
<tr><td><br /> </td></tr>
<tr>
<th class="alignRight">Docs / s:</th><td><c:out value="${docsPerSecond}" /></td>
</tr>
</table>

<br />

<div class="indent"><b>List of imported documents </b></div>

<div class="right">
<button type="button" onclick="javascript:location.href ='<spring:url value="/job/${job.id}/${cycle.id}/report/exportcsv" htmlEscape="true" />';">export to csv</button>
<button type="button" onclick="javascript:location.href ='<spring:url value="/job/${job.id}/${cycle.id}/report/exportpdf" htmlEscape="true" />';">export to pdf</button>
</div>

<table class="table-border-thin">
<tr>
<th width="20px" class="table-border-thin">No.</th>
<th class="table-border-thin">Name</th>
<th class="table-border-thin">Processing date and time</th>
<th class="table-border-thin">Status</th>
</tr>
<c:forEach items="${pagedListHolder.pageList}" var="item">
<tr>
<td class="table-border-thin">${item.key}</td>
<td class="table-border-thin">${item.name}</td>
<td class="table-border-thin">${item.processedDateTime}</td>
<td class="table-border-thin">${item.status}</td>
</tr>
</c:forEach>
</table>


<%-- // load our paging tag, pass pagedListHolder and the link --%>
<tg:paging pagedListHolder="${pagedListHolder}" pagedLink="${pagedLink}"/>

</div>

</div>



<%@ include file="/WEB-INF/jsp/footer.jsp" %>
