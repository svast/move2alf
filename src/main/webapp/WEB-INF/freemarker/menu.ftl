<#if role?? && role=="SYSTEM_ADMIN">
	<#assign menu = [ 
					["Home", "/"],
					["Manage destinations", "/destinations"],
					["Manage users", "/users"],
					["My profile", "/user/profile"],
					["Logout", "/j_spring_security_logout"]
				] />
<#elseif role?? && role=="JOB_ADMIN">
	<#assign menu = [ 
					["Home", "/"],
					["Manage destinations", "/destinations"],
					["My profile", "/user/profile"],
					["Logout", "/j_spring_security_logout"]
				] />
<#elseif role??>
	<#assign menu = [ 
					["Home", "/"],
					["My profile", "/user/profile"],
					["Logout", "/j_spring_security_logout"]
				] />
				
<#else>
	<#assign menu = [ 
					["Home", "/"]
				] />
</#if>



<ul style="" class="nav nav-tabs">
		<#list menu as item >
		<li<#if activeMenu! == item[0]> class="active"</#if>><a href="<@spring.url relativeUrl=item[1] />">${item[0]}</a></li>
		</#list>
</ul>