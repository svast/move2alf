<@labeledSingleLineTextInput label="Name" name="name" binding="destination.destinationName" />
<@labeledInput label="Type" forId="destinationType" >

		<#list destinationOptions as destinationOption>
			<@radio name="destinationType" value=destinationOption.class.name checked=((destinationOption_index==0 && (destinationOptions?size==1 || !destination.destinationType??)) || destinationOption.class.name==destination.destinationType!"false") description=destinationOption.name />
		</#list>
</@labeledInput>
<@labeledSingleLineTextInput label="URL" name="destinationURL" binding="destination.destinationURL" />
<@labeledSingleLineTextInput label="Username" name="alfUser" binding="destination.alfUser" />
<@labeledSingleLineTextInput label="Password" name="alfPswd" binding="destination.alfPswd" />
<@labeledSingleLineTextInput label="Number of threads" name="nbrThreads" binding="destination.nbrThreads" />

<input class="btn btn-success" type="submit" value="Save" />
<a class="btn btn-inverse" href="<@spring.url relativeUrl="/destinations" />">Cancel</a>