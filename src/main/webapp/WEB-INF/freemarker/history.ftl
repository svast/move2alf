<h1>History</h1>
<#include "header.ftl">

<h2>${job.name}</h2>
<div class="centercontainer">
<#if historyInfoList??>
	<table class="zebra-striped small">
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

<#assign pagedLink="/job/${job.id}/history">
<#include "paging.ftl" />

</div>
<#include "footer.ftl">