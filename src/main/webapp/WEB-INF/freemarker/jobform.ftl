	<fieldset>
		<legend>General</legend>
		<@labeledSingleLineTextInput label="Name" name="name" binding="job.name" />
		
		<@textArea label="Description" name="description" binding="job.description" />
		
	</fieldset>
	<fieldset>
		<legend>Import</legend>
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/inputPathHandler2.js" />"> </script>
		<@labeledInput label="Input paths" forId="inputTable" helpText="You can add multiple paths. Press enter or click the save button to confirm a path.">
				<table id="inputPathTable" name="inputFolder" class="small inputTable table-striped">
					<#assign lastIndex = 0>
					<thead>
						<tr>
							<th>ID</th>
							<th>Path</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
					<#list job.inputFolder! as folder>
						<tr id="inputPath_${folder_index}">
							<td>${folder_index+1}</td>
							<td>${folder}<input name="inputFolder" type="hidden" value="${folder}" />
							</td>
							<td>
								<img onclick="$('#inputPath_${folder_index}').remove()" src="<@spring.url relativeUrl="/images/delete-icon.png"/>" alt="delete" class="clickable" />
							</td>
							
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
									e.preventDefault();
									addInputPath('inputPath');
									$("#inputPathTextbox").select();
								}
							});
						</script>
					</tbody>
				</table>
		</@labeledInput>
		
		<@labeledSingleLineTextInput label="Extension" name="extension" binding="job.extension" />
		<@labeledSelectList label="Destination server" name="dest" options=destinations; destination>
			<#if destination.parameters?has_content>
				<option value="${destination.id}" <#if (job.dest?? && job.dest==destination.id)>selected="selected"</#if>> ${destination.parameters.name} - ${destination.parameters.url}</option>
			</#if>
		</@labeledSelectList>
		
		<@labeledSingleLineTextInput label="Destination path" name="path" binding="job.destinationFolder" />
		
	</fieldset>
	<#include "schedule.ftl">
	<fieldset>
		<legend>Processing</legend>
		
		<@labeledSingleLineTextInput label="Command before" name="commandbefore" binding="job.command" helpText="Execute command before processing." />
		
		<@labeledSelectList label="Meta-data processor" name="metadata" options=metadataOptions helpText="Choose the meta-data processor."; processor>
			<option value="${processor.class.name}" <#if job.metadata?? && processor.class.name=job.metadata >selected="selected"</#if> >${processor.name} - ${processor.description}</option>
		</@labeledSelectList>
		
				
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/parameterHandler2.js" />"> </script>
		
		<@labeledParamTableAndInput label="Meta-data parameters" baseId="paramMetadata" paramList=job.paramMetadata />
		
		<@labeledInput label="Transformation" forId="transformation">
				<@radio name="transform" value="notransformation" checked=(jobTransform?? || !jobTransform?has_content || "No transformation"==jobTransform) description="No transformation" />
			<#list transformOptions as transformOption>
				<@radio name="transform" value=transformOption.class.name checked=(job.transform?? && transformOption.class.name==job.transform) description=transformOption.description />
			</#list>
		</@labeledInput>
		
		<@labeledParamTableAndInput label="Tranformation parameters" baseId="paramTransform" paramList=job.paramTransform />
		
		<@labeledSingleLineTextInput label="Command after" name="commandafter" binding="job.commandAfter" helpText="Execute command after processing." />
		
	</fieldset>
	
	<fieldset>
		<legend>Options</legend>
		
		<@labeledInput label="If content exists in destination" forId="docExists" helpText="What should happen when the document already exists in the destination?">
			<@radios name="docExist" options=[
			 ["SkipAndLog", (!job.docExist?? | job.docExist=="SkipAndLog"),"Skip document and log error"],
			 ["Skip", job.docExist?? && job.docExist=="Skip", "Skip document silently"],
			 ["Overwrite", job.docExist?? && job.docExist=="Overwrite", "Overwrite document"],
			 ["Delete", job.docExist?? && job.docExist=="Delete", "Delete document"],
			 ["ListPresence", job.docExist?? && job.docExist=="ListPresence", "List presence"]
			 ] />
		</@labeledInput>
		
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/checkboxWithText.js" />"> </script>
		<@unLabeledInput>
			<@checkboxWithOption binding="job.moveBeforeProc" label="Move before processing to path" textboxValue=job.beforeProcPath />
			<@checkboxWithOption binding="job.moveAfterLoad" label="Move loaded files to path" textboxValue=job.afterLoadPath />
			<@checkboxWithOption binding="job.moveNotLoad" label="Move not loaded files to path" textboxValue=job.notLoadPath />
		</@unLabeledInput>
	</fieldset>

	<fieldset>
		<legend>Error reporting</legend>
		<@unLabeledInput helpText="Separate multiple e-mail addresses with commas">
			<@checkboxWithOption binding="job.sendNotification" label="Send notification e-mails on errors to" textboxValue=job.emailAddressError />
		</@unLabeledInput>
		<@unLabeledInput helpText="Separate multiple e-mail addresses with commas">
			<@checkboxWithOption binding="job.sendReport" label="Send load reporting e-mails to" textboxValue=job.emailAddressRep />
		</@unLabeledInput>
	</fieldset>

	<input type="submit" value="submit" />