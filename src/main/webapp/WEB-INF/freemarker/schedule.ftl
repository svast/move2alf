	<script type="text/javascript" src="<@spring.url relativeUrl="/js/bootstrap-collapse.js" />"> </script>
	<script type="text/javascript" src="<@spring.url relativeUrl="/js/scheduleHandler2.js" />"> </script>

	<fieldset>
		<legend>Schedules</legend>
		<div class="control-group">
			<div class="controls">
				<table id="schedules" class="small inputTable table-striped">
					<thead>
						<tr>
							<th>ID</th>
							<th>Cron Expression</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
					<#list job.cron! as schedule>
					<#if schedule!="0 0 0 1 1 ? 1" >
						<tr id="schedule_${schedule_index}">
							<td>${schedule_index}</td>
							<td class="cronexpr" >${schedule}
								<input name="cron" type="hidden" value="${schedule}" />
							</td>
							<td><img class="clickable" onclick="$('#schedule_${schedule_index}').remove()" src="<@spring.url relativeUrl="/images/delete-icon.png" />" alt="delete" /></td>
						</tr>
					</#if>
					</#list>
					</tbody>
				</table>
    			<div class="accordion-group">
    				<div class="accordion-title">
						<a class="accordion-toggle" href="#collapseOne" data-target="#scheduleform" data-toggle="collapse">Add Schedule</a>
					</div>
					<div id="scheduleform" class="collapse">
							<div class="accordion-inner">
									<div class="scheduleLine row inactive" id="singlerun" onclick="activateScheduleType('singlerun')">
										<div class="lineSegment span2">
											<label class="radio">
												<input type="radio" id="radio_singlerun" name="scheduleType" value="singlerun"/>
												Single run at
											</label>
										</div>
										<div class="lineSegment span4">
											<label for="singlerunDate">Date:</label>
											<input class="datepicker" type="text" name="singlerunDate" disabled="" />
										</div>
										<div class="lineSegment span4">
											<label for="singlerunTime">Time:</label>
											<input class="timepicker" type="text" name="singlerunTime" disabled="" />
										</div>
									</div>
									<div class="scheduleLine row inactive" id="hourly" onclick="activateScheduleType('hourly')" >
										<div class="lineSegment span2">
											<label class="radio">
												<input type="radio" id="radio_hourly" name="scheduleType" value="hourly"/>
												Hourly
											</label>
										</div>
										<div class="lineSegment span4">
											<label for="hourlyMinutes">Minutes:</label>
											<input class="minutepicker" type="text" name="hourlyMinutes" disabled="" />
										</div>
									</div>
									<div class="scheduleLine row inactive" id="daily"  onclick="activateScheduleType('daily')" >
										<div class="lineSegment span2">
											<label class="radio">
												<input type="radio" id="radio_daily" name="scheduleType" value="daily"/>
												Daily
											</label>
										</div>
										<div class="lineSegment span4">
											<label for="dailyTime">Time:</label>
											<input class="timepicker" type="text" name="dailyTime" disabled="" />
										</div>
									</div>
									<div class="scheduleLine row inactive" id="weekly" onclick="activateScheduleType('weekly')">
										<div class="lineSegment span2">
											<label class="radio">
												<input type="radio" id="radio_weekly" name="scheduleType" value="weekly" />
												Weekly
											</label>
										</div>
										<div class="lineSegment span4">
											<label for="weeklyDay">Day:</label>
											<select name="weeklyDay" disabled="" >
												<option value="MON">Monday</option>
												<option value="TUE">Tuesday</option>
												<option value="WED">Wednesday</option>
												<option value="THU">Thursday</option>
												<option value="FRI">Friday</option>
												<option value="SAT">Saturday</option>
												<option value="SUN">Sunday</option>
											</select>
										</div>
										<div class="lineSegment span4">
											<label for="weeklyTime">Time:</label>
											<input class="timepicker" type="text" name="weeklyTime" disabled="" />
										</div>
									</div>
									<div class="scheduleLine row inactive" id="advanced" onclick="activateScheduleType('advanced')" >
										<div class="lineSegment span2">
											<label class="radio">
												<input type="radio" id="radio_advanced" name="scheduleType" value="advanced"/>
												Advanced
											</label>
										</div>
										<div class="lineSegment span4">
											<label for="cronjob">Cronjob:</label>
											<input id="cronJob" type="text" name="cronJob" disabled="" />
										</div>
									</div>
									<input type="button" value="ADD" onclick="addRowToSchedule()" />				
							</div>
					</div>
				</div>
				
			</div>
		</div>
	</fieldset>