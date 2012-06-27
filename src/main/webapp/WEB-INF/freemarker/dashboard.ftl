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
			<th class="small">
				<#if role=="SYSTEM_ADMIN"  || role=="JOB_ADMIN">
					<a href="<@spring.url relativeUrl="/job/create" />"><img src="<@spring.url relativeUrl="/images/add-icon.png"/>" label="Create new job" alt="Create new job" /></a>
				</#if>		
			</th>
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
			<td class="small" >
				<#if role=="SYSTEM_ADMIN"  || role=="JOB_ADMIN">
					<a href="<@spring.url relativeUrl="/job/${jobInfo.jobId}/edit" />"><img src="<@spring.url relativeUrl="/images/edit-icon.png"/>" label="edit" alt="edit" /></a>
				</#if>
			</td>
			<td><span data-content="${jobInfo.description!}" rel="popover" data-original-title="Description">${jobInfo.jobName}</span></td>
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
			<td>
				<#if role=="SYSTEM_ADMIN"  || role=="JOB_ADMIN">
					<a class="btn" href="<@spring.url relativeUrl="/job/${jobInfo.jobId}/cycle/run" />">RUN</a>
				</#if>		
			</td>
			<td>
				<#if role=="SYSTEM_ADMIN"  || role=="JOB_ADMIN">
					<img class="clickable" onclick="deleteJob('${jobInfo.jobId}')" src="<@spring.url relativeUrl="/images/delete-icon.png"/>" alt="delete" />
				</#if>		
			</td>
		</tr>
		</#list>
	</tbody>
</table>
<script>
$("span[rel=popover]").popover({
	offset: 10,
	html: true
});
</script>

<#if licenseIsValid >
        <div class="info">
        <hr>
        	This Move2Alf is licensed to ${licensee.companyName}.
        
            <#if expirationDate?has_content>
            The license expires on ${expirationDate?date}.
            </#if>
        </div>
</#if>
</@bodyMenu>
</@html>
