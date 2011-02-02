<div id="addScheduleButton" class="link small indent" onclick="addSchedule();"><span class="pointer">Add Schedule</span></div>
<div id="scheduleForm" class="hide">
<table class="indent">
<tr>
<td><form:radiobutton path="runFrequency" value="Single run at" onclick="scheduleBox(0)"/>Single run at</td>
<td id="sDate" class="hide">date: <form:input path="singleDate" size="8" maxlength="10" value="1/1/2011"/></td>
<td id="sTime" class="hide">time: <form:input path="singleTime" size="5" maxlength="5" value="00:00" /></td>
</tr>
<tr>
<td><form:radiobutton path="runFrequency" value="Hourly" onclick="scheduleBox(1)"/>Hourly</td>
<td id="hourly" class="hide">minutes: <form:input path="hourTime" size="2" maxlength="2" value="00"/></td>
</tr>
<tr>
<td><form:radiobutton path="runFrequency" value="Daily" onclick="scheduleBox(2)"/>Daily</td>
<td id="daily" class="hide">time: <form:input path="dayTime" size="5" maxlength="5" value="00:00"/></td>
</tr>
<tr>
<td><form:radiobutton path="runFrequency" value="Weekly" onclick="scheduleBox(3)"/>Weekly</td>
<td id="weeklyDay" class="hide">day: <form:select path="weekDay">
			<form:option value="Monday" label="Monday"/>
			<form:option value="Tuesday" label="Tuesday"/>
			<form:option value="Wednesday" label="Wednesday"/>
			<form:option value="Thursday" label="Thursday"/>
			<form:option value="Friday" label="Friday"/>
			<form:option value="Saturday" label="Saturday"/>
			<form:option value="Sunday" label="Sunday"/>
			</form:select>
</td>
<td id="weeklyTime" class="hide">time: <form:input path="weekTime" size="5" maxlength="15" value="00:00"/></td>
</tr>
<tr>
<td><form:radiobutton path="runFrequency" value="Advanced" onclick="scheduleBox(4)"/>Advanced</td>
<td id="advanced" class="hide">cronjob: <form:input path="cronJob" size="15" maxlength="15" value="0 0 * * * ?"/></td>
</tr>
<tr>
<td onclick="cancelSchedule();"><span class="pointer">Cancel</span></td>
<td onclick="confirmSchedule();addRowToSchedule();"><span class="pointer">Ok</span></td>
</tr>
</table>
</div>




