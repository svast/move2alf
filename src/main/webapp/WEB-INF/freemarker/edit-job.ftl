<h1>Edit job</h1>
<#include "header.ftl">

<form:form modelAttribute="job" method="post" name="editJob" onSubmit="return jobValidation(this);">

</form>


<form method="post" name="editJob">
	<fieldset>
		<legend>General</legend>
		<div class="clearfix">
			<label for="name">Name:</label>
			<div class="input">
				<input id="name" class="xlarge" type="text" size="30" name="name" value="${job.name}"/>
			</div>
		</div>
		<div class="clearfix">
			<label for="description">Description:</label>
			<div class="input">
				<textarea id="description" class="xlarge" type="text" rows="5" name="description" >${job.description}</textarea>
			</div>
		</div>
	</fieldset>
	<fieldset>
		<legend>Import</legend>
		<div class="clearfix">
			<label for="inputTable">Input paths:</label>
		<table id="inputTable" class="small inputTable">
			<#assign lastIndex = 0>
			<#list job.inputFolder as folder>
				<tr>
					<td>${folder_index+1}</td>
					<td>${folder}</td>
					<td><img src="<@spring.url relativeUrl="/images/delete-icon.png"/>" alt="delete" /></td>
				</tr>
				<#assign lastIndex = folder_index />
			</#list>
				<tr>
					<td>${lastIndex+2}</td>
					<td><input name="inputPath" type="text" style="width:100%" maxlength="255" /></td>
					<td><img src="<@spring.url relativeUrl="/images/save-icon.png"/>" alt="save" /></td>
				</tr>
		</table>
		</div>
		<div class="clearfix">
			<label for="extension">Extension:</label>
			<div class="input">
				<input id="extension" class="xlarge" type="text" size="30" name="extension" value="${job.extension}"/>
			</div>
		</div>
		<div class="clearfix">
			<label for="destination">Destination server:</label>
			<div class="input">
				<select style="width:auto;" name="destination" id="destination">
					<#list destinations as destination>
						<#if destination.parameters?has_content>
						<option>${destination.parameters.name} - ${destination.parameters.url}</option>
						</#if>
					</#list>
				</select>
			</div>
		</div>
		<div class="clearfix">
			<label for="path">Destination path:</label>
			<div class="input">
				<input id="path" class="xlarge" type="text" size="30" name="path" value="${job.destinationFolder}"/>
			</div>
		</div>
	</fieldset>
	<fieldset>
		<legend>Schedules</legend>
		<table class="small inputTable">
			<#list schedules as schedule>
				<tr>
					<td>${schedule_index}</td>
					<td>Cronjob</td>
					<td>${schedule.quartzScheduling}</td>
					<td><img src="<@spring.url relativeUrl="/images/delete-icon.png"/>" alt="delete" /></td>
				</tr>
			</#list>
		</table>
	</fieldset>
</form>

<#include "footer.ftl">