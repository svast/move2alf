	<fieldset>
		<legend>Schedules</legend>
		<div class="control-group">
			<div class="controls">
				<table class="small inputTable">
					<#list schedules as schedule>
						<tr>
							<td>${schedule_index}</td>
							<td>Cronjob</td>
							<td>${schedule.quartzScheduling}</td>
							<td><img src="<@spring.url relativeUrl="/images/delete-icon.png" />" alt="delete" /></td>
						</tr>
					</#list>
				</table>
				<a href="#">Add Schedule</a>
				<div id="scheduleform" class="hide">
				</div>
			</div>
		</div>
	</fieldset>