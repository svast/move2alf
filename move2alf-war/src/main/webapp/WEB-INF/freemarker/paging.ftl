<div class="pagination pagination-centered">
<ul>
<#if (pagedListHolder.pageCount > 1)>
	<#if !pagedListHolder.firstPage>
		<li><a href="<@spring.url relativeUrl="${pagedLink}?action=list&p=${pagedListHolder.page-1}" />">&lt;</a></li>
	</#if>
	<#if (pagedListHolder.firstLinkedPage >0)>
		<li><a href="<@spring.url relativeUrl="${pagedLink}?action=list&p=0" />">1</a></li>
		<#if pagedListHolder.firstLinkedPage != 1>
		 	<li class="disabled"><a href="#">...</a></li> 
		</#if>
	</#if>
	<#list pagedListHolder.firstLinkedPage..pagedListHolder.lastLinkedPage as i>
		<#if pagedListHolder.page == i>
			<li class="active"><a href="<@spring.url relativeUrl="${pagedLink}?action=list&p=${i}" />">${i+1}</a></li>
		<#else>
			<li><a href="<@spring.url relativeUrl="${pagedLink}?action=list&p=${i}" />">${i+1}</a></li>
		</#if>
	</#list>
	<#if (pagedListHolder.lastLinkedPage < (pagedListHolder.pageCount-1))>
		<#if pagedListHolder.lastLinkedPage != (pagedListHolder.pageCount-2)>
			<li class="disabled"><a href="#">...</a></li> 
		</#if>
		<li><a href="<@spring.url relativeUrl="${pagedLink}?action=list&p=${pagedListHolder.pageCount-1}" />">${pagedListHolder.pageCount}</a></li> 
	</#if>
	<#if !pagedListHolder.lastPage>
		<li><a href="<@spring.url relativeUrl="${pagedLink}?action=list&p=${pagedListHolder.page+1}" />">&gt;</a></li>
	</#if>
</#if>
</ul>
</div>