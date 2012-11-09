<#include "general.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="History" >
<h2>${job.name?html}</h2>
<#if historyInfoList??>
	<table class="table-striped wide">
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

</@bodyMenu>
</@html>