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
	
	if(!document.getElementById('inputPath1')){
		var input = form.inputPath.value;
		
		if(""==input || null==input || "null"==input || "undefined"==input){
			document.getElementById("inputFolderError").style.display='block';
			validated=false;
		}else{
			confirmInputPath();
			addRowToInputPath(form);
		}
	}
	
	var destinationFolder=form.destinationFolder.value;
	if(""==destinationFolder || null==destinationFolder || "null"==destinationFolder || "undefined"==destinationFolder){
		document.getElementById("destinationFolderError").style.display='block';
		validated=false;
	}

	if(!document.getElementById('dest1')){
		var destValidation = destinationValidation(form);
		if(!destValidation){
			document.getElementById("destError").style.display='block';
			validated=false;
		}
	}

	if(!document.getElementById('rowNumber1')){
		confirmSchedule();
		addRowToSchedule(form);
		/*
		document.getElementById("cronError").style.display='block';
		validated=false;
		*/
	}
		return validated;
}

function createDestinationsValidation(form){
	var validated = true;

	if(!document.getElementById('sourceSink1')){
		var destValidation = destinationValidation(form);
		if(!destValidation){
			document.getElementById("destError").style.display='block';
			validated=false;
		}
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
		
		if(!document.editDestination){
			addRowToDestination(form);
		}
	}
	return validated;
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

function inputValidation(form){
	var input = form.inputPath.value;
	var validated = true; 
	
	if(""==input || null==input || "null"==input || "undefined"==input){
		document.getElementById("inputFolderError").style.display='block';
		validated=false;
	}else{
		confirmInputPath();
		addRowToInputPath(form);
	}	
	
	return validated;
}

function metadataValidation(form){
	var metadataName = form.parameterMetadataName.value;
	var metadataValue = form.parameterMetadataValue.value;
	var validated = true;
	
	if(""==metadataName || null==metadataName || "null"==metadataName || "undefined"==metadataName){
		document.getElementById("inputMetadataError").style.display='block';
		validated=false;
	}else{
		if(""==metadataValue || null==metadataValue || "null"==metadataValue || "undefined"==metadataValue){
			document.getElementById("parameterMetadataValue").value="";
		}
		confirmParameterMetadata();
		addRowToParameterMetadata(form);
	}
}

function transformValidation(form){
	var transformName = form.parameterTransformName.value;
	var transformValue = form.parameterTransformValue.value;
	var validated = true;
	
	if(""==transformName || null==transformName || "null"==transformName || "undefined"==transformName){
		document.getElementById("inputTransformError").style.display='block';
		validated=false;
	}else{
		if(""==transformValue || null==transformValue || "null"==transformValue || "undefined"==transformValue){
			document.getElementById("parameterTransformValue").value="";
		}
		confirmParameterTransform();
		addRowToParameterTransform(form);
	}
}