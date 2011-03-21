function showInput(){
	document.getElementById('destination').style.display='inline';
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