<div><form:errors path="cron" cssClass="error"/></div>
<p id="cronError" class="hide error">you must create a schedule.</p>
<%int counter = 1;%>
<table id="tblSample" >
 <c:if test="${empty job.cron}" >
<c:forEach var="schedule" items="${schedules}">
<c:if test="${schedule.quartzScheduling ne defaultSchedule}" >
<tr>
<td>
<div id="rowNumber<%=counter%>"><%=counter%></div>
</td>
<td>Cron job</td>
<td>
<div><c:out value="${schedule.quartzScheduling}" /></div>
</td>
<td>
<div class="pointer" id="remove<%=counter%>" onclick="removeRowFromSchedule(<%=counter%>)">remove</div>
</td>
</tr>
<%counter++; %>
</c:if>
</c:forEach>
</c:if>
<c:forEach var="cronjob" items="${job.cron}">
<c:if test="${cronjob ne defaultSchedule}" >
<tr>
<td>
<div id="rowNumber<%=counter%>"><%=counter%></div>
</td>
<td>Cron job</td>
<td>
<div><c:out value="${cronjob}" /></div>
</td>
<td>
<div class="pointer" id="remove<%=counter%>" onclick="removeRowFromSchedule(<%=counter%>)">remove</div>
</td>
</tr>
<%counter++; %>
</c:if>
</c:forEach>
</table>

<div id="addScheduleButton" class="link small" onclick="addSchedule();"><span class="pointer">Add Schedule</span></div>
<div id="scheduleForm" class="hide">
<table>
<tr>
<td><form:radiobutton path="runFrequency" value="Single run at" onclick="scheduleBox(0)" checked="true" />Single run at</td>
<td id="sDate" >date: <form:input path="singleDate" size="8" /></td>
						<script type="text/javascript">
							Spring.addDecoration(new Spring.ElementDecoration({
								elementId : "singleDate",
								widgetType : "dijit.form.DateTextBox",
								widgetAttrs : { 
								constraints : {
									datePattern : "dd/MM/yyyy"
								},
									value: new Date(),
									datePattern : "dd/MM/yyyy"
								}
							})); 
						</script>
<td id="sTime" >time: <form:input path="singleTime" size="5" /></td>
						<script type="text/javascript">
							Spring.addDecoration(new Spring.ElementDecoration({
								elementId : "singleTime",
								widgetType : "dijit.form.TimeTextBox",
								widgetAttrs : { 
								
								value: new Date(),
									constraints : {
										timePattern : "HH:mm",
										clickableIncrement: "T00:15:00",
						                visibleIncrement: "T00:15:00"
									},			
									timePattern : "HH:mm"			
								}
							})); 
						</script>

</tr>

<tr>
<td><form:radiobutton path="runFrequency" value="Hourly" onclick="scheduleBox(1)"/>Hourly</td>
<td id="hourly" class="hide">minutes: <form:input path="hourTime" size="2"/></td>
						<script type="text/javascript">
							Spring.addDecoration(new Spring.ElementDecoration({
								elementId : "hourTime",
								widgetType : "dijit.form.TimeTextBox",
								widgetAttrs : { 
													
								value: new Date(),
								constraints : {
										timePattern : 'mm',
										clickableIncrement: 'T00:05:00',
						                visibleIncrement: 'T00:05:00',
										visibleRange: 'T01:00:00'
									},			
									timePattern : 'mm'		
								}
							})); 
						</script>
</tr>
<tr>
<td><form:radiobutton path="runFrequency" value="Daily" onclick="scheduleBox(2)"/>Daily</td>
<td id="daily" class="hide">time: <form:input path="dayTime" size="5"  /></td>
						<script type="text/javascript">
							Spring.addDecoration(new Spring.ElementDecoration({
								elementId : "dayTime",
								widgetType : "dijit.form.TimeTextBox",
								widgetAttrs : { 
								value: new Date(),
									constraints : {
										timePattern : 'HH:mm',
										clickableIncrement: 'T00:15:00',
						                visibleIncrement: 'T00:15:00'
									},		
									timePattern : 'HH:mm'		
								}
							})); 
						</script>
</tr>
<tr>
<td><form:radiobutton path="runFrequency" value="Weekly" onclick="scheduleBox(3)"/>Weekly</td>
<td id="weeklyDay" class="hide">day: <form:select path="weekDay" >
		 	<form:option value="Monday" label="Monday"/>
			<form:option value="Tuesday" label="Tuesday"/>
			<form:option value="Wednesday" label="Wednesday"/>
			<form:option value="Thursday" label="Thursday"/>
			<form:option value="Friday" label="Friday"/>
			<form:option value="Saturday" label="Saturday"/>
			<form:option value="Sunday" label="Sunday"/>
			</form:select>
</td>
								<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "weekDay",
                                        widgetType : "dijit.form.FilteringSelect",
                                        widgetAttrs : {
                                                required : true,
                                               	invalidMessage : "this value is not allowed in this field",
                                               	promptMessage : "day of week cannot be empty"
                                                   	
                                        }
                                }));
                        </script>

<td id="weeklyTime" class="hide">time: <form:input path="weekTime" size="5"  value="00:00"/></td>
						<script type="text/javascript">
							Spring.addDecoration(new Spring.ElementDecoration({
								elementId : "weekTime",
								widgetType : "dijit.form.TimeTextBox",
								widgetAttrs : { 
								value: new Date(),
									constraints : {
										timePattern : 'HH:mm',
										clickableIncrement: 'T00:15:00',
						                visibleIncrement: 'T00:15:00'
									},		
									timePattern : 'HH:mm'	
								}
							})); 
						</script>
</tr>
<tr>
<td><form:radiobutton path="runFrequency" value="Advanced" onclick="scheduleBox(4)"/>Advanced</td>
<td id="advanced" class="hide">cronjob: <form:input path="cronJob" size="15" maxlength="255" /></td>
						<script type="text/javascript">
							Spring.addDecoration(new Spring.ElementDecoration({
								elementId : "cronJob",
								widgetType : "dijit.form.ValidationTextBox",
								widgetAttrs : { 
								value: "0 0 * * * ?"			
								}
							})); 
						</script>
</tr>

<tr>
<td><button type="button" class="button" onclick="cancelSchedule();">Cancel</button></td>
<td><input name="cancelButton" type="button" class="button" value="Ok" onclick="confirmSchedule();addRowToSchedule(this.form);" /></td>

</tr>
</table>
</div>
 
 
 <table id="tblCron" class="hide">
 <c:if test="${empty job.cron}" >
<c:forEach var="schedule" items="${schedules}">
<c:if test="${schedule.quartzScheduling ne defaultSchedule}" >
<tr>
<td><input name="cron" type="checkbox" value="<c:out value="${schedule.quartzScheduling}" />" checked /></td>
</tr>
</c:if>
</c:forEach>
</c:if>
<c:forEach var="cronjob" items="${job.cron}">
<c:if test="${cronjob ne defaultSchedule}" >
<tr>
<td><input name="cron" type="checkbox" value="<c:out value="${cronjob}" />" checked /></td>
</tr>
</c:if>
</c:forEach>
</table>

<!-- Opens up the create schedule dialog if there are no existing schedules for the job-->
<c:if test="${empty schedules && empty job.cron}" >
<script type="text/javascript">
		addSchedule();
</script>
</c:if>
