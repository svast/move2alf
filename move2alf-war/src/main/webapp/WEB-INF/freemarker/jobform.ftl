	<fieldset>
		<legend>General</legend>
		<@labeledSingleLineTextInput label="Name" name="name" binding="job.name" attributes="maxlength='${job.jobNameMaxLength}'" />

		<@textArea label="Description" name="description" binding="job.description" attributes="maxlength='${job.jobDescriptionMaxLength}'" />

	</fieldset>
	<fieldset>
		<legend>Input source</legend>
		<script>
			$(function() {
				var inputSource = $('[name="inputSource"]:checked').val();
				$(".inputOptions").each(function() {
					if ($(this).attr('id') != "inputOptions-" + inputSource) {
						$(this).hide();
					}
				});
				$('[name="inputSource"]').change(function() {
					$(".inputOptions").slideToggle();
				});

                $(".importOptions").each(function() {
                    if ($(this).attr('id') != "importOptions-" + inputSource) {
                        $(this).hide();
                    }
                });
                $('[name="inputSource"]').change(function() {
                    $(".importOptions").slideToggle();
                });
			});
		</script>
		<@labeledInput label="Input source" forId="inputSource">
			<@radios name="inputSource" options=[
				["FILESYSTEM", (!job.inputSource?? | job.inputSource=="FILESYSTEM"), "File system"],
				["CMIS", (job.inputSource?? && job.inputSource=="CMIS"), "CMIS repository"]
			] />
		</@labeledInput>

		<div id="inputOptions-FILESYSTEM" class="inputOptions">
			<script type="text/javascript" src="<@spring.url relativeUrl="/js/inputPathHandler2.js" />"></script>
			<@labeledInput label="Input paths" forId="inputTable" helpText="You can add multiple paths. Press enter or click the save button to confirm a path.">
					<table id="inputPathTable" name="inputFolders" class="small inputTable table-striped">
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
		</div>

		<div id="inputOptions-CMIS" class="inputOptions">
		<@labeledSingleLineTextInput label="CMIS URL" name="cmisURL" binding="job.cmisURL"/>
		<@labeledSingleLineTextInput label="Username" name="cmisUsername" binding="job.cmisUsername"/>
		<@labeledSingleLineTextInput label="Password" name="cmisPassword" binding="job.cmisPassword"/>
		</div>
	</fieldset>

	<fieldset>
		<legend>Import</legend>

        <div id="importOptions-FILESYSTEM" class="importOptions">
        <@labeledSingleLineTextInput label="Extension" name="extension" binding="job.extension" attributes="maxlength='${job.extensionMaxLength}'" />
        </div>

        <div id="importOptions-CMIS" class="importOptions">
        <@labeledSingleLineTextInput label="CMIS query" name="cmisQuery" binding="job.cmisQuery" />
        </div>

		<@labeledSelectList label="Destination server" name="dest" options=destinations; destination>
				<option value="${destination.id}" <#if (job.dest?? && job.dest==destination.id)>selected="selected"</#if>>${destination.name}</option>
		</@labeledSelectList>

		<@labeledSingleLineTextInput label="Destination path" name="path" binding="job.destinationFolder" attributes="maxlength='${job.pathMaxLength}'" />

	</fieldset>
	<#include "schedule.ftl">
	<fieldset>
		<legend>Processing</legend>

		<@labeledSingleLineTextInput label="Command before" name="commandbefore" binding="job.command" helpText="Execute command before processing." attributes="maxlength='${job.commandMaxLength}'" />

		<@labeledSelectList label="Metadata processor" name="metadata" options=metadataOptions helpText="Choose the metadata processor."; processor>
			<option value="${processor.classId}" <#if job.metadata?? && processor.classId=job.metadata >selected="selected"</#if> >${processor.classId} - ${processor.description}</option>
		</@labeledSelectList>


		<script type="text/javascript" src="<@spring.url relativeUrl="/js/parameterHandler2.js" />"> </script>

		<@labeledParamTableAndInput label="Metadata parameters" baseId="paramMetadata" paramList=job.paramMetadata paramNameMaxLength="${job.paramNameMaxLength}" paramValueMaxLength="${job.paramValueMaxLength}" />

		<@labeledInput label="Transformation" forId="transformation">
				<@radio name="transform" value="notransformation" checked=(jobTransform?? || !jobTransform?has_content || "No transformation"==jobTransform) description="No transformation" />
			<#list transformOptions as transformOption>
				<@radio name="transform" value=transformOption.classId checked=(job.transform?? && transformOption.classId==job.transform) description=transformOption.classId+" - "+transformOption.description />
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

		<span id="WRITE-options" class="options<#if job.mode?? && job.mode!="WRITE" > hidden</#if>">

		<@labeledInput label="If content exists in destination" forId="writeOption" helpText="What should happen when the document already exists in the destination?">
			<@radios name="writeOption" options=[
			 ["SKIPANDREPORTFAILED", (!job.writeOption?? | job.writeOption=="SKIPANDREPORTFAILED"),"Skip document and log error"],
			 ["SKIPANDIGNORE", job.writeOption?? && job.writeOption=="SKIPANDIGNORE", "Skip document silently"],
			 ["OVERWRITE", job.writeOption?? && job.writeOption=="OVERWRITE", "Overwrite document"]
			 ] />
		</@labeledInput>
		</span>
		
		<span id="DELETE-options" class="options<#if !job.mode?? | job.mode!="DELETE" > hidden</#if>">
		<@labeledInput label="If content does not exists in destination" forId="docNotExists" helpText="What should happen when the document does not exists in the destination?">
			<@radios name="deleteOption" options=[
			 ["SKIPANDIGNORE", !job.deleteOption?? | job.deleteOption=="SKIPANDIGNORE", "Skip document silently"]
			 ] />
		</@labeledInput>
		</span>

		<span id="LIST-options" class="options<#if !job.mode?? | job.mode!="LIST" > hidden</#if>">
		<@unLabeledInput>
			<@checkboxWithText binding="job.listIgnorePath" label="Ignore path" />
		</@unLabeledInput>
		</span>

		<script>
			$('input:radio[name="mode"]').each(function(){
				$( this ).change(function(){
					$('input:radio[name="mode"]').each(function(){
						if($( this ).attr('checked') != "undefined" && $( this ).attr('checked') == "checked"){
							$('span#'+$( this ).val()+'-options').removeClass('hidden');
						}
						else{
							$('span#'+$( this ).val()+'-options').addClass('hidden');
						}
					});
				});
			});
		</script>

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
    <#if job.name??>
    <a class="btn btn-danger" onclick="deleteJob('${job.id}')" >Delete Job</a>
    <script>
        function deleteJob(id){
            if(confirm("Are you sure you want to delete this job?")){
                window.location.href = "<@spring.url relativeUrl="/job/" />"+id+"/delete";
            }
        }
    </script>
    </#if>