	<fieldset>
		<legend>General</legend>
		<@labeledSingleLineTextInput label="Name" name="name" binding="job.name" attributes="maxlength='${job.jobNameMaxLength}'" />
		
		<@textArea label="Description" name="description" binding="job.description" attributes="maxlength='${job.jobDescriptionMaxLength}'" />
		
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
							<td><input id="inputPathTextbox" name="inputPath" type="text" style="width:100%" maxlength="${job.pathMaxLength}" /></td>
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
							$('#inputPathTextbox').closest("form").submit(function(){
									addInputPath('inputPath',true);
							});
						</script>
					</tbody>
				</table>
		</@labeledInput>
		
		<@labeledSingleLineTextInput label="Extension" name="extension" binding="job.extension" attributes="maxlength='${job.extensionMaxLength}'" />
		<@labeledSelectList label="Destination server" name="dest" options=destinations; destination>
			<#if destination.parameters?has_content>
				<option value="${destination.id}" <#if (job.dest?? && job.dest==destination.id)>selected="selected"</#if>> ${destination.parameters.name} - ${destination.parameters.url}</option>
			</#if>
		</@labeledSelectList>
		
		<@labeledSingleLineTextInput label="Destination path" name="path" binding="job.destinationFolder" attributes="maxlength='${job.pathMaxLength}'" />
		
	</fieldset>
	<#include "schedule.ftl">
	<fieldset>
		<legend>Processing</legend>
		
		<@labeledSingleLineTextInput label="Command before" name="commandbefore" binding="job.command" helpText="Execute command before processing." attributes="maxlength='${job.commandMaxLength}'" />
		
		<@labeledSelectList label="Meta-data processor" name="metadata" options=metadataOptions helpText="Choose the meta-data processor."; processor>
			<option value="${processor.class.name}" <#if job.metadata?? && processor.class.name=job.metadata >selected="selected"</#if> >${processor.name} - ${processor.description}</option>
		</@labeledSelectList>
		
				
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/parameterHandler2.js" />"> </script>
		
		<@labeledParamTableAndInput label="Meta-data parameters" baseId="paramMetadata" paramList=job.paramMetadata paramNameMaxLength="${job.paramNameMaxLength}" paramValueMaxLength="${job.paramValueMaxLength}" />
		
		<@labeledInput label="Transformation" forId="transformation">
				<@radio name="transform" value="notransformation" checked=(jobTransform?? || !jobTransform?has_content || "No transformation"==jobTransform) description="No transformation" />
			<#list transformOptions as transformOption>
				<@radio name="transform" value=transformOption.class.name checked=(job.transform?? && transformOption.class.name==job.transform) description=transformOption.description />
			</#list>
		</@labeledInput>
		
		<@labeledParamTableAndInput label="Tranformation parameters" baseId="paramTransform" paramList=job.paramTransform paramNameMaxLength="${job.paramNameMaxLength}" paramValueMaxLength="${job.paramValueMaxLength}" />
		
		<@labeledSingleLineTextInput label="Command after" name="commandafter" binding="job.commandAfter" helpText="Execute command after processing." attributes="maxlength='${job.commandMaxLength}'" />
		
	</fieldset>
	
	<fieldset>
		<legend>Options</legend>
		
		<@labeledInput label="Mode" forId="mode" helpText="What should Move2Alf do with the documents?">
			<@radios name="mode" options=[
				["WRITE", (!job.mode?? | job.mode=="WRITE"), "Write"],
				["DELETE", job.mode?? && job.mode=="DELETE", "Delete"],
				["LIST", job.mode?? && job.mode=="LIST", "List presence"]
			] />
		</@labeledInput>
		
		<span id="write-options" >

		<@labeledInput label="If content exists in destination" forId="writeOption" helpText="What should happen when the document already exists in the destination?">
			<@radios name="writeOption" options=[
			 ["SKIPANDREPORTFAILURE", (!job.writeOption?? | job.writeOption=="SKIPANDREPORTFAILURE"),"Skip document and log error"],
			 ["SKIPANDIGNORE", job.writeOption?? && job.writeOption=="SKIPANDIGNORE", "Skip document silently"],
			 ["OVERWRITE", job.writeOption?? && job.writeOption=="OVERWRITE", "Overwrite document"]
			 ] />
		</@labeledInput>
		</span>
		
		<span id="delete-options" class="hidden" >
		<@labeledInput label="If content does not exists in destination" forId="docNotExists" helpText="What should happen when the document does not exists in the destination?">
			<@radios name="docNotExist" options=[
			 ["SkipAndLog", true,"Skip document and log error"],
			 ["Skip", false, "Skip document silently"]
			 ] />
		</@labeledInput>
		</span>

		<span id="list-options" class="hidden" >
		<@unLabeledInput>
			<@checkboxWithText binding="job.MoveBeforeProc" label="Ignore path" />
		</@unLabeledInput>
		</span>
		
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/checkboxWithText.js" />"> </script>
		<@unLabeledInput>
			<@checkboxWithOption binding="job.moveBeforeProc" label="Move before processing to path" textboxValue=job.beforeProcPath attributes="maxlength='${job.pathMaxLength}'" />
			<@checkboxWithOption binding="job.moveAfterLoad" label="Move loaded files to path" textboxValue=job.afterLoadPath attributes="maxlength='${job.pathMaxLength}'" />
			<@checkboxWithOption binding="job.moveNotLoad" label="Move not loaded files to path" textboxValue=job.notLoadPath attributes="maxlength='${job.pathMaxLength}'" />
		</@unLabeledInput>
	</fieldset>

	<fieldset>
		<legend>Error reporting</legend>
		<@unLabeledInput helpText="Separate multiple e-mail addresses with commas">
			<@checkboxWithOption binding="job.sendNotification" label="Send notification e-mails on errors to" textboxValue=job.emailAddressError attributes="maxlength='${job.emailMaxLength}'" />
		</@unLabeledInput>
		<@unLabeledInput helpText="Separate multiple e-mail addresses with commas">
			<@checkboxWithOption binding="job.sendReport" label="Send load reporting e-mails to" textboxValue=job.emailAddressRep attributes="maxlength='${job.emailMaxLength}'" />
		</@unLabeledInput>
	</fieldset>

	<input class="btn btn-success" type="submit" value="Save" />
	<a class="btn btn-inverse" href="<@spring.url relativeUrl="/job/dashboard" />">Cancel</a>