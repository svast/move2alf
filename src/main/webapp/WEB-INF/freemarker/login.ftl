<#include "general.ftl" />

<@html>
<@head>
</@head>
<@bodyMenu title="Login" >
<#if failed>
<div class="alert alert-error">
    	The credentials you supplied were not valid!
</div>
</#if>

<form name='f' action='<@spring.url relativeUrl="/j_spring_security_check"  />' method='POST'> 
 <table> 
    <tr><td>User:</td><td><input type='text' name='j_username' value=''></td></tr> 
    <tr><td>Password:</td><td><input type='password' name='j_password'/></td></tr> 
    <tr><td><input type='checkbox' name='_spring_security_remember_me'/></td><td>Remember me on this computer.</td></tr> 
    <tr><td colspan='2'><input name="submit" type="submit"/></td></tr> 
  </table> 
</form>

</div>

</@bodyMenu>
</@html>