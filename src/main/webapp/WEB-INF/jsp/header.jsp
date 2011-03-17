
<!doctype html>
<html>
<head>

		<script type="text/javascript" src="<c:url value="/resources/dojo/dojo.js" />"></script>
        <script type="text/javascript" src="<c:url value="/resources/spring/Spring.js" />"></script>
        <script type="text/javascript" src="<c:url value="/resources/spring/Spring-Dojo.js" />"></script>
         <link type="text/css" rel="stylesheet" href="<c:url value="/resources/dijit/themes/tundra/tundra.css" />" /> 

<script type="text/javascript">

function putFocus(formInst, elementInst) {
    if (document.forms.length > 0) {
        try{
      		document.forms[formInst].elements[elementInst].focus();
        }catch(e){
        }
    }
}

function showInput(){
	document.getElementById('destination').style.display='inline';
}

function addSchedule(){
	document.getElementById('scheduleForm').style.display='block';
	document.getElementById('addScheduleButton').style.display='none';
}

function cancelSchedule(){
	document.getElementById('scheduleForm').style.display='none';
	document.getElementById('addScheduleButton').style.display='block';
}

function confirmSchedule(){
	document.getElementById('scheduleForm').style.display='none';
	document.getElementById('addScheduleButton').style.display='block';
}

function scheduleBox(number){
	switch(number){
	case 0: 
		document.getElementById('sDate').style.display='inline';
		document.getElementById('sTime').style.display='inline';
		document.getElementById('hourly').style.display='none';
		document.getElementById('daily').style.display='none';
		document.getElementById('weeklyDay').style.display='none';
		document.getElementById('weeklyTime').style.display='none';
		document.getElementById('advanced').style.display='none';
		break;
	case 1:
		document.getElementById('sDate').style.display='none';
		document.getElementById('sTime').style.display='none';
		document.getElementById('hourly').style.display='block';
		document.getElementById('daily').style.display='none';
		document.getElementById('weeklyDay').style.display='none';
		document.getElementById('weeklyTime').style.display='none';
		document.getElementById('advanced').style.display='none';
		break;
	case 2:
		document.getElementById('sDate').style.display='none';
		document.getElementById('sTime').style.display='none';
		document.getElementById('hourly').style.display='none';
		document.getElementById('daily').style.display='block';
		document.getElementById('weeklyDay').style.display='none';
		document.getElementById('weeklyTime').style.display='none';
		document.getElementById('advanced').style.display='none';
		break;
	case 3:
		document.getElementById('sDate').style.display='none';
		document.getElementById('sTime').style.display='none';
		document.getElementById('hourly').style.display='none';
		document.getElementById('daily').style.display='none';
		document.getElementById('weeklyDay').style.display='inline';
		document.getElementById('weeklyTime').style.display='inline';
		document.getElementById('advanced').style.display='none';
		break;
	case 4:
		document.getElementById('sDate').style.display='none';
		document.getElementById('sTime').style.display='none';
		document.getElementById('hourly').style.display='none';
		document.getElementById('daily').style.display='none';
		document.getElementById('weeklyDay').style.display='none';
		document.getElementById('weeklyTime').style.display='none';
		document.getElementById('advanced').style.display='block';
		break;
	default:
	}
}


function addRowToSchedule(form){
	var date ='';
	var time = '';
	var day = '';
	var cronJob = '';
	var period='';
	var minutes='';

	for (var counter = 0; counter < 5; counter++)
    {
		if(form.runFrequency[counter].checked==true){
			
			switch(counter){
			case 0:
					date = document.getElementById('singleDate').value;
					time = document.getElementById('singleTime').value;
					period = "Single run";
					break;
			case 1:
					minutes = document.getElementById('hourTime').value;
					period = "Hourly";
					break;
			case 2:
					time = document.getElementById('dayTime').value;
					period = "Daily";
					break;
			case 3:
					day = document.getElementById('weekDay').value;
					time = document.getElementById('weekTime').value;
					period = "Weekly";
					break;
			case 4:
					cronJob = document.getElementById('cronJob').value;
					period = "Cron job";
					break;
			default:
					break;
			}
		}		
    }
    
	if(period!="Cron job"){
		cronJob = getCronJob(time, day, date, period, minutes);
	}
	
	  var tbl = document.getElementById('tblSample');
	  var lastRow = tbl.rows.length;
	
	  var iteration = lastRow+1;
	  var row = tbl.insertRow(lastRow);
	  
	  var cellFirst = row.insertCell(0);
	
	  var rowNumber = document.createElement('div');
	   rowNumber.innerHTML=iteration;
	   rowNumber.id="rowNumber"+iteration;
	  
	  cellFirst.appendChild(rowNumber);
	  
	  var cellSecond = row.insertCell(1);
	  var displayString = '';
	
	  if(period=="Cron job")
		  displayString = period;
	  else{
		  if(period!='')
			  displayString = period +' - ';
		  if(day!='')
		  	  displayString = displayString + day +' - ';
		  if(date!='')
			  displayString = displayString + '' + date + ' - ';
		  if(time!='')
			  displayString = displayString + '' + time;
		  if(minutes!=''){
			  if(minutes.length==1)
			 	  displayString = displayString + '*:0' + minutes;
			  else
				  displayString = displayString + '*:' + minutes;
		  }
	  }
	  
	  dayTime = document.createTextNode(displayString);
	  cellSecond.appendChild(dayTime);  
	  
	  var cellThird = row.insertCell(2);
	  var cronJobNode = document.createTextNode(cronJob);
	  cellThird.appendChild(cronJobNode);
	
	 
	  var cellFourth = row.insertCell(3);
	 
	  var sp = document.createElement('div');
	 
	  sp.className = 'pointer';
	  sp.innerHTML='remove';
	  var id = 'remove'+iteration;
	  sp.id=id;
	  sp.setAttribute('onclick', 'removeRowFromSchedule('+iteration+')');
	  cellFourth.appendChild(sp);  
	  setCronInForm(cronJob);

}

function setCronInForm(cronJob){
	var tblCron = document.getElementById('tblCron');
	
	  var lastRow = tblCron.rows.length;

	  var iteration = lastRow+1;
	  var row = tblCron.insertRow(lastRow);
	  
	  var cellFirst = row.insertCell(0);

		try{
			var fi=document.createElement('<input name=\"cron\" type=\"checkbox\" checked>');
			var id = 'cron'+iteration;
			 fi.id=id;
			 fi.value=cronJob;
		}
		catch(e){
			 var fi = document.createElement('input');
			 var id = 'cron'+iteration;
			 fi.id=id;
			 fi.type='checkbox';
			 fi.name='cron';
			 fi.value=cronJob;
			 fi.checked=true;
		}
	  cellFirst.appendChild(fi);
}

function getCronJob(time, day, date, period, mins){
	var seconds=0;
	var minutes=0;
	var hours=0;
	var dom='*';
	var month='*';
	var dow='*';
	var year='';

	if(mins!=''){
		var firstValue=mins.substring(0,1);
		var secondValue=mins.substring(1,2);
		if(mins.length==2 && firstValue=="0")
			minutes=secondValue;
		else
			minutes=mins;
	}
	
	if(time!=''){
		var timeSplit = time.split(":");
			var firstValue=timeSplit[0].substring(0,1);
			var secondValue=timeSplit[0].substring(1,2);
			if(timeSplit[0].length==2 && firstValue=="0")
				hours=secondValue;
			else
				hours=timeSplit[0];
			
			var firstValue=timeSplit[1].substring(0,1);
			var secondValue=timeSplit[1].substring(1,2);
			if(timeSplit[1].length==2 && firstValue=="0")
				minutes=secondValue;
			else
				minutes=timeSplit[1];
	}

	if(date!=''){
		var dateSplit = date.split("/");
			var firstValue=dateSplit[0].substring(0,1);
			var secondValue=dateSplit[0].substring(1,2);
			if(dateSplit[0].length==2 && firstValue=="0")
				dom=secondValue;
			else
				dom=dateSplit[0];

			var firstValue=dateSplit[1].substring(0,1);
			var secondValue=dateSplit[1].substring(1,2);
			if(dateSplit[1].length==2 && firstValue=="0")
				month=secondValue;
			else
				month=dateSplit[1];
			
			year=dateSplit[2];
	}

	if(day!=''){
			dow=day.substring(0,3);
	}

	switch(period){
	case "Single run":
		dow='?';
		break;
	case "Hourly":
		hours='*';
		dow='?';
		break;
	case "Daily":
		dow='?';
		break;
	case "Weekly":
		dom='?';
		break;
	default:
		dow='?';
		break;
	}

	var cronJob = seconds+' '+minutes+' '+hours+' '+dom+' '+month+' '+dow+' '+year;
	
	return cronJob;
}

function removeRowFromSchedule(row){

  var tbl = document.getElementById('tblSample');
  tbl.deleteRow(row-1);
  adjustRows(tbl,row);

  var tblCron = document.getElementById('tblCron');
  tblCron.deleteRow(row-1);
}

function adjustRows(tbl,row){
	var rowCount = tbl.rows.length;
	for(row; row<=rowCount; row++){
		var j=row+1;
		document.getElementById("rowNumber"+j).setAttribute("id","rowNumber"+row);
		document.getElementById("rowNumber"+row).innerHTML=row;
		document.getElementById("remove"+j).setAttribute("id","remove"+row);
		document.getElementById("remove"+row).setAttribute('onclick', 'removeRowFromSchedule('+row+')');
	}
}

function addDestination(){
	document.getElementById('destinationForm').style.display='block';
	document.getElementById('addDestinationButton').style.display='none';
}

function confirmDestination(){
	document.getElementById('destinationForm').style.display='none';
	document.getElementById('addDestinationButton').style.display='block';

	document.getElementById("destinationNameError").style.display='none';
	document.getElementById("destinationURLError").style.display='none';
	document.getElementById("alfUserError").style.display='none';
	document.getElementById("alfPswdError").style.display='none';
	document.getElementById("nbrThreadsError").style.display='none';
}

function cancelDestination(){
	document.getElementById('destinationForm').style.display='none';
	document.getElementById('addDestinationButton').style.display='block';
}

function addRowToDestination(form){
	
	  var tbl = document.getElementById('tblDestination');
	  var lastRow = tbl.rows.length;

	  var iteration = lastRow+1;
	  var row = tbl.insertRow(lastRow);

	  var cellFirst = row.insertCell(0);


	  var len = form.destinationType.length;
		var destType="";
		for(var i=0; i<len; i++){
			if(form.destinationType[i].checked){
				destType=form.destinationType[i].value;
			}
		}
		
		if(document.createJob || document.editJob){
			try{
				var dest = document.createElement("<input type='radio' name='dest' CHECKED />");
				 var id = 'dest'+iteration;
				 dest.id=id;
			}catch(e){
				var dest = document.createElement('input');
				 var id = 'dest'+iteration;
				 dest.id=id;
				 dest.type='radio';
				 dest.name='dest';
				 dest.checked=true;
			}
		}
		else{
			var dest = document.createElement('div');
		}
	 
		var value = form.destinationName.value+"|"+form.destinationURL.value+"|"+form.alfUser.value+"|"+form.alfPswd.value+"|"+document.getElementById('nbrThreads').value+"|"+destType;
	 	dest.value=value;

	 	var text=document.createTextNode(document.getElementById('destinationName').value+" - "+document.getElementById('destinationURL').value);
	
		cellFirst.appendChild(dest);
		cellFirst.appendChild(text);

		setDestInForm(value);

		if(document.getElementById('noDestinations')){
	  		document.getElementById('noDestinations').style.display='none';
		}

 }

function setDestInForm(value){
	var tblDestForm = document.getElementById('tblDestForm');
	var lastRow = tblDestForm.rows.length;

	  var iteration = lastRow+1;
	  var row = tblDestForm.insertRow(lastRow);
	  
	  var cellFirst = row.insertCell(0);

	  try{
			var fi = document.createElement("<input type='checkbox' name='sourceSink' CHECKED />");
			var id = 'sourceSink'+iteration;
			fi.id=id;
			fi.value=value;
		}catch(e){
			var fi = document.createElement('input');
			var id = 'sourceSink'+iteration;
			fi.id=id;
			fi.type='checkbox';
			fi.name='sourceSink';
			fi.value=value;
			fi.checked=true;
		}
	  cellFirst.appendChild(fi);
}

function noDestNeeded(){
	document.getElementById('noDestinations').style.display='none';
	document.getElementById('destError').style.display='none';
}

//password validation 
function comparePasswords(form){
	if(form.newPassword.value != form.newPasswordRetype.value){
		window.alert("You must type the same new password twice.");
		return false;
	}

	confirmDestination();
	addRowToDestination(form);
	
}

function jobValidation(form){
	var validated=true;
	
	var name=form.name.value;
	if(""==name || null==name || "null"==name || "undefined"==name){
		document.getElementById("nameError").style.display='block';
		validated=false;
	}
	
	var description=form.description.value;
	if(""==description || null==description || "null"==description || "undefined"==description){
		document.getElementById("descriptionError").style.display='block';
		validated=false;
	}
	
	var inputFolder=form.inputFolder.value;
	if(""==inputFolder || null==inputFolder || "null"==inputFolder || "undefined"==inputFolder){
		document.getElementById("inputFolderError").style.display='block';
		validated=false;
	}
	
	var destinationFolder=form.destinationFolder.value;
	if(""==destinationFolder || null==destinationFolder || "null"==destinationFolder || "undefined"==destinationFolder){
		document.getElementById("destinationFolderError").style.display='block';
		validated=false;
	}

	if(!document.getElementById('dest1')){
		document.getElementById("destError").style.display='block';
		validated=false;
	}

	if(!document.getElementById('rowNumber1')){
		document.getElementById("cronError").style.display='block';
		validated=false;
	}
		return validated;
}

function createDestinationsValidation(form){
	var validated = true;

	if(!document.getElementById('sourceSink1')){
		document.getElementById("destError").style.display='block';
		validated=false;
	}

	return validated;
}

function destinationValidation(form){
	var validated=true;
	
	var destName = document.getElementById('destinationName').value;
	if(null==destName || 'undefined'==destName || ''==destName || 'null'==destName){
		document.getElementById("destinationNameError").style.display='block';
		validated=false;
	}

	var destURL = document.getElementById('destinationURL').value;
	if(null==destURL || 'undefined'==destURL || ''==destURL || 'null'==destURL){
		document.getElementById("destinationURLError").style.display='block';
		validated=false;
	}
	
	var alfUser = document.getElementById('alfUser').value;
	if(null==alfUser || 'undefined'==alfUser || ''==alfUser || 'null'==alfUser){
		document.getElementById("alfUserError").style.display='block';
		validated=false;
	}
	
	var alfPswd = document.getElementById('alfPswd').value;
	if(null==alfPswd || 'undefined'==alfPswd || ''==alfPswd || 'null'==alfPswd){
		document.getElementById("alfPswdError").style.display='block';
		validated=false;
	}

	var nbrThreads = document.getElementById('nbrThreads').value;
	if(null==nbrThreads || 'undefined'==nbrThreads || ''==nbrThreads || 'null'==nbrThreads || nbrThreads != parseInt(nbrThreads)){
		document.getElementById("nbrThreadsError").style.display='block';
		validated=false;
	}

	if(validated==true){
		confirmDestination(); 
		addRowToDestination(form);
	}

	return validated
}

function addUserValidation(form){
	var validated=true;

	var userName = form.userName.value;
	if(null==userName || 'undefined'==userName || ''==userName || 'null'==userName){
		document.getElementById("userNameError").style.display='block';
		validated=false;
	}

	var password = form.password.value;
	if(null==password || 'undefined'==password || ''==password || 'null'==password){
		document.getElementById("passwordError").style.display='block';
		validated=false;
	}

	return validated;
}

function editPasswordValidation(form){
	var validated=true;

	var oldPassword = form.oldPassword.value;
	if(null==oldPassword || 'undefined'==oldPassword || ''==oldPassword || 'null'==oldPassword){
		document.getElementById("oldPasswordError").style.display='block';
		validated=false;
	}

	var newPassword = form.newPassword.value;
	if(null==newPassword || 'undefined'==newPassword || ''==newPassword || 'null'==newPassword){
		document.getElementById("newPasswordError").style.display='block';
		validated=false;
	}

	var newPasswordRetype = form.newPasswordRetype.value;
	if(null==newPasswordRetype || 'undefined'==newPasswordRetype || ''==newPasswordRetype || 'null'==newPasswordRetype){
		document.getElementById("newPasswordRetypeError").style.display='block';
		validated=false;
	}

	if(validated ==true){
	validated=comparePasswords(form);
	}
	
	return validated;
}

function editRoleValidation(form){
	var validated=true;

	var oldPassword = form.oldPassword.value;
	if(null==oldPassword || 'undefined'==oldPassword || ''==oldPassword || 'null'==oldPassword){
		document.getElementById("oldPasswordError").style.display='block';
		validated=false;
	}

	return validated;
}


</script>
<meta charset="utf-8">
<!-- <link rel="stylesheet" href="/styles/blueprint/screen.css"
	type="text/css" media="screen, projection">
<link rel="stylesheet" href="/styles/blueprint/print.css"
	type="text/css" media="print"> -->
<!--[if lt IE 8]>
  <link rel="stylesheet" href="/styles/blueprint/ie.css" type="text/css" media="screen, projection">
<![endif]-->
<link rel="stylesheet"
	href="<spring:url value="/styles/main.css" htmlEscape="true" />"
	type="text/css" />
<title>XeniT Move2Alf</title>
</head>
<body onLoad="putFocus(0,0);">
<div class="container">
<div class="span-24 last header">
<img src="<spring:url value="/images/move2alf-logo.png" htmlEscape="true" />" alt="Move2Alf" width=""/>
<%String roleCheck= "";%>
<c:forEach var="role" items="${roles}">
		<c:if test='${role.role=="SYSTEM_ADMIN"}'>
		<%roleCheck="systemAdmin"; %>
		</c:if>
		<%if(roleCheck=="consumer" ||roleCheck=="scheduleAdmin" || roleCheck==""){ %>
		<c:if test='${role.role=="JOB_ADMIN"}'>
		<%roleCheck="jobAdmin"; %>
		</c:if>
		<%}if(roleCheck=="consumer" || roleCheck==""){ %>
		<c:if test='${role.role=="SCHEDULE_ADMIN"}'>
		<%roleCheck="scheduleAdmin"; %>
		</c:if>
		<%}if(roleCheck==""){ %>
		<c:if test='${role.role=="CONSUMER"}'>
		<%roleCheck="consumer"; %>
		</c:if>
		<%}%>
</c:forEach>

	<p>
	<div>

	<div class="left">
<%if(roleCheck=="systemAdmin" || roleCheck=="jobAdmin" || roleCheck=="scheduleAdmin" || roleCheck=="consumer"){ %>
<a href="<spring:url value="/" htmlEscape="true"/>">Home</a> 
<%if(roleCheck=="systemAdmin" || roleCheck=="jobAdmin"){ %>
| <a href="<spring:url value="/destinations/" htmlEscape="true"/>">Manage destinations</a>
<%}if(roleCheck=="systemAdmin"){ %>
| <a href="<spring:url value="/users/" htmlEscape="true"/>">Manage users</a>
<%} %>
| 	<a href="<spring:url value="/user/profile/" htmlEscape="true"/>">My profile</a>

</div>

<div class="right">
Username: <sec:authentication property="principal.username" /> 

| <a href="<spring:url value="/j_spring_security_logout" htmlEscape="true"/>">Logout</a>
<%} %>
</div>
<div class="clear"></div>

</div></p>

</div>