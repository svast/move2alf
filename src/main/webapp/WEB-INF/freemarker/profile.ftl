<#assign activeMenu="My profile" />
<#include "general.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="Profile" >

<p>
Username: ${user.userName}
</p>
<p>
Role: ${role}
</p>
<a class="btn" href="<@spring.url relativeUrl="/user/${user.userName}/edit" />">Edit profile</a>

<#include "footer.ftl" />

</@bodyMenu>
</@html>