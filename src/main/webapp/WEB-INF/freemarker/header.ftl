<#import "spring.ftl" as spring />
<!doctype html>
<html>
	<head>
		<link rel="stylesheet/less" href="<@spring.url relativeUrl="/styles/less/bootstrap.less" />" media="all" />
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/less.js" />"> </script>
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/jquery.js" />"> </script>
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/bootstrap-twipsy.js" />"> </script>
		<script type="text/javascript" src="<@spring.url relativeUrl="/js/bootstrap-popover.js" />"> </script>
		<link rel="stylesheet" href="<@spring.url relativeUrl="/styles/move2alf.css" />" />
		<title>Xenit Move2Alf</title>
	</head>
	<body>
		<img style="position:absolute; right:20px; top:0px; z-index:5" src="<@spring.url relativeUrl="/images/move2alf-logo.png"/>" alt="Move2Alf" />
		<div>
			<#include "menu.ftl">