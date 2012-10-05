<#import "spring.ftl" as spring />
<#macro myMacro param >param="${param}"; </#macro>
<@spring.formTextarea "job.description" />
<@myMacro param=job.description />
Name: ${job.name}; Description: ${job.description}