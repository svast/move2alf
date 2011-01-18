<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/dialog-header.jsp"%>

<head>
<script type="text/javascript">

//copyright 1999 Idocs, Inc. http://www.idocs.com
//Distribute this script freely but keep this notice in place
function numbersonly(myfield, e, dec)
{
	var key;
	var keychar;

	if (window.event)
		key = window.event.keyCode;
	else if (e)
		key = e.which;
	else
		return true;
	keychar = String.fromCharCode(key);

	//control keys
	if ((key==null) || (key==0) || (key==8) || 
 	(key==9) || (key==13) || (key==27) )
		return true;

	//numbers
	else if ((("0123456789").indexOf(keychar) > -1))
		return true;

	//decimal point jump
	else if (dec && (keychar == ".")){
		myfield.form.elements[dec].focus();
		return false;
	}
	else
		return false;
	}

</script>
</head>

<body>
<div class="span-24 last main">

<h2>Create new job</h2>

<form:form modelAttribute="job" method="post">

<p>Create new destination</p>
<table>
<tr>
<td>Name:</td>
<td><form:input path="destinationName" size="30" maxlength="30" /></td>
</tr>

<tr>
<td>Type:</td>
<td><form:radiobutton path="destinationType" value="Alfresco" />Alfresco</td>
</tr>
<tr>
<td></td>
<td><form:radiobutton path="destinationType" value="CMIS" />CMIS</td>
</tr>

<tr>
<td>URL:</td>
<td><form:input path="destinationURL" size="50" maxlength="40" /></td>
</tr>

<tr>
<td>Username:</td>
<td><form:input path="alfUser" size="30" maxlength="30" /></td>
</tr>

<tr>
<td>Password:</td>
<td><form:input path="alfPswd" size="30" maxlength="30" /></td>
</tr>

<tr>
<td>Number of threads:</td>
<td><form:input path="nbrThreads" size="15" value="5" onKeyPress="return numbersonly(this, event)"/></td>
</tr>

</table>
<br />

<div style="float:left"><a href="JavaScript:window.close()">Cancel</a></div>
<div style="float:right"><button type="button">Validate</button>	<input type="submit" value="Ok"/></div>
<br style="clear:both">
</form:form>
	
</div>
</body>

