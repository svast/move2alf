<#include "general.ftl" />
<#assign activeMenu="Home" />
<@html>
<@head>
</@head>
<@bodyMenu title="Dashboard">

<script src="<@spring.url relativeUrl="/js/jquery.tablesorter.js" />"></script>
<script>
$(function() {
	$("table#dashboard").tablesorter({
		headers:{
			0:{sorter: false},
			4:{sorter: false},
			5:{sorter: false},
			6:{sorter: false}
		}
	});
});

function deleteJob(id){
	if(confirm("Are you sure you want to delete this job?")){
		window.location.href = "<@spring.url relativeUrl="/job/" />"+id+"/delete";
	}
}
</script>

<table id="dashboard" class="table-striped wide">
	<thead>
		<tr>
			<th class="small"><a href="<@spring.url relativeUrl="/job/create" />"><img src="<@spring.url relativeUrl="/images/add-icon.png"/>" label="Create new job" alt="Create new job" /></a></th>
			<th class="header">Job Name</th>
			<th class="header">Last run</th>
			<th class="header">Status</th>
			<th></th>
			<th></th>
			<th></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<#list jobInfoList as jobInfo>
		<tr>
			<td class="small" ><a href="<@spring.url relativeUrl="/job/${jobInfo.jobId}/edit" />"><img src="<@spring.url relativeUrl="/images/edit-icon.png"/>" label="edit" alt="edit" /></a></td>
			<td><a data-content="${jobInfo.description!}" rel="popover" href="#" data-original-title="Description">${jobInfo.jobName}</a></td>
			<td>
				<#if jobInfo.cycleStartDateTime??>
					<a href="<@spring.url relativeUrl="/job/${jobInfo.jobId}/report" />">
					<#if jobInfo.cycleStartDateTime?string("yyyyMMdd") == .now?string("yyyyMMdd") >
						Today, ${jobInfo.cycleStartDateTime?time}
					<#else>	
						${jobInfo.cycleStartDateTime}
					</#if>
					</a>
				</#if>
			</td>
			<td>${jobInfo.scheduleState!"Not running"}</td>
			<td><a href="<@spring.url relativeUrl="/job/${jobInfo.jobId}/history" />">History</a></td>
			<td><a class="btn" href="<@spring.url relativeUrl="/job/${jobInfo.jobId}/cycle/run" />">RUN</a></td>
			<td><img class="clickable" onclick="deleteJob('${jobInfo.jobId}')" src="<@spring.url relativeUrl="/images/delete-icon.png"/>" alt="delete" /></td>
		</tr>
		</#list>
	</tbody>
</table>
<script>
$("a[rel=popover]").popover({
	offset: 10,
	html: true
});
</script>

<#if licenseIsValid >
        <div class="info">
        <hr>
        	This Move2Alf is licensed to ${licensee.companyName}.
        
            <#if expirationDate?has_content>
            The license expires on ${expirationDate}.
            </#if>
        </div>
</#if>
</@bodyMenu>
</@html>