<#macro labeledInput label forId helpText=''>
<div class="control-group">
	<label class="control-label" for="${forId}">${label}:</label>
	<div class="controls">
		<#nested>
		<#if helpText?has_content>
			<p class="help-block">${helpText}</p>
		</#if>
	</div>
</div>
</#macro>

<#macro unLabeledInput helpText=''>
<div class="control-group">
	<div class="controls">
		<#nested>
		<#if helpText??>
			<p class="help-block">${helpText}</p>
		</#if>
	</div>
</div>
</#macro>

<#macro textArea label name binding helpText='' attributes=''>
<@labeledInput label=label forId=name helpText=helpText>
	<@spring.formTextarea binding, "class=\"input-xlarge\" rows=\"5\" " + attributes />
</@labeledInput>
</#macro>

<#macro labeledSingleLineTextInput label name binding helpText='' attributes=''>
<@labeledInput label=label forId=name helpText=helpText>
	<@spring.formInput binding, "class=\"input-xlarge\" " + attributes />
</@labeledInput>
</#macro>

<#macro labeledSingleLinePasswordInput label name binding helpText=''>
<@labeledInput label=label forId=name helpText=helpText>
	<@spring.formPasswordInput binding, "class=\"input-xlarge\"" />
</@labeledInput>
</#macro>

<#macro labeledSelectList label name options helpText=''>
<@labeledInput label=label forId=name helpText=helpText>
	<select style="width:auto;" name="${name}" id="${name}">
		<#list options as option>
			<#nested option>
		</#list>
	</select>
</@labeledInput>
</#macro>

<#macro labeledParamTableAndInput label baseId paramNameMaxLength paramValueMaxLength paramList='' >
<#assign tableId=baseId+"Table" />
<@labeledInput label=label forId=tableId>

				<table id="${tableId}" name="${baseId}" class="small inputTable">
					<thead>
						<tr>
							<th>ID</th>
							<th>Name</th>
							<th>Value</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
					<#assign lastIndex = 0>
					<#if paramList?has_content>
					<#list paramList as paramS>
						<#assign param=(paramS?split('|')) />
						<#assign rowIndex=baseId+"_"+paramS_index />
						<tr id="${rowIndex}">
							<td>${paramS_index+1}</td>
							<td>${param[0]}</td>
							<td>${param[1]}
								<input name="${baseId}" type="hidden" value="${param[0]}|${param[1]}" />
							</td>
							<td><img class="clickable" onclick="$('#${rowIndex}').remove()" src="<@spring.url relativeUrl="/images/delete-icon.png"/>" alt="delete" /></td>
						</tr>
					</#list>
					</#if>				
						<tr>
							<td><img src="<@spring.url relativeUrl="/images/new.png"/>" alt="new" /></td>
							<#assign nameId=baseId+"Name" />
							<#assign valueId=baseId+"Value" />
							<td><input id="${nameId}" name="${nameId}" type="text" style="width:100%" maxlength="${paramNameMaxLength}" /></td>
							<td><input id="${valueId}" name="${valueId}" type="text" style="width:100%" maxlength="${paramValueMaxLength}" /></td>
							<script>
								$('#${valueId}').bind('keypress', function(e) {
									if(e.keyCode==13){
										e.preventDefault();
										addParameter('${baseId}');
										$("#${nameId}").select();
									}
								});
								$('#${valueId}').closest("form").submit(function(){
									addParameter('${baseId}',true);
								});
							</script>
							<td><img class="clickable" onclick="addParameter('${baseId}')" src="<@spring.url relativeUrl="/images/save-icon.png"/>" alt="save" /></td>
						</tr>
					</tbody>
				</table>
</@labeledInput>

</#macro>

<#macro radio name value checked description>
	<label class="radio">
		<#if checked>
			<input type="radio" name="${name}" checked="true" value="${value}" />
		<#else>
			<input type="radio" name="${name}" value="${value}" />
		</#if>
		${description}
	</label>
</#macro>

<#macro radios name options>
	<#list options as option>
		<@radio name=name value=option[0] checked=option[1] description=option[2] />
	</#list>
</#macro>

<#macro formCheckbox path attributes="">
    <@spring.bind path />
    <input type="checkbox" id="${spring.status.expression}" value="true" name="${spring.status.expression}"
           <#if spring.status.value?? && spring.status.value?string=="true">checked="true"</#if>
    ${attributes}
    <@spring.closeTag/>
</#macro>

<#macro checkboxWithOption binding label checked=false textboxValue='' attributes='' >
<#assign name=binding?split('.')?last />
<div class="line" id="<#--{name}-->-container">
	<label class="checkbox">
		<#-- <input id="${name}" name="${name}" value="true" <#if checked>checked="checked" </#if>type="checkbox" /> ${label}: -->
		<@formCheckbox binding />${label}:
	</label>
	<#-- <input type="text" id="${name}Text" name="${name}Text" value="${textboxValue!}"<#if !checked> disabled="disabled"</#if> /> -->
	<@spring.formInput (binding+"Text"), attributes />
</div>

<script>
$(function(){checkboxChanged("${name}");});
$("#${name}").change(function(){
	checkboxChanged("${name}");
});
</script>

</#macro>