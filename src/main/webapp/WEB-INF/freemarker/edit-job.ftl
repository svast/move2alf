<#include "general.ftl" />
<#include "forms.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="Edit Job">
<form class="form-horizontal" method="post" name="editJob">
	<fieldset>
		<legend>General</legend>
		<@labeledSingleLineTextInput label="Name" name="name" binding="job.name" />
		
		<@textArea label="Description" name="description" value=job.description />
		
	</fieldset>
	<fieldset>
		<legend>Import</legend>
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/inputPathHandler2.js" />"> </script>
		<@labeledInput label="Input paths" forId="inputTable">
				<table id="inputPathTable" class="small inputTable table-striped">
					<#assign lastIndex = 0>
					<thead>
						<tr>
							<th>ID</th>
							<th>Path</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
					<#list job.inputFolder as folder>
						<tr id="inputPath_${folder_index}">
							<td>${folder_index+1}</td>
							<td>${folder}</td>
							<td><img onclick="$('#inputPath_${folder_index}').remove()" src="<@spring.url relativeUrl="/images/delete-icon.png"/>" alt="delete" class="clickable" /></td>
						</tr>
						<#assign lastIndex = folder_index />
					</#list>
						<tr>
							<td><img src="<@spring.url relativeUrl="/images/new.png"/>" alt="new" /></td>
							<td><input id="inputPathTextbox" name="inputPath" type="text" style="width:100%" maxlength="255" /></td>
							<td><img onclick="addInputPath('inputPath')" src="<@spring.url relativeUrl="/images/save-icon.png" />" alt="save" class="clickable" /></td>
						</tr>
						<script>
							$('#inputPathTextbox').bind('keypress', function(e) {
								if(e.keyCode==13){
									addInputPath('inputPath');
									$("#inputPathTextbox").select();
								}
							});
						</script>
					</tbody>
				</table>
		</@labeledInput>
		
		<@labeledSingleLineTextInput label="Extension" name="extension" value=job.extension />
		
		<@labeledSelectList label="Destination server" name="destination" options=destinations; destination>
			<#if destination.parameters?has_content>
				<option>${destination.parameters.name} - ${destination.parameters.url}</option>
			</#if>
		</@labeledSelectList>
		
		<@labeledSingleLineTextInput label="Destination path" name="path" value=job.destinationFolder />
		
	</fieldset>
	<#include "schedule.ftl">
	<fieldset>
		<legend>Processing</legend>
		
		<@labeledSingleLineTextInput label="Command before" name="commandbefore" value=job.command helpText="Execute command before processing." />
		
		<@labeledSelectList label="Meta-data processor" name="parser" options=metadataOptions helpText="Choose the meta-data processor."; processor>
			<option value="${processor.class.name}">${processor.name} - ${processor.description}</option>
		</@labeledSelectList>
		
				
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/parameterHandler2.js" />"> </script>
		
		<@labeledParamTableAndInput label="Meta-data parameters" baseId="meta-dataParameter" paramList=job.paramMetadata />
		
		<@labeledInput label="Transformation" forId="transformation">
				<@radio name="transformation" value="notransformation" checked=(jobTransform?? || !jobTransform?has_content || "No transformation"==jobTransform) description="No transformation" />
			<#list transformOptions as transformOption>
				<@radio name="transformation" value=transformOption.class.name checked=(transformOption==job.transform) description=transformOption.description />
			</#list>
		</@labeledInput>
		
		<@labeledParamTableAndInput label="Tranformation parameters" baseId="transformationParameter" paramList=job.paramTransform />
		
		<@labeledSingleLineTextInput label="Command after" name="commandafter" value=job.commandAfter helpText="Execute command after processing." />
		
	</fieldset>
	
	<fieldset>
		<legend>Options</legend>
		
		<@labeledInput label="If exists" forId="docExists" helpText="What should happen when the document already exists in the destination?">
			<@radios name="docExists" options=[
			 ["SkipAndLog", job.docExist=="SkipAndLog","Skip document and log error"],
			 ["Skip", job.docExist=="Skip", "Skip document silently"],
			 ["Overwrite", job.docExist=="Overwrite", "Overwrite document"],
			 ["Delete", job.docExist=="Delete", "Delete document"],
			 ["ListPresence", job.docExist=="ListPresence", "List presence"]
			 ] />
		</@labeledInput>
		
		<@unLabeledInput>
			<@checkboxWithOption name="moveBeforeLoad" checked=job.moveBeforeProc!false label="Move before processing to path" textboxValue=job.beforeProcPath />
			<@checkboxWithOption name="moveAfterLoad" checked=job.moveAfterLoad!false label="Move loaded files to path" textboxValue=job.afterLoadPath />
			<@checkboxWithOption name="moveNotLoad" checked=job.moveNotLoad!false label="Move not loaded files to path" textboxValue=job.notLoadPath />
		</@unLabeledInput>
	</fieldset>
	
	<fieldset>
		<legend>Error reporting</legend>
		<@unLabeledInput helpText="Separate multiple e-mail addresses with commas">
			<@checkboxWithOption name="sendErrorNotification" checked=job.sendNotification!false label="Send notification e-mails on errors to" textboxValue=job.emailAddressError />
		</@unLabeledInput>
		<@unLabeledInput helpText="Separate multiple e-mail addresses with commas">
			<@checkboxWithOption name="sendLoadReports" checked=job.sendReport!false label="Send load reporting e-mails to" textboxValue=job.emailAddressRep />
		</@unLabeledInput>
	</fieldset>
	
</form>

</@bodyMenu>
</@html>