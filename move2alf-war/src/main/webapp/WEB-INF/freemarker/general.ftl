<#import "spring.ftl" as spring />

<#macro html>
	<?xml version="1.0"?>
	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
	<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
		<#nested />
	</html>
</#macro>

<#macro head>
	<head>
        <meta http-equiv='Content-Type' content='Type=text/html; charset=utf-8'>
		<link rel="stylesheet" href="<@spring.url relativeUrl="/styles/bootstrap.min.css" />" />
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/jquery.js" />"> </script>
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/jquery-ui.js" />"> </script>
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/jquery.ui.timepicker.js" />"> </script>
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/bootstrap-twipsy.js" />"> </script>
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/bootstrap-tooltip.js" />"> </script>
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/bootstrap-popover.js" />"> </script>
        <script type="text/javascript" src="<@spring.url relativeUrl="/js/bootstrap-scrollspy.js" />"> </script>
		<script type="text/javascript">
			var basePath = "<@spring.url relativeUrl="" />";
		</script>
		<link rel="stylesheet" href="<@spring.url relativeUrl="/styles/move2alf.css" />" />
		<link rel="stylesheet" href="<@spring.url relativeUrl="/styles/tablesorter.css" />" />
		<link rel="stylesheet" href="<@spring.url relativeUrl="/styles/jquery-ui.css" />" />
		<link rel="stylesheet" href="<@spring.url relativeUrl="/styles/jquery.ui.timepicker.css" />" />
		<#nested />
		<title>Xenit Move2Alf</title>
	</head>
</#macro>

<#macro bodyMenu title>
<body>
		<h1>${title}</h1>
		<div class="wrapper">
			<img style="position:absolute; right:20px; top:0px; z-index:5" src="<@spring.url relativeUrl="/images/move2alf-logo.png"/>" alt="Move2Alf" />		
			<#include "menu.ftl" />
			<#nested>
		</div>
    	
</body>
</#macro>