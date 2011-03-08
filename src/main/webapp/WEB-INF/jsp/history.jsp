<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="/WEB-INF/jsp/header.jsp" %>

<%@ taglib prefix="tg" tagdir="/WEB-INF/tags" %>


<jsp:useBean id="pagedListHolder" scope="request" 
   type="org.springframework.beans.support.PagedListHolder"/>
<%-- // create link for pages, "~" will be replaced 
   later on with the proper page number --%>
<c:url value="/job/${job.id}/history" var="pagedLink">
<c:param name="action" value="list"/>
<c:param name="p" value="~"/>
</c:url>

<div class="span-24 last main">

<h2>Historical reports</h2>
<br />

<div class="frame-job">
<h3><c:out value="${job.name}" /></h3>

<c:if test='${empty cycles}'>
This job has not yet run.
</c:if>

<c:if test='${not empty cycles}'>
<table class="table-border-thin">
<tr class="table-border-thin">
<th class="table-border-thin">Start time</th>
<th class="table-border-thin">Status</th>
<th class="table-border-thin">Nr. of documents</th>
<th class="table-border-thin"></th>
</tr>

<c:forEach var="item" items="${pagedListHolder.pageList}">
<tr>
<td class="table-border-thin"><fmt:formatDate value="${item.startDateTime}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></td>
<td class="table-border-thin"><c:out value="${item.schedule.state.displayName}"/></td>
<td class="table-border-thin"><c:out value="" /></td>
<td class="table-border-thin"><a href="<spring:url value="/job/${job.id}/${item.id}/report" htmlEscape="true" />">view details</a></td>
</tr>

</c:forEach>
</table>
</c:if>

<%-- // load our paging tag, pass pagedListHolder and the link --%>
<tg:paging pagedListHolder="${pagedListHolder}" pagedLink="${pagedLink}"/>

</div>

</div>


<%@ include file="/WEB-INF/jsp/footer.jsp" %>
