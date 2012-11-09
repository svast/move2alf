<#include "general.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="Report" >
<h2>${job.name?html}</h2>
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
		<td>${cycle.endDateTime!}</td>
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
		<th>Nr. of documents:</th>
		<td>${documentListSize}</td>
		<th<#if (nrOfFailedDocuments > 0)> class="failedWarning"</#if>>Failed documents:</th>
		<td<#if (nrOfFailedDocuments > 0)> class="failedWarning"</#if>>${nrOfFailedDocuments}</td>
	</tr>
	<tr>
		<th>State:</th>
    	<td>${cycle.state.displayName}</td>
		<th>Docs/s:</th>
		<td>${docsPerSecond}</td>
	</tr>

</table>
<a href="<@spring.url relativeUrl=("/job/"+job.id+"/"+cycle.id+"/report/exportcsv") />" >Export to CSV</a>
<hr />
<table class="table-striped wide">
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
						    <#if parameter.name = "errormessage">
							    <strong>${parameter.name}:</strong> ${parameter.value?replace(" < ", "<br/>&lt; ")}
							<#else>
							    <strong>${parameter.name}:</strong> ${parameter.value}
							</#if>
						</p>
					</#list>
			</tr>
		</#list>
	</tbody>

</table>

<#assign pagedLink="/job/${job.id}/${cycle.id}/report">
<#include "paging.ftl" />

</@bodyMenu>
</@html>