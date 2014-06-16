<@labeledSingleLineTextInput label="Name" name="name" binding="destination.name" />
<@labeledInput label="Type" forId="type" >

		<#list destinationOptions?keys as destinationOption>
			<@radio name="type" value=destinationOption checked=((destinationOption_index==0 && (destinationOptions?size==1 || !destination.type??)) || destinationOption==destination.classId!"false") description=destinationOption />
		</#list>
</@labeledInput>
<#list destinationOptions?values as type>
    <#include type.viewName />
</#list>

<input class="btn btn-success" type="submit" value="Save" />
<a class="btn btn-inverse" href="<@spring.url relativeUrl="/destinations" />">Cancel</a>