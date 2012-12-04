function addParameter(idbase, raw){
	//default value
	raw = typeof raw !== 'undefined' ? raw : false;
	
	var name = $.trim($("#"+idbase+"Name").val());
	var value = $.trim($("#"+idbase+"Value").val());
	
	if(name == "" || value== ""){
		if(!raw){
			alert("Name or value should not be empty!");
		}
	}
	else{
		var tableId = idbase+"Table";
		if($("#"+tableId+" tbody tr").size() == 1){
			index = 0;
		}else{
			index = parseInt($("#"+tableId+" tbody tr").eq(-2).attr('id').split('_')[1])+1;
		}
		var parameterId = idbase+"_"+index;
		
		$("#"+tableId+" tr:last").before("" +
				"<tr id=\""+parameterId+"\">" +
				"	<td><img src='"+basePath+"/images/new.png' alt='new' /></td>" +
				"	<td class=\"parameterName\">"+name+"</td>" +
				"	<td class=\"parameterValue\">"+value+"" +
						"<input name=\""+idbase+"\" type=\"hidden\" value=\""+name+"|"+value+"\" />" +
				"	</td>" +
				"	<td>" +
				"		<img class=\"clickable\" onclick=\"$('#"+parameterId+"').remove()\" src='"+basePath+"/images/delete-icon.png' alt='delete' />" +
				"	</td>" +
				"</tr>");
		
		$("#"+idbase+"Name").val('');
		$("#"+idbase+"Value").val('');
		
		$("#"+tableId+" tbody tr").eq(-2).effect('pulsate', {}, 300);
	}
}