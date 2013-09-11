<@labeledSingleLineTextInput label="Name" name="name" binding="destination.name" />
<@labeledSingleLineTextInput label="URL" name="destinationURL" binding="destination.destinationURL" />
<@labeledSingleLineTextInput label="Username" name="alfUser" binding="destination.alfUser" />
<@labeledSingleLineTextInput label="Password" name="alfPswd" binding="destination.alfPswd" />
<@labeledSingleLineTextInput label="Number of threads" name="nbrThreads" binding="destination.nbrThreads" />


<input class="btn btn-success" type="submit" value="Save" />
<a class="btn btn-inverse" href="<@spring.url relativeUrl="/destinations" />">Cancel</a>