<#include "general.ftl" />
<#assign activeMenu="Home" />
<@html>
<@head>
</@head>
<@bodyMenu title="Dashboard">

<script src="<@spring.url relativeUrl="/js/jquery.tablesorter.js" />"></script>
<script>
    $.tablesorter.addParser({
        // set a unique id
        id: 'date',
        is: function(s) {
            // return false so this parser is not auto detected
            return false;
        },
        format: function(s, table, cell, cellIndex) {
            var $cell = $(cell);
            return $cell.attr('date');
        },
        // set type, either numeric or text
        type: 'text'
    });


$(function() {
	$("table#dashboard").tablesorter({
		headers:{
			0:{sorter: false},
            2:{sorter: 'date'},
			4:{sorter: false},
			5:{sorter: false},
			6:{sorter: false}
		}
	});
});

function stopJob(id){
	if(confirm("Are you sure you want to stop this job?")){
		window.location.href = "<@spring.url relativeUrl="/job/" />"+id+"/stop";
	}
}
</script>

<table id="dashboard" class="table-striped wide tablesorter">
	<col class="edit-column" />
	<col />
	<col />
	<col />
	<col />
	<col class="run-column" />
	<col class="delete-column" />
	
	<thead>
		<tr>
			<th>
				<#if role=="SYSTEM_ADMIN"  || role=="JOB_ADMIN">
					<a href="<@spring.url relativeUrl="/job/create" />"><img src="<@spring.url relativeUrl="/images/add-icon.png"/>" label="Create new job" alt="Create new job" /></a>
				</#if>		
			</th>
			<th class="header">Job Name</th>
			<th class="header">Last run</th>
			<th class="header">Status</th>
			<th></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<#list jobInfoList as jobInfo>
		<tr>
			<td>
				<#if jobInfo.scheduleState=="Not running" && (role=="SYSTEM_ADMIN"  || role=="JOB_ADMIN")>
					<a href="<@spring.url relativeUrl="/job/${jobInfo.jobId}/edit" />"><img src="<@spring.url relativeUrl="/images/edit-icon.png"/>" label="edit" alt="edit" /></a>
				</#if>
			</td>
			<td><span data-content="${jobInfo.description!?html}" rel="popover"
			    data-original-title="Description">${jobInfo.jobName?html}</span></td>
			<td date="<#if jobInfo.cycleStartDateTime?? >${jobInfo.cycleStartDateTime?string("yyyyMMddHHmmss")}<#else >0</#if>">
				<#if jobInfo.cycleStartDateTime??>
					<a href="<@spring.url relativeUrl="/job/${jobInfo.jobId}/report" />">
					<#if jobInfo.cycleStartDateTime?string("yyyyMMdd") == .now?string("yyyyMMdd") >
						Today, ${jobInfo.cycleStartDateTime?time}
					<#else>	
						${jobInfo.cycleStartDateTime}
					</#if>
					</a>
					 — ${jobInfo.nrOfDocuments} documents,
					 <span<#if (jobInfo.nrOfFailedDocuments > 0)> class="failedWarning"</#if>>${jobInfo.nrOfFailedDocuments} failed</span>
				</#if>
			</td>
			<td>${jobInfo.scheduleState!"Not running"}</td>
			<td><a href="<@spring.url relativeUrl="/job/${jobInfo.jobId}/history" />">History</a></td>
			<td>
				<#if jobInfo.scheduleState=="Not running" && (role=="SYSTEM_ADMIN"  || role=="JOB_ADMIN")>
					<a class="btn" href="<@spring.url relativeUrl="/job/${jobInfo.jobId}/cycle/run" />">RUN</a>
				</#if>
                <#if jobInfo.scheduleState=="Running" && (role=="SYSTEM_ADMIN"  || role=="JOB_ADMIN")>
                    <a class="btn btn-danger" onclick="stopJob('${jobInfo.jobId}')">STOP</a>
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

</@bodyMenu>
</@html>
