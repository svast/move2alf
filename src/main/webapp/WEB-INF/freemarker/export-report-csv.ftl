Name;${job.name}
Description;${job.description}
Start time;${cycle.startDateTime?datetime}
End time;${cycle.endDateTime?datetime}
Duration;${duration}
Status;${cycle.state.displayName}
Nr. of documents;${documentListSize}
Docs / s;${docsPerSecond}

List of imported documents

Name;Processed date and time;Status
<#list processedDocuments as document>
${document.name};${document.processedDateTime?datetime};${document.status.displayName}<#list document.processedDocumentParameterSet as docParameter>;${docParameter.name}: ${docParameter.value}</#list>
</#list>