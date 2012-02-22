<h1>Report</h1>
<#include "header.ftl" />

<h2>${job.name} - ${cycle.startDateTime}</h2>

<table class="small">
	<tr>
		<th>Description:</th>
		<td>${job.description}</td>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<th>Start time:</th>
		<td>${cycle.startDateTime}</td>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<th>End time:</th>
		<td>${cycle.endDateTime}</td>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<th>Duration:</th>
		<td>${duration}</td>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<th>State:</th>
		<td>${cycle.schedule.state.displayName}</td>
		<th>Nr. of documents</th>
		<td>${documentListSize}</td>
	</tr>
	<tr>
		<th>Docs/s:</th>
		<td>${docsPerSecond}</td>
		<td></td>
		<td></td>
	</tr>

</table>
<div class="centercontainer">
<table class="table-striped small">
	<thead>
		<th>Name</th>
		<th>Processing date and time</th>
		<th>Status</th>
		<th>Parameters</th>
	</thead>
	<tbody>
		<#list processedDocuments as item>
			<tr>
				<td>${item.name}</td>
				<td>${item.processedDateTime}</td>
				<td>${item.status.displayName}</td>
				<td>
					<#list item.processedDocumentParameterSet as parameter>
						<p>
							<strong>${parameter.name}:</strong> ${parameter.value}
						</p>
					</#list>
			</tr>
		</#list>
	</tbody>

</table>

<#assign pagedLink="/job/${job.id}/${cycle.id}/report">
<#include "paging.ftl" />
</div>

<#include "footer.ftl" />