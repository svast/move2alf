<h1>History</h1>
<#include "header.ftl">

<h2>${job.name}</h2>

<#if historyInfoList??>
	<table class="zebra-striped">
		<thead>
			<tr>
				<th>Start time</th>
				<th>Status</th>
				<th>Nr. of documents</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<#list pagedListHolder.pageList as item>
				<tr>
					<td>${item.cycleStartDateTime}</td>
					<td>${item.scheduleState}</td>
					<td>${item.nbrOfDocuments}</td>
					<td><a href="<@spring.url relativeUrl="/job/${job.id}/${item.cycleId}/report" />">view details</a></td>
				</tr>
			</#list>
		</tbody>
	</table>
	
<#else>
	This job has not yet run.
</#if>



<#include "footer.ftl">