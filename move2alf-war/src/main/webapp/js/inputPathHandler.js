function addInputPath() {
	document.getElementById('inputPathForm').style.display = 'block';
	document.getElementById('addInputPathButton').style.display = 'none';
}

function cancelInputPath() {
	document.getElementById('inputPathForm').style.display = 'none';
	document.getElementById('addInputPathButton').style.display = 'block';
}

function confirmInputPath() {
	document.getElementById('inputPathForm').style.display = 'none';
	document.getElementById('addInputPathButton').style.display = 'block';
}

function addRowToInputPath(form){

	  var tbl = document.getElementById('inputPathTable');
	  var lastRow = tbl.rows.length;
	  var iteration = lastRow+1;
	  var row = tbl.insertRow(lastRow);
	  
	  var cellFirst = row.insertCell(0);

	  var rowNumber = document.createElement('div');
		rowNumber.innerHTML = iteration;
		rowNumber.id = "inputPath" + iteration;
		
		cellFirst.appendChild(rowNumber);

		var cellSecond = row.insertCell(1);
		
		var parameter = document.createTextNode(form.inputPath.value);
		cellSecond.appendChild(parameter); 

		var cellThird = row.insertCell(2);
		  var sp = document.createElement('div');
		 
		  sp.className = 'pointer';
		  sp.innerHTML='remove';
		  var id = 'removeInputPath'+iteration;
		  sp.id=id;
		  sp.setAttribute('onclick', 'removeRowFromInputPath('+iteration+')');
		  cellThird.appendChild(sp);  
		  
		setInputPathInForm(form.inputPath.value);
	  /*
	  var rowNumber = document.createElement('div');
	   rowNumber.innerHTML=iteration;
	   rowNumber.id="inputPath"+iteration;
	   rowNumber.className="hide";
	  
	  cellFirst.appendChild(rowNumber);

	  var cellSecond = row.insertCell(1);
	  var fi = document.createElement('input');
		 var id = 'inputFolder'+iteration;
		 fi.id=id;
		 fi.type='text';
		 fi.name='inputFolder';
		 cellSecond.appendChild(fi);  
		
		 Spring.addDecoration(new Spring.ElementDecoration({
             elementId : id,
             widgetType : "dijit.form.ValidationTextBox",
             widgetAttrs : {
                required : true,
                invalidMessage : "Path cannot be empty",
                promptMessage : "Path cannot be empty"
                        	
             }
		 }));

	 
	  var cellThird = row.insertCell(2);
	  var sp = document.createElement('div');
	 
	  sp.className = 'pointer';
	  sp.innerHTML='remove';
	  var id = 'removeInputPath'+iteration;
	  sp.id=id;
	  sp.setAttribute('onclick', 'removeRowFromInputPath('+iteration+')');
	  cellThird.appendChild(sp);  
*/
}

function removeRowFromInputPath(row){

	  var tbl = document.getElementById('inputPathTable');
	  tbl.deleteRow(row-1);
	  adjustRowsInputPath(tbl,row);
	  
	  var tblFolder = document.getElementById('tblInputPath');
	  tblFolder.deleteRow(row-1);
	  adjustRowsInputFolder(tblFolder,row);
	  
}

function adjustRowsInputPath(tbl,row){
	var rowCount = tbl.rows.length;
	for(row; row<=rowCount; row++){
		var j=row+1;
		document.getElementById("inputPath"+j).setAttribute("id","inputPath"+row);
		document.getElementById("inputPath"+row).innerHTML=row;
		document.getElementById("removeInputPath"+j).setAttribute("id","removeInputPath"+row);
		document.getElementById("removeInputPath"+row).setAttribute('onclick', 'removeRowFromInputPath('+row+')');
	}
}

function adjustRowsInputFolder(tbl,row){
	var rowCount = tbl.rows.length;
	for(row; row<=rowCount; row++){
		var j=row+1;
		document.getElementById("inputFolder"+j).setAttribute("id","inputFolder"+row);
	}
}

function setInputPathInForm(inputValue){
	var tblParam = document.getElementById('tblInputPath');
	
	  var lastRow = tblParam.rows.length;

	  var iteration = lastRow+1;
	  var row = tblParam.insertRow(lastRow);
	  
	  var cellFirst = row.insertCell(0);

		try{
			var fi=document.createElement('<input name=\"inputFolder\" type=\"checkbox\" checked>');
			var id = 'inputFolder'+iteration;
			 fi.id=id;
			 fi.value=inputValue;
		}
		catch(e){
			 var fi = document.createElement('input');
			 var id = 'inputFolder'+iteration;
			 fi.id=id;
			 fi.type='checkbox';
			 fi.name='inputFolder';
			 fi.value=inputValue;
			 fi.checked=true;
		}
	  cellFirst.appendChild(fi);
}
