<!doctype html>
<html>
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
<meta charset="utf-8">
<link rel="stylesheet" href="/styles/blueprint/screen.css"
	type="text/css" media="screen, projection">
<link rel="stylesheet" href="/styles/blueprint/print.css"
	type="text/css" media="print">
<!--[if lt IE 8]>
  <link rel="stylesheet" href="/styles/blueprint/ie.css" type="text/css" media="screen, projection">
<![endif]-->
<link rel="stylesheet"
	href="<spring:url value="/styles/main.css" htmlEscape="true" />"
	type="text/css" />
	
<script type="text/javascript" src="/js/field-int.js"></script>
<title>XeniT Move2Alf</title>
</head>
<body>
