<h1>${user.userName}'s Profile</h1>
<#assign activeMenu="My profile" />
<#include "header.ftl" />

<p>
Username: ${user.userName}
</p>
<p>
Role: ${role}
</p>
<a class="btn" href="<@spring.url relativeUrl="/user/${user.userName}/edit" />">Edit profile</a>

<#include "footer.ftl" />