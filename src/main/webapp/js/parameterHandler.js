function addParameter() {
	document.getElementById('parameterForm').style.display = 'block';
	document.getElementById('addParameterButton').style.display = 'none';
}

function cancelParameter() {
	document.getElementById('parameterForm').style.display = 'none';
	document.getElementById('addParameterButton').style.display = 'block';
}

function confirmParameter() {
	document.getElementById('parameterForm').style.display = 'none';
	document.getElementById('addParameterButton').style.display = 'block';
}

function addRowToParameter(form) {
	var tbl = document.getElementById('paramTable');
	var lastRow = tbl.rows.length;

	var iteration = lastRow + 1;
	var row = tbl.insertRow(lastRow);

	var cellFirst = row.insertCell(0);

	var rowNumber = document.createElement('div');
	rowNumber.innerHTML = iteration;
	rowNumber.id = "rowNumberParam" + iteration;

	cellFirst.appendChild(rowNumber);

	var cellSecond = row.insertCell(1);
	var displayString = form.parameterName.value + " = \""
			+ form.parameterValue.value+"\"";
	
	var parameter = document.createTextNode(displayString);
	cellSecond.appendChild(parameter); 

	var cellThird = row.insertCell(2);

	var sp = document.createElement('div');

	sp.className = 'pointer';
	sp.innerHTML = 'remove';
	var id = 'removeParam' + iteration;
	sp.id = id;
	sp.setAttribute('onclick', 'removeRowFromParameter(' + iteration + ')');
	cellThird.appendChild(sp);
	setParamInForm(form.parameterName.value, form.parameterValue.value);
}

function removeRowFromParameter(row){

	  var tbl = document.getElementById('paramTable');
	  tbl.deleteRow(row-1);
	  adjustRowsParams(tbl,row);

	  var tblParam = document.getElementById('tblParam');
	  tblParam.deleteRow(row-1);
	  
}

function adjustRowsParams(tbl,row){
	var rowCount = tbl.rows.length;
	for(row; row<=rowCount; row++){
		var j=row+1;
		document.getElementById("rowNumberParam"+j).setAttribute("id","rowNumberParam"+row);
		document.getElementById("rowNumberParam"+row).innerHTML=row;
		document.getElementById("removeParam"+j).setAttribute("id","removeParam"+row);
		document.getElementById("removeParam"+row).setAttribute('onclick', 'removeRowFromParameter('+row+')');
	}
}

function setParamInForm(name, value){
	var tableValue=name+"|"+value;
	var tblParam = document.getElementById('tblParam');
	
	  var lastRow = tblParam.rows.length;

	  var iteration = lastRow+1;
	  var row = tblParam.insertRow(lastRow);
	  
	  var cellFirst = row.insertCell(0);

		try{
			var fi=document.createElement('<input name=\"param\" type=\"checkbox\" checked>');
			var id = 'param'+iteration;
			 fi.id=id;
			 fi.value=tableValue;
		}
		catch(e){
			 var fi = document.createElement('input');
			 var id = 'param'+iteration;
			 fi.id=id;
			 fi.type='checkbox';
			 fi.name='param';
			 fi.value=tableValue;
			 fi.checked=true;
		}
	  cellFirst.appendChild(fi);
}