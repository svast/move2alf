<#assign menu = [ 
					["Home", "/"],
					["Manage destinations", "/destinations"],
					["Manage users", "/users"],
					["My profile", "/user/profile"],
					["Logout", "/j_spring_security_logout"]
				] />

<ul style="" class="tabs">
		<#list menu as item >
		<li<#if activeMenu! == item[0]> class="active"</#if>><a href="<@spring.url relativeUrl=item[1] />">${item[0]}</a></li>
		</#list>
</ul>