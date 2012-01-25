<h1>Dashboard</h1>
<#assign activeMenu="Home" />
<#include "header.ftl">

<script src="<@spring.url relativeUrl="/js/jquery.tablesorter.js" />"></script>
<script >
$(function() {
	$("table#dashboard").tablesorter({
		headers:{
			0:{sorter: false},
			4:{sorter: false},
			5:{sorter: false}
		}
	});
});
</script>

<table id="dashboard" class="zebra-striped">
	<thead>
		<tr>
			<th><a href="<@spring.url relativeUrl="/job/create" />"><img src="<@spring.url relativeUrl="/images/add-icon.png"/>" label="Create new job" alt="Create new job" /></a></th>
			<th>Job Name</th>
			<th>Last run</th>
			<th>Status</th>
			<th></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<#list jobInfoList as jobInfo>
		<tr>
			<td style="width:10px;" ><a href="<@spring.url relativeUrl="/job/${jobInfo.jobId}/edit" />"><img src="<@spring.url relativeUrl="/images/edit-icon.png"/>" label="edit" alt="edit" /></a></td>
			<td><a data-content="${jobInfo.description}" rel="popover" href="#" data-original-title="Description">${jobInfo.jobName}</a></td>
			<script>
				$(function () {
					$("a[rel=popover]").popover({
						offset: 10
					}).click(function(e) {
							e.preventDefault()
					})
				})
			</script>
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
			<td><a class="btn primary" href="<@spring.url relativeUrl="/job/${jobInfo.jobId}/cycle/run" />">RUN</a></td>
		</tr>
		</#list>
	</tbody>
</table>

<#include "footer.ftl">