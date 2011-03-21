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