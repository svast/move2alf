function addInputPath(baseId, raw){
	//default field
	raw = typeof raw !== 'undefined' ? raw : false;
	
	var textBox = $("#"+baseId+"Textbox");
	var inputPath = textBox.val();
	
	if(inputPath == ""){
		if(!raw){
			alert("Input path should not be empty!");
		}
	}else{
		var tableId = baseId+"Table";
		if($("#"+tableId+" tbody tr").size() == 1){
			index = 0;
		}else{
			index = parseInt($("#"+tableId+" tbody tr").eq(-2).attr('id').split('_')[1])+1;
		}
		var inputId = baseId+"_"+index;
		
		$("#"+tableId+" tr:last").before("" +
				"<tr id=\""+inputId+"\">" +
				"	<td><img src='"+basePath+"/images/new.png' alt='new' /></td>" +
				"	<td>"+inputPath+"</td>" +
				"		<input name=\"inputFolder\" type=\"hidden\" value=\""+inputPath+"\" />" +
				"	<td><img class=\"clickable\" onclick=\"$('#"+inputId+"').remove()\" src='"+basePath+"/images/delete-icon.png' alt='delete' /></td>" +
				"</tr>");
		
		textBox.val('');
		
		$("#"+inputId).effect('pulsate', {}, 300);
	}
}