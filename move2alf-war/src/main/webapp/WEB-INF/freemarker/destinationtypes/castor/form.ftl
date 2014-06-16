<@labeledSingleLineTextInput label="Name" name="name" binding="destination.name" />
<@labeledSingleLineTextInput label="Cluster name" name="clusterName" helpText="Name that alfresco uses in the content url." binding="destination.clusterName" />
<@labeledSingleLineTextInput label="Node 1" name="node1" binding="destination.node1" />
<@labeledSingleLineTextInput label="Node 2" name="node2" binding="destination.node2" />
<@labeledSingleLineTextInput label="Node 3" name="node3" binding="destination.node3" />
<@labeledSingleLineTextInput label="Number of replica's" name="reps" binding="destination.reps" />
<@labeledInput label="Deletable" forId="deletable" helpText="Should the document be deletable in Castor?">
    <@formCheckbox path="destination.deletable" />
</@labeledInput>
<@labeledSingleLineTextInput label="Number of threads" name="nbrThreads" binding="destination.nbrThreads" />

<input class="btn btn-success" type="submit" value="Save" />
<a class="btn btn-inverse" href="<@spring.url relativeUrl="/destinations" />">Cancel</a>