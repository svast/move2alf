function addRowToSchedule(){
	
	var cronJob =  getCron();
	if(cronJob){
		var index;
		if($("table#schedules tbody tr").size() == 0){
			index = 0;
		}else{
			index = parseInt($("table#schedules tbody tr:last").attr('id').split('_')[1])+1;
		}
		var scheduleId = "schedule_"+index;
		$("table#schedules tbody").append("" +
				"<tr id='"+scheduleId+"'>" +
				"	<td><img src='"+basePath+"/images/new.png' alt='new' /></td>" +
				"	<td class=\"cronexpr\">"+cronJob+"" +
				"		<input name=\"cron\" type=\"hidden\" value=\""+cronJob+"\" />" +
				"	</td>" +
				"	<td><img class=\"clickable\" onclick=\"$('#"+scheduleId+"').remove()\" src='"+basePath+"/images/delete-icon.png' alt='delete' /></td>" +
				"</tr>");
		$("table#schedules tbody tr:last").effect('pulsate', {}, 300);
	}
	
	
}

function getCron(){
	var scheduleType = $("input[name=scheduleType]:checked").val();
	switch(scheduleType){
	case 'singlerun':
		var date = $("input[name=singlerunDate]").datepicker('getDate');
		var hour = $("input[name=singlerunTime]").timepicker('getHour');
		var minute = $("input[name=singlerunTime]").timepicker('getMinute');
		return '0 '+minute+' '+hour+' '+date.getDate()+' '+(date.getMonth()+1)+' ? '+date.getFullYear();
		break;
	case 'hourly':
		var minute = $("input[name=hourlyMinutes]").timepicker('getMinute');
		return '0 '+minute+' * * * ?';
		break;
	case 'daily':
		var hour = $("input[name=dailyTime]").timepicker('getHour');
		var minute = $("input[name=dailyTime]").timepicker('getMinute');
		return '0 '+minute+' '+hour+' * * ?';
		break;
	case 'weekly':
		var day = $("select[name=weeklyDay]").val();
		var hour = $("input[name=weeklyTime]").timepicker('getHour');
		var minute = $("input[name=weeklyTime]").timepicker('getMinute');
		return '0 '+minute+' '+hour+' ? * '+day; 
		break;
	case 'advanced':
		return $("#cronJob").val();
		break;
	default:
		return false;
	}
}

function activateScheduleType(rowId){
	$("div.scheduleLine").addClass("inactive");
	$("div.scheduleLine input[type!=radio]").attr("disabled","");
	$("div.scheduleLine select").attr("disabled","");
	$("div#"+rowId).removeClass("inactive");
	$("div#"+rowId+" input").removeAttr("disabled");
	$("div#"+rowId+" select").removeAttr("disabled");
	$("#radio_"+rowId).prop('checked', true);

}

$(function(){
	$("input.datepicker").datepicker({dateFormat: 'dd/mm/yy' });
	$("input.timepicker").timepicker();
	$("input.minutepicker").timepicker({showHours: false});
});