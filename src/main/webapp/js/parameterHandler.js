function addParameterMetadata() {
	document.getElementById('parameterMetadataForm').style.display = 'block';
	document.getElementById('addParameterMetadataButton').style.display = 'none';
}

function cancelParameterMetadata() {
	document.getElementById('parameterMetadataForm').style.display = 'none';
	document.getElementById('addParameterMetadataButton').style.display = 'block';
}

function confirmParameterMetadata() {
	document.getElementById('parameterMetadataForm').style.display = 'none';
	document.getElementById('addParameterMetadataButton').style.display = 'block';
}

function addRowToParameterMetadata(form) {
	var tbl = document.getElementById('paramMetadataTable');
	var lastRow = tbl.rows.length;

	var iteration = lastRow + 1;
	var row = tbl.insertRow(lastRow);

	var cellFirst = row.insertCell(0);

	var rowNumber = document.createElement('div');
	rowNumber.innerHTML = iteration;
	rowNumber.id = "rowNumberParamMetadata" + iteration;

	cellFirst.appendChild(rowNumber);

	var cellSecond = row.insertCell(1);
	var displayString = form.parameterMetadataName.value + " = \""
			+ form.parameterMetadataValue.value+"\"";
	
	var parameter = document.createTextNode(displayString);
	cellSecond.appendChild(parameter); 

	var cellThird = row.insertCell(2);

	var sp = document.createElement('div');

	sp.className = 'pointer';
	sp.innerHTML = 'remove';
	var id = 'removeParamMetadata' + iteration;
	sp.id = id;
	sp.setAttribute('onclick', 'removeRowFromParameterMetadata(' + iteration + ')');
	cellThird.appendChild(sp);
	setParamMetadataInForm(form.parameterMetadataName.value, form.parameterMetadataValue.value);
}

function removeRowFromParameterMetadata(row){

	  var tbl = document.getElementById('paramMetadataTable');
	  tbl.deleteRow(row-1);
	  adjustRowsParamsMetadata(tbl,row);

	  var tblParam = document.getElementById('tblParamMetadata');
	  tblParam.deleteRow(row-1);
	  
}

function adjustRowsParamsMetadata(tbl,row){
	var rowCount = tbl.rows.length;
	for(row; row<=rowCount; row++){
		var j=row+1;
		document.getElementById("rowNumberParamMetadata"+j).setAttribute("id","rowNumberParamMetadata"+row);
		document.getElementById("rowNumberParamMetadata"+row).innerHTML=row;
		document.getElementById("removeParamMetadata"+j).setAttribute("id","removeParamMetadata"+row);
		document.getElementById("removeParamMetadata"+row).setAttribute('onclick', 'removeRowFromParameterMetadata('+row+')');
	}
}

function setParamMetadataInForm(name, value){
	var tableValue=name+"|"+value;
	var tblParam = document.getElementById('tblParamMetadata');
	
	  var lastRow = tblParam.rows.length;

	  var iteration = lastRow+1;
	  var row = tblParam.insertRow(lastRow);
	  
	  var cellFirst = row.insertCell(0);

		try{
			var fi=document.createElement('<input name=\"paramMetadata\" type=\"checkbox\" checked>');
			var id = 'paramMetadata'+iteration;
			 fi.id=id;
			 fi.value=tableValue;
		}
		catch(e){
			 var fi = document.createElement('input');
			 var id = 'paramMetadata'+iteration;
			 fi.id=id;
			 fi.type='checkbox';
			 fi.name='paramMetadata';
			 fi.value=tableValue;
			 fi.checked=true;
		}
	  cellFirst.appendChild(fi);
}

function transformBox(state){
	if(state=="none"){
		document.getElementById('addParameterTransformButton').style.display='none';
		document.getElementById('paramTransformTable').style.display='none';
	}else{
		document.getElementById('addParameterTransformButton').style.display='block';
		document.getElementById('paramTransformTable').style.display='block';
	}
}

function addParameterTransform() {
	document.getElementById('parameterTransformForm').style.display = 'block';
	document.getElementById('addParameterTransformButton').style.display = 'none';
}

function cancelParameterTransform() {
	document.getElementById('parameterTransformForm').style.display = 'none';
	document.getElementById('addParameterTransformButton').style.display = 'block';
}

function confirmParameterTransform() {
	document.getElementById('parameterTransformForm').style.display = 'none';
	document.getElementById('addParameterTransformButton').style.display = 'block';
}

function addRowToParameterTransform(form) {
	var tbl = document.getElementById('paramTransformTable');
	var lastRow = tbl.rows.length;

	var iteration = lastRow + 1;
	var row = tbl.insertRow(lastRow);

	var cellFirst = row.insertCell(0);

	var rowNumber = document.createElement('div');
	rowNumber.innerHTML = iteration;
	rowNumber.id = "rowNumberParamTransform" + iteration;

	cellFirst.appendChild(rowNumber);

	var cellSecond = row.insertCell(1);
	var displayString = form.parameterTransformName.value + " = \""
			+ form.parameterTransformValue.value+"\"";
	
	var parameter = document.createTextNode(displayString);
	cellSecond.appendChild(parameter); 

	var cellThird = row.insertCell(2);

	var sp = document.createElement('div');

	sp.className = 'pointer';
	sp.innerHTML = 'remove';
	var id = 'removeParamTransform' + iteration;
	sp.id = id;
	sp.setAttribute('onclick', 'removeRowFromParameterTransform(' + iteration + ')');
	cellThird.appendChild(sp);
	setParamTransformInForm(form.parameterTransformName.value, form.parameterTransformValue.value);
}

function removeRowFromParameterTransform(row){

	  var tbl = document.getElementById('paramTransformTable');
	  tbl.deleteRow(row-1);
	  adjustRowsParamsTransform(tbl,row);

	  var tblParam = document.getElementById('tblParamTransform');
	  tblParam.deleteRow(row-1);
	  
}

function adjustRowsParamsTransform(tbl,row){
	var rowCount = tbl.rows.length;
	for(row; row<=rowCount; row++){
		var j=row+1;
		document.getElementById("rowNumberParamTransform"+j).setAttribute("id","rowNumberParamTransform"+row);
		document.getElementById("rowNumberParamTransform"+row).innerHTML=row;
		document.getElementById("removeParamTransform"+j).setAttribute("id","removeParamTransform"+row);
		document.getElementById("removeParamTransform"+row).setAttribute('onclick', 'removeRowFromParameterTransform('+row+')');
	}
}

function setParamTransformInForm(name, value){
	var tableValue=name+"|"+value;
	var tblParam = document.getElementById('tblParamTransform');
	
	  var lastRow = tblParam.rows.length;

	  var iteration = lastRow+1;
	  var row = tblParam.insertRow(lastRow);
	  
	  var cellFirst = row.insertCell(0);

		try{
			var fi=document.createElement('<input name=\"paramTransform\" type=\"checkbox\" checked>');
			var id = 'paramTransform'+iteration;
			 fi.id=id;
			 fi.value=tableValue;
		}
		catch(e){
			 var fi = document.createElement('input');
			 var id = 'paramTransform'+iteration;
			 fi.id=id;
			 fi.type='checkbox';
			 fi.name='paramTransform';
			 fi.value=tableValue;
			 fi.checked=true;
		}
	  cellFirst.appendChild(fi);
}